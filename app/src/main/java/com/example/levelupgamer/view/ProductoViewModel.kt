package com.example.levelupgamer.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelupgamer.data.model.Categoria
import com.example.levelupgamer.data.model.Producto
import com.example.levelupgamer.data.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductoViewModel(
    private val repository: ProductoRepository
) : ViewModel() {

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias.asStateFlow()

    private val _productoSeleccionado = MutableStateFlow<Producto?>(null)
    val productoSeleccionado: StateFlow<Producto?> = _productoSeleccionado.asStateFlow()

    var filtroState by mutableStateOf(FiltroState())
        private set

    init {
        cargarCategorias()
        cargarProductos()
    }

    private fun cargarCategorias() {
        viewModelScope.launch {
            repository.obtenerTodasLasCategorias().collect { categorias ->
                _categorias.value = categorias
            }
        }
    }

    private fun cargarProductos() {
        viewModelScope.launch {
            repository.obtenerTodosLosProductos().collect { productos ->
                _productos.value = productos
            }
        }
    }

    fun cargarProductosPorCategoria(categoriaId: Int) {
        viewModelScope.launch {
            repository.obtenerProductosPorCategoria(categoriaId).collect { productos ->
                _productos.value = productos
            }
        }
    }

    fun buscarProductos(busqueda: String) {
        if (busqueda.isBlank()) {
            cargarProductos()
            return
        }

        viewModelScope.launch {
            repository.buscarProductos(busqueda).collect { productos ->
                _productos.value = productos
            }
        }
    }

    fun ordenarProductosPorPrecioAsc() {
        viewModelScope.launch {
            repository.obtenerProductosOrdenadosPorPrecioAsc().collect { productos ->
                _productos.value = productos
            }
        }
    }

    fun ordenarProductosPorPrecioDesc() {
        viewModelScope.launch {
            repository.obtenerProductosOrdenadosPorPrecioDesc().collect { productos ->
                _productos.value = productos
            }
        }
    }

    fun ordenarProductosPorCalificacion() {
        viewModelScope.launch {
            repository.obtenerProductosOrdenadosPorCalificacion().collect { productos ->
                _productos.value = productos
            }
        }
    }

    fun seleccionarProducto(codigo: String) {
        viewModelScope.launch {
            repository.obtenerProductoPorCodigoFlow(codigo).collect { producto ->
                _productoSeleccionado.value = producto
            }
        }
    }

    fun onBusquedaChange(value: String) {
        filtroState = filtroState.copy(busqueda = value)
        buscarProductos(value)
    }

    fun onCategoriaSeleccionadaChange(categoriaId: Int?) {
        filtroState = filtroState.copy(categoriaSeleccionada = categoriaId)
        if (categoriaId != null) {
            cargarProductosPorCategoria(categoriaId)
        } else {
            cargarProductos()
        }
    }

    fun onOrdenChange(orden: OrdenProductos) {
        filtroState = filtroState.copy(orden = orden)
        when (orden) {
            OrdenProductos.PRECIO_ASC -> ordenarProductosPorPrecioAsc()
            OrdenProductos.PRECIO_DESC -> ordenarProductosPorPrecioDesc()
            OrdenProductos.CALIFICACION -> ordenarProductosPorCalificacion()
            OrdenProductos.NINGUNO -> cargarProductos()
        }
    }

    fun limpiarFiltros() {
        filtroState = FiltroState()
        cargarProductos()
    }
}

data class FiltroState(
    val busqueda: String = "",
    val categoriaSeleccionada: Int? = null,
    val orden: OrdenProductos = OrdenProductos.NINGUNO
)

enum class OrdenProductos {
    NINGUNO,
    PRECIO_ASC,
    PRECIO_DESC,
    CALIFICACION
}