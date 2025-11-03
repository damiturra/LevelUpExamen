package com.example.levelupgamer.data.dao

import androidx.room.*
import com.example.levelupgamer.data.model.Categoria
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCategoria(categoria: Categoria)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCategorias(categorias: List<Categoria>)

    @Update
    suspend fun actualizarCategoria(categoria: Categoria)

    @Delete
    suspend fun eliminarCategoria(categoria: Categoria)

    @Query("SELECT * FROM categorias ORDER BY nombre ASC")
    fun obtenerTodasLasCategorias(): Flow<List<Categoria>>

    @Query("SELECT * FROM categorias WHERE id = :id LIMIT 1")
    suspend fun obtenerCategoriaPorId(id: Int): Categoria?

    @Query("SELECT * FROM categorias WHERE id = :id LIMIT 1")
    fun obtenerCategoriaPorIdFlow(id: Int): Flow<Categoria?>

    @Query("SELECT COUNT(*) FROM categorias")
    suspend fun contarCategorias(): Int
}