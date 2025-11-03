package com.example.levelupgamer.data.repository

import com.example.levelupgamer.data.dao.CarritoDao
import com.example.levelupgamer.data.model.ItemCarrito
import com.example.levelupgamer.data.model.Producto
import kotlinx.coroutines.flow.Flow

class CarritoRepository(private val carritoDao: CarritoDao) {

    fun obtenerCarritoPorUsuario(usuarioId: Int): Flow<List<ItemCarrito>> {
        return carritoDao.obtenerCarritoPorUsuario(usuarioId)
    }

    suspend fun agregarProductoAlCarrito(
        usuarioId: Int,
        producto: Producto,
        cantidad: Int = 1
    ): AgregarCarritoResult {
        val itemExistente = carritoDao.obtenerItemCarrito(usuarioId, producto.codigo)

        return if (itemExistente != null) {
            val nuevaCantidad = itemExistente.cantidad + cantidad
            val nuevoSubtotal = producto.precio * nuevaCantidad

            carritoDao.actualizarCantidad(
                itemId = itemExistente.id,
                cantidad = nuevaCantidad,
                subtotal = nuevoSubtotal
            )
            AgregarCarritoResult.ActualizadoCantidad(nuevaCantidad)
        } else {
            val nuevoItem = ItemCarrito(
                usuarioId = usuarioId,
                productoCodigo = producto.codigo,
                productoNombre = producto.nombre,
                productoPrecio = producto.precio,
                cantidad = cantidad,
                subtotal = producto.precio * cantidad
            )
            carritoDao.agregarItemCarrito(nuevoItem)
            AgregarCarritoResult.Agregado
        }
    }

    suspend fun actualizarCantidad(itemId: Int, cantidad: Int, precio: Int) {
        val nuevoSubtotal = precio * cantidad
        carritoDao.actualizarCantidad(itemId, cantidad, nuevoSubtotal)
    }

    suspend fun eliminarItemCarrito(item: ItemCarrito) {
        carritoDao.eliminarItemCarrito(item)
    }

    suspend fun vaciarCarrito(usuarioId: Int) {
        carritoDao.vaciarCarrito(usuarioId)
    }

    fun contarItemsCarrito(usuarioId: Int): Flow<Int> {
        return carritoDao.contarItemsCarrito(usuarioId)
    }

    fun obtenerTotalCarrito(usuarioId: Int): Flow<Int?> {
        return carritoDao.obtenerTotalCarrito(usuarioId)
    }
}

sealed class AgregarCarritoResult {
    object Agregado : AgregarCarritoResult()
    data class ActualizadoCantidad(val nuevaCantidad: Int) : AgregarCarritoResult()
}