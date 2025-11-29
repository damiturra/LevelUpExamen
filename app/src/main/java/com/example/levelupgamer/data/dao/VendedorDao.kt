package com.example.levelupgamer.data.dao

import androidx.room.*
import com.example.levelupgamer.data.model.VendedorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VendedorDao {
    @Query("SELECT * FROM vendedores ORDER BY nombre ASC")
    fun observeAll(): Flow<List<VendedorEntity>>

    @Query("SELECT * FROM vendedores WHERE id = :id")
    suspend fun getById(id: Long): VendedorEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(v: VendedorEntity): Long

    @Update
    suspend fun update(v: VendedorEntity)

    @Query("DELETE FROM vendedores WHERE id = :id")
    suspend fun delete(id: Long)

    // ✅ para seed sin usar replayCache
    @Query("SELECT COUNT(*) FROM vendedores")
    suspend fun contarVendedores(): Int
}
