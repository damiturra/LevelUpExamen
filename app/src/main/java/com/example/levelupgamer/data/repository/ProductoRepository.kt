package com.example.levelupgamer.data.repository

import com.example.levelupgamer.data.dao.CategoriaDao
import com.example.levelupgamer.data.dao.ProductoDao
import com.example.levelupgamer.data.model.Categoria
import com.example.levelupgamer.data.model.Producto
import kotlinx.coroutines.flow.Flow

class ProductoRepository(
    private val productoDao: ProductoDao,
    private val categoriaDao: CategoriaDao
) {

    fun obtenerTodosLosProductos(): Flow<List<Producto>> {
        return productoDao.obtenerTodosLosProductos()
    }

    suspend fun obtenerProductoPorCodigo(codigo: String): Producto? {
        return productoDao.obtenerProductoPorCodigo(codigo)
    }

    fun obtenerProductoPorCodigoFlow(codigo: String): Flow<Producto?> {
        return productoDao.obtenerProductoPorCodigoFlow(codigo)
    }

    fun obtenerProductosPorCategoria(categoriaId: Int): Flow<List<Producto>> {
        return productoDao.obtenerProductosPorCategoria(categoriaId)
    }

    fun buscarProductos(busqueda: String): Flow<List<Producto>> {
        return productoDao.buscarProductos(busqueda)
    }

    fun buscarProductosPorCategoria(categoriaId: Int, busqueda: String): Flow<List<Producto>> {
        return productoDao.buscarProductosPorCategoria(categoriaId, busqueda)
    }

    fun obtenerProductosOrdenadosPorPrecioAsc(): Flow<List<Producto>> {
        return productoDao.obtenerProductosOrdenadosPorPrecioAsc()
    }

    fun obtenerProductosOrdenadosPorPrecioDesc(): Flow<List<Producto>> {
        return productoDao.obtenerProductosOrdenadosPorPrecioDesc()
    }

    fun obtenerProductosOrdenadosPorCalificacion(): Flow<List<Producto>> {
        return productoDao.obtenerProductosOrdenadosPorCalificacion()
    }

    suspend fun insertarProducto(producto: Producto) {
        productoDao.insertarProducto(producto)
    }

    suspend fun actualizarProducto(producto: Producto) {
        productoDao.actualizarProducto(producto)
    }

    fun obtenerTodasLasCategorias(): Flow<List<Categoria>> {
        return categoriaDao.obtenerTodasLasCategorias()
    }

    suspend fun obtenerCategoriaPorId(id: Int): Categoria? {
        return categoriaDao.obtenerCategoriaPorId(id)
    }

    fun obtenerCategoriaPorIdFlow(id: Int): Flow<Categoria?> {
        return categoriaDao.obtenerCategoriaPorIdFlow(id)
    }
}