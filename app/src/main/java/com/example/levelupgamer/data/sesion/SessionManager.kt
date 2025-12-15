package com.example.levelupgamer.data.session

import android.content.Context
import android.content.SharedPreferences
import com.example.levelupgamer.data.user.Role

object SessionManager {

    // ---- Estado en memoria ----
    var currentUserId: Int? = null
    var currentUserName: String? = null
    var currentUserEmail: String? = null
    var currentUserPassword: String? = null   // solo RAM
    var esDuoc: Boolean = false
    var role: Role = Role.USER
    var currentVendedorId: Long? = null

    // ---- Persistencia ----
    private const val PREFS_NAME = "levelup_session_prefs"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_ES_DUOC = "es_duoc"
    private const val KEY_ROLE = "role"
    private const val KEY_VENDEDOR_ID = "vendedor_id"

    private lateinit var prefs: SharedPreferences

    /**
     * Debe llamarse una vez al iniciar la app (por ejemplo desde AppNavigation).
     * Si hay una sesión guardada, se carga a los campos en memoria.
     */
    fun init(context: Context) {
        if (this::prefs.isInitialized) return  // ya inicializado

        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Cargar sesión previa si existía
        val storedUserId = prefs.getInt(KEY_USER_ID, -1)
        if (storedUserId != -1) {
            currentUserId = storedUserId
            currentUserName = prefs.getString(KEY_USER_NAME, null)
            currentUserEmail = prefs.getString(KEY_USER_EMAIL, null)
            esDuoc = prefs.getBoolean(KEY_ES_DUOC, false)
            val roleName = prefs.getString(KEY_ROLE, Role.USER.name) ?: Role.USER.name
            role = try {
                Role.valueOf(roleName)
            } catch (_: Exception) {
                Role.USER
            }
            val vendId = prefs.getLong(KEY_VENDEDOR_ID, -1L)
            currentVendedorId = if (vendId != -1L) vendId else null
        }
    }

    private fun persist() {
        // En tests unitarios prefs NO estará inicializado → no hacemos nada
        if (!this::prefs.isInitialized) return

        prefs.edit().apply {
            if (currentUserId != null) {
                putInt(KEY_USER_ID, currentUserId!!)
                putString(KEY_USER_NAME, currentUserName)
                putString(KEY_USER_EMAIL, currentUserEmail)
                putBoolean(KEY_ES_DUOC, esDuoc)
                putString(KEY_ROLE, role.name)
                putLong(KEY_VENDEDOR_ID, currentVendedorId ?: -1L)
            } else {
                // No hay usuario logeado → limpiamos
                remove(KEY_USER_ID)
                remove(KEY_USER_NAME)
                remove(KEY_USER_EMAIL)
                remove(KEY_ES_DUOC)
                remove(KEY_ROLE)
                remove(KEY_VENDEDOR_ID)
            }
        }.apply()
    }

    fun clear() {
        currentUserId = null
        currentUserName = null
        currentUserEmail = null
        currentUserPassword = null
        esDuoc = false
        role = Role.USER
        currentVendedorId = null

        // limpiar persistencia
        if (this::prefs.isInitialized) {
            prefs.edit().clear().apply()
        }
    }

    fun safeUserId(): Int = currentUserId ?: -1

    fun requireUserId(): Int =
        currentUserId ?: error("No hay usuario logeado en SessionManager.currentUserId")

    /**
     * Login exitoso: llenamos todos los datos de la sesión
     * y los persistimos.
     */
    fun loginSuccess(
        id: Int,
        nombre: String,
        email: String,
        password: String,
        esDuoc: Boolean,
        role: Role,
        vendedorId: Long? = null
    ) {
        currentUserId = id
        currentUserName = nombre
        currentUserEmail = email
        currentUserPassword = password    // solo RAM
        this.esDuoc = esDuoc
        this.role = role
        currentVendedorId = vendedorId

        persist()
    }

    /**
     * Descuento actual aplicado al usuario según su estado.
     * Aquí centralizamos la lógica del descuento DUOC.
     */
    fun currentDiscountPercentage(): Int {
        return if (esDuoc) 15 else 0   // por ejemplo 15% si esDuoc
    }
}
