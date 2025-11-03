package com.example.levelupgamer.viewmodel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.levelupgamer.data.model.UsuariosManager

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class LoginViewModel : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun onEmailChange(value: String) {
        uiState = uiState.copy(
            email = value,
            error = null
        )
    }

    fun onPasswordChange(value: String) {
        uiState = uiState.copy(
            password = value,
            error = null
        )
    }

    fun hacerLogin(
        onSuccess: (nombreUsuario: String, esDuoc: Boolean) -> Unit
    ) {
        if (uiState.email.isBlank() || uiState.password.isBlank()) {
            uiState = uiState.copy(error = "Completa todos los campos")
            return
        }

        uiState = uiState.copy(isLoading = true, error = null)

        // Persistencia local: usamos UsuariosManager (en memoria, sin BD ni nube)
        val usuario = UsuariosManager.login(
            uiState.email.trim(),
            uiState.password
        )

        uiState = uiState.copy(isLoading = false)

        if (usuario != null) {
            onSuccess(usuario.nombre, usuario.esDuoc)
        } else {
            uiState = uiState.copy(error = "Credenciales incorrectas")
        }
    }
}
