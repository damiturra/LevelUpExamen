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

data class PerfilUiState(
    val id: Int = -1,
    val nombre: String = "",
    val email: String = "",
    val esDuoc: Boolean = false,
    val role: Role = Role.USER,
    val vendedorId: Long? = null,

    // CRUD de perfil
    //aca prueba 2
    val telefono: String = "",
    val direccion: String = "",
    val numero: String = "",
    val comuna: String = "",
    val region: String = "",


    val isLoading: Boolean = false,
    val error: String? = null,
    val savedOk: Boolean = false
)

class PerfilViewModel(
    private val repo: UsuarioRepository
) : ViewModel() {

    var uiState by mutableStateOf(PerfilUiState())
        private set

    // Password real en RAM (no se expone a la UI)
    private var cachedPassword: String? = null

    init { cargar() }

    /** Carga datos del usuario logeado desde Room */
    fun cargar() {
        val uid = SessionManager.safeUserId()
        if (uid <= 0) {
            uiState = uiState.copy(error = "No hay sesión iniciada")
            return
        }
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null, savedOk = false)
            val u = repo.getById(uid)
            if (u == null) {
                uiState = uiState.copy(isLoading = false, error = "Usuario no encontrado")
                return@launch
            }
            cachedPassword = u.password
            val role = runCatching { Role.valueOf(u.role) }.getOrDefault(Role.USER)
            uiState = uiState.copy(
                isLoading = false,
                id = u.id,
                nombre = u.nombre,
                email = u.email,
                esDuoc = u.esDuoc,
                role = role,
                vendedorId = u.vendedorId,
                //aca prueba 3
                telefono = u.telefono.orEmpty(),
                direccion = u.direccion.orEmpty(),
                numero = u.numero.orEmpty(),
                comuna = u.comuna.orEmpty(),
                region = u.region.orEmpty()
            )
        }
    }

    /** Alias para pantallas antiguas */
    fun cargarUsuario() = cargar()

    // Bindings
    fun onNombreChange(v: String)     { uiState = uiState.copy(nombre = v, savedOk = false) }
    fun onTelefonoChange(v: String)   { uiState = uiState.copy(telefono = v, savedOk = false) }
    fun onDireccionChange(v: String)  { uiState = uiState.copy(direccion = v, savedOk = false) }
    fun onNumeroChange(v: String)     { uiState = uiState.copy(numero = v, savedOk = false) }
    fun onComunaChange(v: String)     { uiState = uiState.copy(comuna = v, savedOk = false) }
    fun onRegionChange(v: String)     { uiState = uiState.copy(region = v, savedOk = false) }

    //aca prueba 4





    /** Guarda cambios en Room y refresca SessionManager (nombre / esDuoc) */
    fun guardar() {
        viewModelScope.launch {
            try {
                // 1) loading ON
                uiState = uiState.copy(isLoading = true, error = null, savedOk = false)

                // 2) snapshot para evitar sobrescribir cambios
                val u = uiState
                val passwordSegura = SessionManager.currentUserPassword ?: cachedPassword.orEmpty()

                // 3) construimos la entidad con LO QUE HAY EN UI
                val entity = UsuarioEntity(
                    id = u.id,
                    nombre = u.nombre.trim(),
                    email = u.email,                 // email fijo
                    password = passwordSegura,
                    esDuoc = u.esDuoc,
                    role = u.role.name,
                    vendedorId = u.vendedorId,
                    //aca prueba 5
                    telefono = u.telefono.ifBlank { null },
                    direccion = u.direccion.ifBlank { null },
                    numero = u.numero.ifBlank { null },
                    comuna = u.comuna.ifBlank { null },
                    region = u.region.ifBlank { null }
                )

                // 4) persistimos
                repo.actualizar(entity)

                // 5) refrescamos sesión visible
                SessionManager.currentUserName = entity.nombre
                SessionManager.esDuoc = entity.esDuoc

                // 6) actualizar uiState con lo guardado
                uiState = u.copy(
                    isLoading = false,
                    savedOk = true,
                    telefono = entity.telefono.orEmpty(),
                    direccion = entity.direccion.orEmpty(),
                    numero   = entity.numero.orEmpty(),
                    comuna   = entity.comuna.orEmpty(),
                    region   = entity.region.orEmpty(),
                      //aca prueba 6
                )
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, error = "No se pudo guardar: ${e.message}")
            }
        }
    }

    /** Alias para pantallas antiguas */
    fun guardarPerfil() = guardar()
}
