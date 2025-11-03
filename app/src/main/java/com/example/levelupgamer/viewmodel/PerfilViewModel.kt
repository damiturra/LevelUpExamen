package com.example.levelupgamer.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class PerfilUiState(
    val nombre: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val mensaje: String? = null
)

class PerfilViewModel : ViewModel() {

    var uiState by mutableStateOf(PerfilUiState())
        private set

    // Para setear el nombre inicial desde el username
    fun setNombreInicial(nombre: String) {
        if (uiState.nombre.isEmpty()) {        // solo la primera vez
            uiState = uiState.copy(nombre = nombre)
        }
    }

    fun onNombreChange(value: String) {
        uiState = uiState.copy(
            nombre = value,
            error = null,
            mensaje = null
        )
    }

    fun onTelefonoChange(value: String) {
        uiState = uiState.copy(
            telefono = value,
            error = null,
            mensaje = null
        )
    }

    fun onDireccionChange(value: String) {
        uiState = uiState.copy(
            direccion = value,
            error = null,
            mensaje = null
        )
    }

    fun guardarCambios() {
        // Validación simple
        if (uiState.nombre.isBlank() ||
            uiState.telefono.isBlank() ||
            uiState.direccion.isBlank()
        ) {
            uiState = uiState.copy(error = "Completa todos los campos")
            return
        }

        uiState = uiState.copy(
            error = null,
            mensaje = "Perfil actualizado"
        )
    }

    fun limpiarMensaje() {
        uiState = uiState.copy(mensaje = null)
    }
}
