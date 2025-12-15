package com.example.levelupgamer.data.model

import androidx.room.*

/**
 * Cabecera de la compra (boleta).
 */
@Entity(
    tableName = "compras",
    indices = [Index("userId")]
)
data class CompraEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: Int,
    val fechaMillis: Long = System.currentTimeMillis(),
    val subtotal: Int,
    val descuentoPorcentaje: Int,
    val descuentoMonto: Int,
    val ivaPorcentaje: Int,
    val ivaMonto: Int,
    val total: Int
)

/**
 * Ítems asociados a una compra.
 */
@Entity(
    tableName = "compra_items",
    indices = [Index("compraId"), Index("productoCodigo")]
)
data class CompraItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val compraId: Long,
    val productoCodigo: String,
    val productoNombre: String,
    val productoPrecio: Int,
    val cantidad: Int
)

/**
 * Relación Compra + Items para mostrar en el historial.
 */
data class CompraConItems(
    @Embedded val compra: CompraEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "compraId"
    )
    val items: List<CompraItemEntity>
)
