package com.example.levelupgamer.data.repository

import android.content.Context
import com.example.levelupgamer.data.database.AppDatabase
import com.example.levelupgamer.data.model.VendedorEntity
import kotlinx.coroutines.flow.Flow

class VendedorRepository(context: Context) {
    private val dao = AppDatabase.getDatabase(context).vendedorDao()

    fun observeAll(): Flow<List<VendedorEntity>> = dao.observeAll()
    suspend fun getById(id: Long) = dao.getById(id)
    suspend fun insert(nombre: String, email: String, activo: Boolean): Long =
        dao.insert(VendedorEntity(nombre = nombre.trim(), email = email.trim(), activo = activo))
    suspend fun update(id: Long, nombre: String, email: String, activo: Boolean) =
        dao.update(VendedorEntity(id = id, nombre = nombre.trim(), email = email.trim(), activo = activo))
    suspend fun delete(id: Long) = dao.delete(id)
}
