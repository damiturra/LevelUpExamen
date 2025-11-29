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
    // Productos
    fun obtenerTodosLosProductos(): Flow<List<Producto>> =
        productoDao.obtenerTodosLosProductos()

    suspend fun obtenerProductoPorCodigo(codigo: String): Producto? =
        productoDao.obtenerProductoPorCodigo(codigo)

    fun obtenerProductoPorCodigoFlow(codigo: String): Flow<Producto?> =
        productoDao.obtenerProductoPorCodigoFlow(codigo)

    fun obtenerProductosPorCategoria(categoriaId: Int): Flow<List<Producto>> =
        productoDao.obtenerProductosPorCategoria(categoriaId)

    fun buscarProductos(busqueda: String): Flow<List<Producto>> =
        productoDao.buscarProductos(busqueda)

    fun buscarProductosPorCategoria(categoriaId: Int, busqueda: String): Flow<List<Producto>> =
        productoDao.buscarProductosPorCategoria(categoriaId, busqueda)

    fun obtenerProductosOrdenadosPorPrecioAsc(): Flow<List<Producto>> =
        productoDao.obtenerProductosOrdenadosPorPrecioAsc()

    fun obtenerProductosOrdenadosPorPrecioDesc(): Flow<List<Producto>> =
        productoDao.obtenerProductosOrdenadosPorPrecioDesc()

    fun obtenerProductosOrdenadosPorCalificacion(): Flow<List<Producto>> =
        productoDao.obtenerProductosOrdenadosPorCalificacion()

    suspend fun insertarProducto(producto: Producto) = productoDao.insertarProducto(producto)
    suspend fun actualizarProducto(producto: Producto) = productoDao.actualizarProducto(producto)
    suspend fun eliminarProducto(producto: Producto) = productoDao.eliminarProducto(producto)

    // Aliases para el VM del vendedor
    fun observeByVendedor(vendedorId: Long): Flow<List<Producto>> = productoDao.observeByVendedor(vendedorId)
    suspend fun getByCode(codigo: String): Producto? = productoDao.obtenerProductoPorCodigo(codigo)
    suspend fun insert(p: Producto) = productoDao.insertarProducto(p)
    suspend fun update(p: Producto) = productoDao.actualizarProducto(p)
    suspend fun delete(p: Producto) = productoDao.eliminarProducto(p)

    // Categorías
    fun obtenerTodasLasCategorias(): Flow<List<Categoria>> = categoriaDao.obtenerTodasLasCategorias()
    suspend fun obtenerCategoriaPorId(id: Int): Categoria? = categoriaDao.obtenerCategoriaPorId(id)
    fun obtenerCategoriaPorIdFlow(id: Int): Flow<Categoria?> = categoriaDao.obtenerCategoriaPorIdFlow(id)
}
