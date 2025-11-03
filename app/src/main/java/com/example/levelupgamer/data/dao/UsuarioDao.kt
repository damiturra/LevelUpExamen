package com.example.levelupgamer.data.dao

import androidx.room.*
import com.example.levelupgamer.data.model.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuario(usuario: Usuario): Long

    @Update
    suspend fun actualizarUsuario(usuario: Usuario)

    @Delete
    suspend fun eliminarUsuario(usuario: Usuario)

    @Query("SELECT * FROM usuarios WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun obtenerUsuarioPorEmail(email: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun obtenerUsuarioPorId(id: Int): Usuario?

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    fun obtenerUsuarioPorIdFlow(id: Int): Flow<Usuario?>

    @Query("SELECT COUNT(*) FROM usuarios WHERE email = :email")
    suspend fun existeEmail(email: String): Int

    @Query("SELECT * FROM usuarios")
    fun obtenerTodosLosUsuarios(): Flow<List<Usuario>>
}