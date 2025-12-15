package com.example.levelupgamer.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelupgamer.data.repository.UsuarioRepository
import com.example.levelupgamer.data.session.SessionManager
import com.example.levelupgamer.data.user.Role
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class LoginViewModel(
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun onEmailChange(v: String) { uiState = uiState.copy(email = v) }
    fun onPasswordChange(v: String) { uiState = uiState.copy(password = v) }

    fun login(onSuccess: () -> Unit) {
        val email = uiState.email.trim().lowercase()
        val pass = uiState.password

        if (email.isBlank() || pass.isBlank()) {
            uiState = uiState.copy(error = "Ingresa correo y contraseña")
            return
        }

        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true, error = null)

                val user = usuarioRepository.login(email, pass)
                if (user == null) {
                    uiState = uiState.copy(isLoading = false, error = "Credenciales inválidas")
                    return@launch
                }

                val role = try { Role.valueOf(user.role) } catch (_: Exception) { Role.USER }

                SessionManager.loginSuccess(
                    id = user.id,
                    nombre = user.nombre,
                    email = user.email,
                    password = user.password,
                    esDuoc = user.esDuoc,
                    role = role,
                    vendedorId = user.vendedorId
                )

                uiState = uiState.copy(isLoading = false)
                onSuccess()
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, error = "No se pudo iniciar sesión: ${e.message}")
            }
        }
    }
}
