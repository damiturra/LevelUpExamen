package com.example.levelupgamer.viewmodel.admin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelupgamer.data.model.VendedorEntity
import com.example.levelupgamer.data.repository.VendedorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminVendUI(
    val isLoading: Boolean = false,
    val error: String? = null,
    val vendedores: List<VendedorEntity> = emptyList(),
    val editando: VendedorEntity? = null
)

class AdminVendedoresViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = VendedorRepository(app)
    private val _ui = MutableStateFlow(AdminVendUI())
    val ui: StateFlow<AdminVendUI> = _ui.asStateFlow()

    init {
        viewModelScope.launch {
            repo.observeAll().collect { list ->
                _ui.update { it.copy(vendedores = list, isLoading = false, error = null) }
            }
        }
    }

    fun nueva() { _ui.update { it.copy(editando = VendedorEntity(nombre = "", email = "", activo = true)) } }
    fun editar(v: VendedorEntity) { _ui.update { it.copy(editando = v) } }
    fun cancelar() { _ui.update { it.copy(editando = null) } }

    fun guardar(nombre: String, email: String, activo: Boolean) = viewModelScope.launch {
        val ed = _ui.value.editando ?: return@launch
        _ui.update { it.copy(isLoading = true, error = null) }
        try {
            if (ed.id == 0L) repo.insert(nombre, email, activo) else repo.update(ed.id, nombre, email, activo)
            _ui.update { it.copy(editando = null, isLoading = false) }
        } catch (e: Exception) {
            _ui.update { it.copy(isLoading = false, error = e.message) }
        }
    }

    fun eliminar(id: Long) = viewModelScope.launch {
        _ui.update { it.copy(isLoading = true, error = null) }
        try {
            repo.delete(id)
            _ui.update { it.copy(isLoading = false) }
        } catch (e: Exception) {
            _ui.update { it.copy(isLoading = false, error = e.message) }
        }
    }
}
