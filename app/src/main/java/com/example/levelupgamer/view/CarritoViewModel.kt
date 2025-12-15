package com.example.levelupgamer.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelupgamer.data.model.ItemCarrito
import com.example.levelupgamer.data.repository.CarritoRepository
import com.example.levelupgamer.data.repository.CompraRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import com.example.levelupgamer.data.model.CompraEntity
import com.example.levelupgamer.data.model.CompraItemEntity


data class ResumenCarrito(
    val subtotal: Int = 0,
    val descuentoPorcentaje: Int = 0,
    val descuentoMonto: Int = 0,
     // aca la prueba
    val ivaPorcentaje: Int = 19,
    val ivaMonto: Int = 0,
    val total: Int = 0
)

/**
 * ViewModel para manejar el carrito.
 * Depende de CarritoRepository y se inicializa con un userId (0 = invitado).
 */

class CarritoViewModel(
    private val repository: CarritoRepository,
    private val compraRepository: CompraRepository
) : ViewModel() {


    // ---- Estado principal ----
    private val _userId = MutableStateFlow(0)                // 0 = invitado
    private val _descPorcentaje = MutableStateFlow(0)        // p.ej. 20 si es DUOC

    /** Ítems del carrito observables */
    val items: StateFlow<List<ItemCarrito>> =
        _userId.flatMapLatest { uid ->
            if (uid <= 0) flowOf(emptyList())
            else repository.observeItems(uid).map { it ?: emptyList() }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Cantidad total (para badge) */
    val cantidadItems: StateFlow<Int> =
        _userId.flatMapLatest { uid ->
            if (uid <= 0) flowOf(0)
            else repository.observeCount(uid).map { it ?: 0 }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    /** Resumen (subtotal, descuentos, IVA, total) */
    val resumen: StateFlow<ResumenCarrito?> =
        combine(
            _userId.flatMapLatest { uid ->
                if (uid <= 0) flowOf(0)
                else repository.observeSubtotal(uid).map { it ?: 0 }
            },
            _descPorcentaje
        ) { subtotal, descPct ->
            calcularResumen(subtotal, descPct)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // ---- Setup ----
    /**
     * Debe llamarse al entrar a pantallas de carrito o detalle:
     * - userId: id del usuario logeado (0 si invitado)
     * - descuentoPorcentaje: p.ej. 20 para DUOC
     */
    fun inicializarCarrito(userId: Int?, descuentoPorcentaje: Int = 0) {
        _userId.value = (userId ?: 0).coerceAtLeast(0)
        _descPorcentaje.value = descuentoPorcentaje.coerceIn(0, 100)
    }

    // ---- Operaciones ----
    fun agregarProducto(producto: com.example.levelupgamer.data.model.Producto, cantidad: Int = 1) {
        val uid = _userId.value
        if (uid < 0) return

        viewModelScope.launch {
            val existente = repository.findItem(uid, producto.codigo)
            val nuevo = if (existente == null) {
                ItemCarrito(
                    userId = uid,
                    productoCodigo = producto.codigo,
                    productoNombre = producto.nombre,
                    productoPrecio = producto.precio,
                    cantidad = cantidad.coerceAtLeast(1)
                )
            } else {
                existente.copy(cantidad = (existente.cantidad + cantidad).coerceAtLeast(1))
            }
            repository.upsert(nuevo)
        }
    }

    fun incrementarCantidad(item: ItemCarrito) {
        viewModelScope.launch {
            repository.upsert(item.copy(cantidad = item.cantidad + 1))
        }
    }

    fun decrementarCantidad(item: ItemCarrito) {
        if (item.cantidad <= 1) return
        viewModelScope.launch {
            repository.upsert(item.copy(cantidad = item.cantidad - 1))
        }
    }

    fun eliminarItem(item: ItemCarrito) {
        viewModelScope.launch {
            repository.delete(item)
        }
    }

    /**
     * Simula el “checkout”: limpia el carrito y ejecuta un callback (volver atrás, etc.)
     */
    /**
     * Checkout:
     * 1) Toma snapshot de ítems + resumen.
     * 2) Registra la compra en historial.
     * 3) Limpia el carrito.
     */
    fun finalizarCompra(onDone: (() -> Unit)? = null) {
        val uid = _userId.value
        if (uid <= 0) {
            onDone?.invoke()
            return
        }

        viewModelScope.launch {
            // 1) Snapshot de ítems actuales
            val items = repository.getItemsSnapshot(uid)
            if (items.isEmpty()) {
                onDone?.invoke()
                return@launch
            }

            // 2) Obtener resumen actual (o recalcular por seguridad)
            val resumenActual = resumen.value ?: run {
                val subtotalCalc = items.sumOf { it.cantidad * it.productoPrecio }
                calcularResumen(subtotalCalc, _descPorcentaje.value)
            }

            // 3) Registrar compra en historial
            val compra = CompraEntity(
                userId = uid,
                subtotal = resumenActual.subtotal,
                descuentoPorcentaje = resumenActual.descuentoPorcentaje,
                descuentoMonto = resumenActual.descuentoMonto,
                ivaPorcentaje = resumenActual.ivaPorcentaje,
                ivaMonto = resumenActual.ivaMonto,
                total = resumenActual.total
            )

            val itemsCompra = items.map {
                CompraItemEntity(
                    compraId = 0, // se rellena en el DAO
                    productoCodigo = it.productoCodigo,
                    productoNombre = it.productoNombre,
                    productoPrecio = it.productoPrecio,
                    cantidad = it.cantidad
                )
            }

            compraRepository.registrarCompra(compra, itemsCompra)

            // 4) Limpiar carrito
            repository.clear(uid)

            onDone?.invoke()
        }
    }


    // ---- Helpers ----
    private fun calcularResumen(subtotal: Int, descuentoPorcentaje: Int): ResumenCarrito {
        val descMonto = ((subtotal * (descuentoPorcentaje / 100.0)).roundToInt()).coerceAtLeast(0)
        val base = (subtotal - descMonto).coerceAtLeast(0)
        val ivaPct = 19
        val ivaMonto = ((base * (ivaPct / 100.0)).roundToInt()).coerceAtLeast(0)
        val total = base + ivaMonto

        return ResumenCarrito(
            subtotal = subtotal,
            descuentoPorcentaje = descuentoPorcentaje,
            descuentoMonto = descMonto,
             //aca la prueba
            ivaPorcentaje = ivaPct,
            ivaMonto = ivaMonto,
            total = total
        )
    }
}
