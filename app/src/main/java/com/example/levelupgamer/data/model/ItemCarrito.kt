package com.example.levelupgamer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "carrito")
data class ItemCarrito(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val usuarioId: Int,
    val productoCodigo: String,
    val productoNombre: String,
    val productoPrecio: Int,
    val cantidad: Int = 1,
    val subtotal: Int = productoPrecio * cantidad
) {
    fun calcularSubtotal(): Int {
        return productoPrecio * cantidad
    }
}

data class ResumenCarrito(
    val items: List<ItemCarrito>,
    val subtotal: Int,
    val descuentoPorcentaje: Int = 0,
    val descuentoMonto: Int = 0,
    val total: Int
) {
    companion object {
        fun calcular(items: List<ItemCarrito>, descuentoPorcentaje: Int = 0): ResumenCarrito {
            val subtotal = items.sumOf { it.calcularSubtotal() }
            val descuentoMonto = (subtotal * descuentoPorcentaje) / 100
            val total = subtotal - descuentoMonto

            return ResumenCarrito(
                items = items,
                subtotal = subtotal,
                descuentoPorcentaje = descuentoPorcentaje,
                descuentoMonto = descuentoMonto,
                total = total
            )
        }
    }
}