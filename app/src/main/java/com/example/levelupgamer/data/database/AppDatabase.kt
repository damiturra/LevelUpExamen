package com.example.levelupgamer.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration
import com.example.levelupgamer.data.dao.CarritoDao
import com.example.levelupgamer.data.dao.CategoriaDao
import com.example.levelupgamer.data.dao.ProductoDao
import com.example.levelupgamer.data.dao.VendedorDao
import com.example.levelupgamer.data.model.Categoria
import com.example.levelupgamer.data.model.ItemCarrito
import com.example.levelupgamer.data.model.Producto
import com.example.levelupgamer.data.model.VendedorEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Producto::class,
        Categoria::class,
        ItemCarrito::class,
        VendedorEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productoDao(): ProductoDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun carritoDao(): CarritoDao
    abstract fun vendedorDao(): VendedorDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // MIGRACIÓN 1 -> 2: agrega columna vendedorId y crea índice único en codigo
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1) Nueva columna vendedorId
                db.execSQL("ALTER TABLE productos ADD COLUMN vendedorId INTEGER NOT NULL DEFAULT 0")
                // 2) Índice único en código
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_productos_codigo ON productos(codigo)")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "levelupgamer_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(SeedIfEmptyCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Seed sólo si las tablas están vacías (sin usar replayCache)
        private class SeedIfEmptyCallback : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val categoriaDao = database.categoriaDao()
                        val productoDao = database.productoDao()
                        val vendedorDao = database.vendedorDao()

                        if (categoriaDao.contarCategorias() == 0) {
                            categoriaDao.insertarCategorias(Categoria.obtenerCategoriasDefault())
                        }
                        if (productoDao.contarProductos() == 0) {
                            productoDao.insertarProductos(Producto.obtenerProductosDefault())
                        }
                        if (vendedorDao.contarVendedores() == 0) {
                            vendedorDao.insert(
                                VendedorEntity(
                                    nombre = "Vendedor Demo",
                                    email = "demo@levelupgamer.cl",
                                    activo = true
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
