package com.example.levelupgamer.repository

import com.example.levelupgamer.data.dao.CategoriaDao
import com.example.levelupgamer.data.dao.ProductoDao
import com.example.levelupgamer.data.model.Producto
import com.example.levelupgamer.data.repository.ProductoRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ProductoRepositoryTest {

    @Test
    fun `obtenerTodosLosProductos emite lista del dao`() = runTest {
        val productoDao = mockk<ProductoDao>()
        val categoriaDao = mockk<CategoriaDao>(relaxed = true)

        val lista = listOf(
            Producto(
                codigo = "P001",
                nombre = "Teclado gamer",
                descripcion = "Teclado mecánico RGB",
                precio = 29990,
                categoriaId = 1,
                categoriaNombre = "Periféricos",
                vendedorId = 1L,
                calificacion = 5.0f,
                stock = 10,                                // <- si no existe en tu data class, elimina esta línea
                imagenUrl = "https://example.com/p1.jpg"   // <- si es opcional, puedes omitirla
            ),
            Producto(
                codigo = "P002",
                nombre = "Mouse gamer",
                descripcion = "Mouse 7200 DPI",
                precio = 19990,
                categoriaId = 1,
                categoriaNombre = "Periféricos",
                vendedorId = 1L,
                calificacion = 4.5f,
                stock = 8,
                imagenUrl = "https://example.com/p2.jpg"
            )
        )

        every { productoDao.obtenerTodosLosProductos() } returns flowOf(lista)

        val repo = ProductoRepository(productoDao, categoriaDao)

        val recibida = repo.obtenerTodosLosProductos().first()
        assertEquals(lista, recibida)
    }

    @Test
    fun `obtenerProductoPorCodigo delega en dao`() = runTest {
        val productoDao = mockk<ProductoDao>()
        val categoriaDao = mockk<CategoriaDao>(relaxed = true)

        val p = Producto(
            codigo = "P001",
            nombre = "Teclado gamer",
            descripcion = "Teclado mecánico RGB",
            precio = 29990,
            categoriaId = 1,
            categoriaNombre = "Periféricos",
            vendedorId = 1L,
            calificacion = 5.0f,
            stock = 10,
            imagenUrl = "https://example.com/p1.jpg"
        )

        coEvery { productoDao.obtenerProductoPorCodigo("P001") } returns p

        val repo = ProductoRepository(productoDao, categoriaDao)

        val r = repo.obtenerProductoPorCodigo("P001")
        assertEquals(p, r)
    }
}
