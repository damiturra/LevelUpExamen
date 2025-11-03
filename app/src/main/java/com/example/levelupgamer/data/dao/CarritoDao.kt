package com.example.levelupgamer.data.dao

import androidx.room.*
import com.example.levelupgamer.data.model.ItemCarrito
import kotlinx.coroutines.flow.Flow

@Dao
interface CarritoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun agregarItemCarrito(item: ItemCarrito): Long

    @Update
    suspend fun actualizarItemCarrito(item: ItemCarrito)

    @Delete
    suspend fun eliminarItemCarrito(item: ItemCarrito)

    @Query("SELECT * FROM carrito WHERE usuarioId = :usuarioId")
    fun obtenerCarritoPorUsuario(usuarioId: Int): Flow<List<ItemCarrito>>

    @Query("SELECT * FROM carrito WHERE usuarioId = :usuarioId AND productoCodigo = :productoCodigo LIMIT 1")
    suspend fun obtenerItemCarrito(usuarioId: Int, productoCodigo: String): ItemCarrito?

    @Query("DELETE FROM carrito WHERE usuarioId = :usuarioId")
    suspend fun vaciarCarrito(usuarioId: Int)

    @Query("SELECT COUNT(*) FROM carrito WHERE usuarioId = :usuarioId")
    fun contarItemsCarrito(usuarioId: Int): Flow<Int>

    @Query("SELECT SUM(subtotal) FROM carrito WHERE usuarioId = :usuarioId")
    fun obtenerTotalCarrito(usuarioId: Int): Flow<Int?>

    @Query("UPDATE carrito SET cantidad = :cantidad, subtotal = :subtotal WHERE id = :itemId")
    suspend fun actualizarCantidad(itemId: Int, cantidad: Int, subtotal: Int)
}