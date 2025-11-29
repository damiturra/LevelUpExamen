package com.example.levelupgamer.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.levelupgamer.data.model.UsuariosManager
import com.example.levelupgamer.data.session.SessionManager
import com.example.levelupgamer.data.user.Role

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
        uiState = uiState.copy(email = value, error = null)
    }

    fun onPasswordChange(value: String) {
        uiState = uiState.copy(password = value, error = null)
    }

    /**
     * Login con callback que entrega también el rol.
     */
    fun hacerLogin(
        onSuccess: (nombreUsuario: String, esDuoc: Boolean, role: Role) -> Unit
    ) {
        if (uiState.email.isBlank() || uiState.password.isBlank()) {
            uiState = uiState.copy(error = "Completa todos los campos")
            return
        }

        uiState = uiState.copy(isLoading = true, error = null)

        val email = uiState.email.trim().lowercase()
        val pass  = uiState.password
        val usuario = UsuariosManager.login(email, pass)

        uiState = uiState.copy(isLoading = false)

        if (usuario != null) {
            // Guarda sesión global
            SessionManager.currentUserId = 1
            SessionManager.currentUserName = usuario.nombre
            SessionManager.esDuoc = usuario.esDuoc
            SessionManager.role = usuario.role
            SessionManager.currentVendedorId = usuario.vendedorId   // 👈 NUEVO

            onSuccess(usuario.nombre, usuario.esDuoc, usuario.role)
        } else {
            uiState = uiState.copy(error = "Credenciales incorrectas")
        }
    }

    // Versión antigua, por compatibilidad si la usas en alguna parte
    fun hacerLoginBasico(
        onSuccess: (nombreUsuario: String, esDuoc: Boolean) -> Unit
    ) {
        hacerLogin { nombre, esDuoc, _ -> onSuccess(nombre, esDuoc) }
    }
}
