package com.example.levelupgamer.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelupgamer.data.model.ItemCarrito
import com.example.levelupgamer.data.model.Producto
import com.example.levelupgamer.data.model.ResumenCarrito
import com.example.levelupgamer.data.repository.CarritoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CarritoViewModel(
    private val repository: CarritoRepository
) : ViewModel() {

    private val _items = MutableStateFlow<List<ItemCarrito>>(emptyList())
    val items: StateFlow<List<ItemCarrito>> = _items.asStateFlow()

    private val _resumen = MutableStateFlow<ResumenCarrito?>(null)
    val resumen: StateFlow<ResumenCarrito?> = _resumen.asStateFlow()

    private val _cantidadItems = MutableStateFlow(0)
    val cantidadItems: StateFlow<Int> = _cantidadItems.asStateFlow()

    var carritoState by mutableStateOf(CarritoState())
        private set

    private var usuarioIdActual: Int = 0
    private var descuentoPorcentaje: Int = 0

    fun inicializarCarrito(usuarioId: Int, descuento: Int) {
        usuarioIdActual = usuarioId
        descuentoPorcentaje = descuento
        cargarCarrito()
    }

    private fun cargarCarrito() {
        viewModelScope.launch {
            repository.obtenerCarritoPorUsuario(usuarioIdActual).collect { items ->
                _items.value = items
                _cantidadItems.value = items.size
                calcularResumen(items)
            }
        }
    }

    private fun calcularResumen(items: List<ItemCarrito>) {
        _resumen.value = ResumenCarrito.calcular(items, descuentoPorcentaje)
    }

    fun agregarProducto(producto: Producto, cantidad: Int = 1) {
        viewModelScope.launch {
            carritoState = carritoState.copy(isLoading = true, mensaje = null)

            val resultado = repository.agregarProductoAlCarrito(
                usuarioId = usuarioIdActual,
                producto = producto,
                cantidad = cantidad
            )

            val mensaje = when (resultado) {
                is com.example.levelupgamer.data.repository.AgregarCarritoResult.Agregado ->
                    "Producto agregado al carrito"
                is com.example.levelupgamer.data.repository.AgregarCarritoResult.ActualizadoCantidad ->
                    "Cantidad actualizada a ${resultado.nuevaCantidad}"
            }

            carritoState = carritoState.copy(
                isLoading = false,
                mensaje = mensaje,
                mostrarMensaje = true
            )
        }
    }

    fun actualizarCantidad(item: ItemCarrito, nuevaCantidad: Int) {
        if (nuevaCantidad <= 0) {
            eliminarItem(item)
            return
        }

        viewModelScope.launch {
            repository.actualizarCantidad(
                itemId = item.id,
                cantidad = nuevaCantidad,
                precio = item.productoPrecio
            )
        }
    }

    fun incrementarCantidad(item: ItemCarrito) {
        actualizarCantidad(item, item.cantidad + 1)
    }

    fun decrementarCantidad(item: ItemCarrito) {
        actualizarCantidad(item, item.cantidad - 1)
    }

    fun eliminarItem(item: ItemCarrito) {
        viewModelScope.launch {
            repository.eliminarItemCarrito(item)
            carritoState = carritoState.copy(
                mensaje = "Producto eliminado del carrito",
                mostrarMensaje = true
            )
        }
    }

    fun vaciarCarrito() {
        viewModelScope.launch {
            repository.vaciarCarrito(usuarioIdActual)
            carritoState = carritoState.copy(
                mensaje = "Carrito vaciado",
                mostrarMensaje = true
            )
        }
    }

    fun ocultarMensaje() {
        carritoState = carritoState.copy(mostrarMensaje = false, mensaje = null)
    }

    fun finalizarCompra(onSuccess: () -> Unit) {
        if (_items.value.isEmpty()) {
            carritoState = carritoState.copy(
                error = "El carrito está vacío",
                mostrarMensaje = true
            )
            return
        }

        viewModelScope.launch {
            carritoState = carritoState.copy(isLoading = true)

            kotlinx.coroutines.delay(1000)

            repository.vaciarCarrito(usuarioIdActual)

            carritoState = carritoState.copy(
                isLoading = false,
                mensaje = "¡Compra realizada con éxito!",
                mostrarMensaje = true
            )

            onSuccess()
        }
    }
}

data class CarritoState(
    val isLoading: Boolean = false,
    val mensaje: String? = null,
    val error: String? = null,
    val mostrarMensaje: Boolean = false
)