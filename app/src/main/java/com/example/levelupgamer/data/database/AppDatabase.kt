package com.example.levelupgamer.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
import com.example.levelupgamer.data.dao.UsuarioDao
import com.example.levelupgamer.data.model.UsuarioEntity
import com.example.levelupgamer.data.model.CompraEntity
import com.example.levelupgamer.data.model.CompraItemEntity
import com.example.levelupgamer.data.dao.CompraDao



@Database(
    entities = [
        Producto::class,
        Categoria::class,
        ItemCarrito::class,
        VendedorEntity::class,
        UsuarioEntity::class,
        CompraEntity::class,
        CompraItemEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productoDao(): ProductoDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun carritoDao(): CarritoDao
    abstract fun vendedorDao(): VendedorDao
    abstract fun usuarioDao(): UsuarioDao

    abstract fun compraDao(): CompraDao



    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // 1 -> 2: agrega vendedorId y crea índice único en codigo
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE productos ADD COLUMN vendedorId INTEGER NOT NULL DEFAULT 0")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_productos_codigo ON productos(codigo)")
            }
        }

        // 2 -> 3: agrega 'activo' (INTEGER 0/1)
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE productos ADD COLUMN activo INTEGER NOT NULL DEFAULT 1")
            }
        }

        // 3 -> 4: crea tabla usuarios
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS usuarios(
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                nombre TEXT NOT NULL,
                email TEXT NOT NULL,
                password TEXT NOT NULL,
                esDuoc INTEGER NOT NULL,
                role TEXT NOT NULL,
                vendedorId INTEGER
            )
            """.trimIndent()
                )
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_usuarios_email ON usuarios(email)"
                )
            }
        }
        // 4 -> 5: crea tablas compras y compra_items
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS compras(
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId INTEGER NOT NULL,
                        fechaMillis INTEGER NOT NULL,
                        subtotal INTEGER NOT NULL,
                        descuentoPorcentaje INTEGER NOT NULL,
                        descuentoMonto INTEGER NOT NULL,
                        ivaPorcentaje INTEGER NOT NULL,
                        ivaMonto INTEGER NOT NULL,
                        total INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS compra_items(
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        compraId INTEGER NOT NULL,
                        productoCodigo TEXT NOT NULL,
                        productoNombre TEXT NOT NULL,
                        productoPrecio INTEGER NOT NULL,
                        cantidad INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_compras_userId ON compras(userId)"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_compra_items_compraId ON compra_items(compraId)"
                )
            }
        }


        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "levelupgamer_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)//aca prueba 2

                    // .fallbackToDestructiveMigration() // <- solo si quieres wipe en desarrollo
                    .addCallback(SeedIfEmptyCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class SeedIfEmptyCallback : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                        val categoriaDao = database.categoriaDao()
                        val productoDao = database.productoDao()
                        val vendedorDao = database.vendedorDao()
                        val usuarioDao = database.usuarioDao()


                        if (vendedorDao.contarVendedores() == 0) {
                            vendedorDao.insert(
                                com.example.levelupgamer.data.model.VendedorEntity(
                                    nombre = "Vendedor Demo",
                                    email = "demo@levelupgamer.cl",
                                    activo = true
                                )
                            )
                        }
                        if (categoriaDao.contarCategorias() == 0) {
                            categoriaDao.insertarCategorias(
                                com.example.levelupgamer.data.model.Categoria.obtenerCategoriasDefault()
                            )
                        }
                        if (productoDao.contarProductos() == 0) {
                            // ⬇️ aquí estaba el problema
                            productoDao.upsertAll(
                                com.example.levelupgamer.data.model.Producto.obtenerProductosDefault()
                            )
                        }
                        // Usuarios demo: solo si no hay usuarios todavía
                        if (usuarioDao.countAll() == 0) {
                            usuarioDao.insertAll(
                                listOf(
                                    // Usuarios normales
                                    UsuarioEntity(
                                        nombre = "Damian Duoc",
                                        email = "damian@duoc.cl",
                                        password = "123456",
                                        esDuoc = true,
                                        role = "USER",
                                        vendedorId = null
                                    ),
                                    UsuarioEntity(
                                        nombre = "Jean Duoc",
                                        email = "jean@duoc.cl",
                                        password = "123456",
                                        esDuoc = true,
                                        role = "USER",
                                        vendedorId = null
                                    ),

                                    // Vendedores
                                    UsuarioEntity(
                                        nombre = "Damian Vendedor",
                                        email = "damian@vendedor.cl",
                                        password = "123456",
                                        esDuoc = false,
                                        role = "VENDEDOR",
                                        vendedorId = 1L
                                    ),
                                    UsuarioEntity(
                                        nombre = "Jean Vendedor",
                                        email = "jean@vendedor.cl",
                                        password = "123456",
                                        esDuoc = false,
                                        role = "VENDEDOR",
                                        vendedorId = 1L
                                    ),

                                    // Administradores
                                    UsuarioEntity(
                                        nombre = "Damian Admin",
                                        email = "damian@admin.cl",
                                        password = "123456",
                                        esDuoc = false,
                                        role = "ADMIN",
                                        vendedorId = null
                                    ),
                                    UsuarioEntity(
                                        nombre = "Jean Admin",
                                        email = "jean@admin.cl",
                                        password = "123456",
                                        esDuoc = false,
                                        role = "ADMIN",
                                        vendedorId = null
                                    )
                                )
                            )
                        }


                    }
                }
            }
        }
    }
}
