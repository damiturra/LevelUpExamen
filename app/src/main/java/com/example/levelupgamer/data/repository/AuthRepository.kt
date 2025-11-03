package com.example.levelupgamer.data.repository

import com.example.levelupgamer.data.dao.UsuarioDao
import com.example.levelupgamer.data.model.Usuario
import kotlinx.coroutines.flow.Flow

class AuthRepository(private val usuarioDao: UsuarioDao) {

    suspend fun login(email: String, password: String): Usuario? {
        return usuarioDao.login(email, password)
    }

    suspend fun registrarUsuario(
        nombre: String,
        apellido: String,
        email: String,
        password: String,
        fechaNacimiento: String,
        telefono: String,
        direccion: String
    ): RegistroResult {
        val edad = Usuario.calcularEdad(fechaNacimiento)
        if (edad < 18) {
            return RegistroResult.Error("Debes ser mayor de 18 años para registrarte")
        }

        if (usuarioDao.existeEmail(email) > 0) {
            return RegistroResult.Error("El correo electrónico ya está registrado")
        }

        val esDuoc = Usuario.esDuocEmail(email)
        val descuento = if (esDuoc) 20 else 0

        val usuario = Usuario(
            nombre = nombre,
            apellido = apellido,
            email = email,
            password = password,
            fechaNacimiento = fechaNacimiento,
            edad = edad,
            telefono = telefono,
            direccion = direccion,
            esDuoc = esDuoc,
            descuento = descuento
        )

        val usuarioId = usuarioDao.insertarUsuario(usuario)
        return RegistroResult.Success(usuarioId.toInt(), esDuoc, descuento)
    }

    suspend fun obtenerUsuarioPorId(id: Int): Usuario? {
        return usuarioDao.obtenerUsuarioPorId(id)
    }

    fun obtenerUsuarioPorIdFlow(id: Int): Flow<Usuario?> {
        return usuarioDao.obtenerUsuarioPorIdFlow(id)
    }

    suspend fun actualizarUsuario(usuario: Usuario) {
        usuarioDao.actualizarUsuario(usuario)
    }

    fun obtenerTodosLosUsuarios(): Flow<List<Usuario>> {
        return usuarioDao.obtenerTodosLosUsuarios()
    }
}

sealed class RegistroResult {
    data class Success(val usuarioId: Int, val esDuoc: Boolean, val descuento: Int) : RegistroResult()
    data class Error(val mensaje: String) : RegistroResult()
}