package com.example.levelupgamer.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelupgamer.data.model.Usuario
import com.example.levelupgamer.data.repository.AuthRepository
import com.example.levelupgamer.data.repository.RegistroResult
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    var loginState by mutableStateOf(LoginState())
        private set

    var registroState by mutableStateOf(RegistroState())
        private set

    var usuarioActual by mutableStateOf<Usuario?>(null)
        private set

    fun onEmailChange(value: String) {
        loginState = loginState.copy(email = value, error = null)
    }

    fun onPasswordChange(value: String) {
        loginState = loginState.copy(password = value, error = null)
    }

    fun login(onSuccess: (Usuario) -> Unit) {
        viewModelScope.launch {
            loginState = loginState.copy(isLoading = true, error = null)

            val usuario = repository.login(
                email = loginState.email.trim(),
                password = loginState.password
            )

            if (usuario != null) {
                usuarioActual = usuario
                loginState = loginState.copy(isLoading = false)
                onSuccess(usuario)
            } else {
                loginState = loginState.copy(
                    isLoading = false,
                    error = "Credenciales incorrectas"
                )
            }
        }
    }

    fun onNombreChange(value: String) {
        registroState = registroState.copy(nombre = value, error = null)
    }

    fun onApellidoChange(value: String) {
        registroState = registroState.copy(apellido = value, error = null)
    }

    fun onEmailRegistroChange(value: String) {
        registroState = registroState.copy(email = value, error = null)
    }

    fun onPasswordRegistroChange(value: String) {
        registroState = registroState.copy(password = value, error = null)
    }

    fun onConfirmarPasswordChange(value: String) {
        registroState = registroState.copy(confirmarPassword = value, error = null)
    }

    fun onFechaNacimientoChange(value: String) {
        registroState = registroState.copy(fechaNacimiento = value, error = null)
    }

    fun onTelefonoChange(value: String) {
        registroState = registroState.copy(telefono = value, error = null)
    }

    fun onDireccionChange(value: String) {
        registroState = registroState.copy(direccion = value, error = null)
    }

    fun registrar(onSuccess: (Usuario, Boolean, Int) -> Unit) {
        viewModelScope.launch {
            if (registroState.password != registroState.confirmarPassword) {
                registroState = registroState.copy(error = "Las contraseñas no coinciden")
                return@launch
            }

            if (registroState.password.length < 6) {
                registroState = registroState.copy(error = "La contraseña debe tener al menos 6 caracteres")
                return@launch
            }

            registroState = registroState.copy(isLoading = true, error = null)

            val resultado = repository.registrarUsuario(
                nombre = registroState.nombre.trim(),
                apellido = registroState.apellido.trim(),
                email = registroState.email.trim(),
                password = registroState.password,
                fechaNacimiento = registroState.fechaNacimiento,
                telefono = registroState.telefono.trim(),
                direccion = registroState.direccion.trim()
            )

            when (resultado) {
                is RegistroResult.Success -> {
                    val usuario = repository.obtenerUsuarioPorId(resultado.usuarioId)
                    if (usuario != null) {
                        usuarioActual = usuario
                        registroState = registroState.copy(isLoading = false)
                        onSuccess(usuario, resultado.esDuoc, resultado.descuento)
                    }
                }
                is RegistroResult.Error -> {
                    registroState = registroState.copy(
                        isLoading = false,
                        error = resultado.mensaje
                    )
                }
            }
        }
    }

    fun cargarUsuario(usuarioId: Int) {
        viewModelScope.launch {
            val usuario = repository.obtenerUsuarioPorId(usuarioId)
            usuarioActual = usuario
        }
    }

    fun cerrarSesion() {
        usuarioActual = null
        loginState = LoginState()
        registroState = RegistroState()
    }
}

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

data class RegistroState(
    val nombre: String = "",
    val apellido: String = "",
    val email: String = "",
    val password: String = "",
    val confirmarPassword: String = "",
    val fechaNacimiento: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)