package com.example.levelupgamer.data.repository

import com.example.levelupgamer.data.dao.UsuarioDao
import com.example.levelupgamer.data.model.UsuarioEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UsuarioRepository(
    private val dao: UsuarioDao,
    private val io: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun existeEmail(email: String): Boolean = withContext(io) {
        dao.findByEmail(email.trim()) != null
    }

    suspend fun login(email: String, password: String): UsuarioEntity? = withContext(io) {
        dao.findByEmailAndPassword(email.trim(), password.trim())
    }

    suspend fun registrarUsuario(usuario: UsuarioEntity): Long = withContext(io) {
        dao.insert(usuario)
    }

    suspend fun getById(id: Int): UsuarioEntity? = withContext(io) {
        dao.findById(id)
    }

    suspend fun actualizar(usuario: UsuarioEntity) = withContext(io) {
        dao.update(usuario)
    }

    suspend fun findByEmail(email: String): UsuarioEntity? = withContext(io) {
        dao.findByEmail(email.trim())
    }
}
