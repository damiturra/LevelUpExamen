package com.example.levelupgamer.data.dao

import androidx.room.*
import com.example.levelupgamer.data.model.UsuarioEntity

@Dao
interface UsuarioDao {

    @Query(
        """
        SELECT * FROM usuarios
        WHERE LOWER(TRIM(email)) = LOWER(TRIM(:email))
        LIMIT 1
        """
    )
    suspend fun findByEmail(email: String): UsuarioEntity?

    @Query(
        """
        SELECT * FROM usuarios
        WHERE LOWER(TRIM(email)) = LOWER(TRIM(:email))
          AND TRIM(password) = TRIM(:password)
        LIMIT 1
        """
    )
    suspend fun findByEmailAndPassword(email: String, password: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun findById(id: Int): UsuarioEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(usuario: UsuarioEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(usuarios: List<UsuarioEntity>)

    @Update
    suspend fun update(usuario: UsuarioEntity)

    @Delete
    suspend fun delete(usuario: UsuarioEntity)

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun countAll(): Int
}
