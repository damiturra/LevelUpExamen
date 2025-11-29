package com.example.levelupgamer.viewmodel.vendedor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelupgamer.data.model.Producto
import com.example.levelupgamer.data.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class VendorProdUI(
    val vendedorId: Long,
    val isLoading: Boolean = false,
    val error: String? = null,
    val productos: List<Producto> = emptyList(),
    val editando: Producto? = null
)

class VendedorProductosViewModel(
    app: Application,
    private val repo: ProductoRepository,
    private val vendedorIdArg: Long
) : AndroidViewModel(app) {

    private val _ui = MutableStateFlow(VendorProdUI(vendedorId = vendedorIdArg))
    val ui: StateFlow<VendorProdUI> = _ui.asStateFlow()

    init {
        viewModelScope.launch {
            repo.observeByVendedor(vendedorIdArg)
                .catch { e -> _ui.update { it.copy(error = e.message) } }
                .collect { list ->
                    _ui.update { it.copy(productos = list, isLoading = false, error = null) }
                }
        }
    }

    fun nuevo() {
        _ui.update {
            it.copy(
                editando = Producto(
                    codigo = UUID.randomUUID().toString(),
                    nombre = "",
                    descripcion = "",
                    precio = 0,
                    categoriaId = 0,
                    categoriaNombre = "",
                    stock = 100,
                    imagenUrl = "",
                    fabricante = "",
                    calificacion = 0f,
                    vendedorId = vendedorIdArg
                )
            )
        }
    }

    fun editar(p: Producto) { _ui.update { it.copy(editando = p) } }
    fun cancelar() { _ui.update { it.copy(editando = null) } }

    fun guardar(
        nombre: String,
        descripcion: String,
        precio: Int,
        categoriaId: Int,
        categoriaNombre: String,
        activo: Boolean
    ) = viewModelScope.launch {
        val ed = _ui.value.editando ?: return@launch
        _ui.update { it.copy(isLoading = true, error = null) }
        try {
            val p = ed.copy(
                nombre = nombre.trim(),
                descripcion = descripcion.trim(),
                precio = precio,
                categoriaId = categoriaId,
                categoriaNombre = categoriaNombre.trim(),
                vendedorId = vendedorIdArg
            )
            if (_ui.value.productos.none { it.codigo == p.codigo }) repo.insert(p) else repo.update(p)
            _ui.update { it.copy(editando = null, isLoading = false) }
        } catch (e: Exception) {
            _ui.update { it.copy(isLoading = false, error = e.message) }
        }
    }

    fun eliminar(codigo: String) = viewModelScope.launch {
        _ui.update { it.copy(isLoading = true, error = null) }
        try {
            val existente = repo.getByCode(codigo) ?: return@launch
            repo.delete(existente)
            _ui.update { it.copy(isLoading = false) }
        } catch (e: Exception) {
            _ui.update { it.copy(isLoading = false, error = e.message) }
        }
    }
}