package com.example.levelupgamer.data.repository

import com.example.levelupgamer.data.dao.CompraDao
import com.example.levelupgamer.data.model.CompraEntity
import com.example.levelupgamer.data.model.CompraItemEntity
import com.example.levelupgamer.data.model.CompraConItems
import kotlinx.coroutines.flow.Flow

class CompraRepository(
    private val dao: CompraDao
) {
    fun observeHistorial(userId: Int): Flow<List<CompraConItems>> =
        dao.observeHistorial(userId)

    suspend fun registrarCompra(
        compra: CompraEntity,
        items: List<CompraItemEntity>
    ) = dao.registrarCompra(compra, items)
}
