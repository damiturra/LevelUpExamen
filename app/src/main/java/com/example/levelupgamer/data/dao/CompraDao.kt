package com.example.levelupgamer.data.dao

import androidx.room.*
import com.example.levelupgamer.data.model.CompraEntity
import com.example.levelupgamer.data.model.CompraItemEntity
import com.example.levelupgamer.data.model.CompraConItems
import kotlinx.coroutines.flow.Flow

@Dao
interface CompraDao {

    // Historial de un usuario con sus items
    @Transaction
    @Query("SELECT * FROM compras WHERE userId = :userId ORDER BY fechaMillis DESC")
    fun observeHistorial(userId: Int): Flow<List<CompraConItems>>

    @Insert
    suspend fun insertCompra(compra: CompraEntity): Long

    @Insert
    suspend fun insertItems(items: List<CompraItemEntity>)

    // Operación conveniente: registrar compra + items en una transacción
    @Transaction
    suspend fun registrarCompra(compra: CompraEntity, items: List<CompraItemEntity>) {
        val compraId = insertCompra(compra)
        val conId = items.map { it.copy(compraId = compraId) }
        insertItems(conId)
    }
}
