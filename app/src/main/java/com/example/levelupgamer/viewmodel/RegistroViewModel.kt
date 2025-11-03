package com.example.levelupgamer.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.levelupgamer.data.model.ResultadoRegistro
import com.example.levelupgamer.data.model.UsuariosManager

data class RegistroUiState(
    val nombre: String = "",
    val email: String = "",
    val password: String = "",
    val fechaNacimiento: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val mensajeExito: String? = null
)

class RegistroViewModel : ViewModel() {

    var uiState by mutableStateOf(RegistroUiState())
        private set

    fun onNombreChange(value: String) {
        uiState = uiState.copy(nombre = value, error = null, mensajeExito = null)
    }

    fun onEmailChange(value: String) {
        uiState = uiState.copy(email = value, error = null, mensajeExito = null)
    }

    fun onPasswordChange(value: String) {
        uiState = uiState.copy(password = value, error = null, mensajeExito = null)
    }

    fun onFechaNacimientoChange(value: String) {
        uiState = uiState.copy(fechaNacimiento = value, error = null, mensajeExito = null)
    }

    fun registrar(
        onSuccess: (nombreUsuario: String, esDuoc: Boolean) -> Unit
    ) {
        // Validación básica
        if (uiState.nombre.isBlank() ||
            uiState.email.isBlank() ||
            uiState.password.isBlank() ||
            uiState.fechaNacimiento.isBlank()
        ) {
            uiState = uiState.copy(error = "Completa todos los campos")
            return
        }

        uiState = uiState.copy(isLoading = true, error = null, mensajeExito = null)

        // Persistencia local usando UsuariosManager (en memoria)
        when (val resultado = UsuariosManager.registrarUsuario(
            nombre = uiState.nombre.trim(),
            email = uiState.email.trim(),
            password = uiState.password,
            fechaNacimiento = uiState.fechaNacimiento.trim()
        )) {
            is ResultadoRegistro.Error -> {
                uiState = uiState.copy(
                    isLoading = false,
                    error = resultado.mensaje
                )
            }
            is ResultadoRegistro.Exito -> {
                uiState = uiState.copy(
                    isLoading = false,
                    error = null,
                    mensajeExito = "Registro exitoso${if (resultado.esDuoc) " - Usuario Duoc (20% desc.)" else ""}"
                )
                onSuccess(resultado.usuario.nombre, resultado.esDuoc)
            }
        }
    }
}
