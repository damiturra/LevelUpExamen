package com.example.levelupgamer.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelupgamer.data.model.UsuarioEntity
import com.example.levelupgamer.data.repository.UsuarioRepository
import com.example.levelupgamer.data.session.SessionManager
import com.example.levelupgamer.data.user.Role
import kotlinx.coroutines.launch

data class RegistroUiState(
    val nombre: String = "",
    val email: String = "",
    val password: String = "",
    val esDuoc: Boolean = false,
    val role: Role = Role.USER,
    // lo guardamos como texto en el form y lo parseamos al guardar
    val vendedorIdText: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class RegistroViewModel(
    private val repo: UsuarioRepository
) : ViewModel() {

    var uiState by mutableStateOf(RegistroUiState())
        private set

    fun onNombreChange(v: String) { uiState = uiState.copy(nombre = v, error = null) }
    fun onEmailChange(v: String) { uiState = uiState.copy(email = v, error = null) }
    fun onPasswordChange(v: String) { uiState = uiState.copy(password = v, error = null) }
    fun onEsDuocChange(v: Boolean) { uiState = uiState.copy(esDuoc = v, error = null) }
    fun onRoleChange(v: Role) { uiState = uiState.copy(role = v, error = null) }
    fun onVendedorIdChange(v: String) { uiState = uiState.copy(vendedorIdText = v, error = null) }

    fun registrar(onSuccess: (String, Boolean, Role) -> Unit) {
        val nombre = uiState.nombre.trim()
        val email = uiState.email.trim().lowercase()
        val password = uiState.password
        val esDuoc = uiState.esDuoc
        val role = uiState.role

        if (nombre.isBlank() || email.isBlank() || password.isBlank()) {
            uiState = uiState.copy(error = "Completa nombre, correo y contraseña")
            return
        }

        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true, error = null)

                // 1) ¿Correo ya existe?
                if (repo.existeEmail(email)) {
                    uiState = uiState.copy(isLoading = false, error = "El correo ya está registrado")
                    return@launch
                }

                // 2) Parse vendedorId si aplica
                val vendedorId: Long? = if (role == Role.VENDEDOR) {
                    uiState.vendedorIdText.trim().takeIf { it.isNotEmpty() }?.toLongOrNull()
                } else null

                val entity = UsuarioEntity(
                    id = 0,
                    nombre = nombre,
                    email = email,
                    password = password,
                    esDuoc = esDuoc,
                    role = role.name,
                    vendedorId = vendedorId
                )

                val newId = repo.registrarUsuario(entity).toInt()

                // 3) Persistimos sesión
                SessionManager.loginSuccess(
                    id = newId,
                    nombre = nombre,
                    email = email,
                    password = password,
                    esDuoc = esDuoc,
                    role = role,
                    vendedorId = vendedorId
                )

                uiState = uiState.copy(isLoading = false)
                onSuccess(nombre, esDuoc, role)
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, error = "No se pudo registrar: ${e.message}")
            }
        }
    }
}
