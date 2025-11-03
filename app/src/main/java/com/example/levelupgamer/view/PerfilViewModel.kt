package com.example.levelupgamer.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelupgamer.data.model.Usuario
import com.example.levelupgamer.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PerfilViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario.asStateFlow()

    var perfilState by mutableStateOf(PerfilState())
        private set

    fun cargarPerfil(usuarioId: Int) {
        viewModelScope.launch {
            repository.obtenerUsuarioPorIdFlow(usuarioId).collect { usuario ->
                _usuario.value = usuario
                if (usuario != null) {
                    perfilState = perfilState.copy(
                        nombre = usuario.nombre,
                        apellido = usuario.apellido,
                        email = usuario.email,
                        telefono = usuario.telefono,
                        direccion = usuario.direccion,
                        fechaNacimiento = usuario.fechaNacimiento
                    )
                }
            }
        }
    }

    fun onNombreChange(value: String) {
        perfilState = perfilState.copy(nombre = value, error = null)
    }

    fun onApellidoChange(value: String) {
        perfilState = perfilState.copy(apellido = value, error = null)
    }

    fun onTelefonoChange(value: String) {
        perfilState = perfilState.copy(telefono = value, error = null)
    }

    fun onDireccionChange(value: String) {
        perfilState = perfilState.copy(direccion = value, error = null)
    }

    fun guardarCambios(onSuccess: () -> Unit) {
        val usuarioActual = _usuario.value ?: return

        viewModelScope.launch {
            perfilState = perfilState.copy(isLoading = true, error = null)

            val usuarioActualizado = usuarioActual.copy(
                nombre = perfilState.nombre.trim(),
                apellido = perfilState.apellido.trim(),
                telefono = perfilState.telefono.trim(),
                direccion = perfilState.direccion.trim()
            )

            repository.actualizarUsuario(usuarioActualizado)

            perfilState = perfilState.copy(
                isLoading = false,
                mensaje = "Perfil actualizado correctamente",
                mostrarMensaje = true
            )

            onSuccess()
        }
    }

    fun ocultarMensaje() {
        perfilState = perfilState.copy(mostrarMensaje = false, mensaje = null)
    }
}

data class PerfilState(
    val nombre: String = "",
    val apellido: String = "",
    val email: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val fechaNacimiento: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val mensaje: String? = null,
    val mostrarMensaje: Boolean = false
)