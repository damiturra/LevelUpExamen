package com.example.levelupgamer.data.repository

import android.content.Context
import com.example.levelupgamer.data.database.AppDatabase
import com.example.levelupgamer.data.model.Categoria
import kotlinx.coroutines.flow.Flow

class CategoriaRepository(context: Context) {
    private val dao = AppDatabase.getDatabase(context).categoriaDao()

    fun obtenerTodas(): Flow<List<Categoria>> = dao.obtenerTodasLasCategorias()
    suspend fun insertar(categoria: Categoria) = dao.insertarCategoria(categoria)
    suspend fun insertarTodas(items: List<Categoria>) = dao.insertarCategorias(items)
    suspend fun actualizar(categoria: Categoria) = dao.actualizarCategoria(categoria)
    suspend fun eliminar(categoria: Categoria) = dao.eliminarCategoria(categoria)
    suspend fun contar(): Int = dao.contarCategorias()
}
