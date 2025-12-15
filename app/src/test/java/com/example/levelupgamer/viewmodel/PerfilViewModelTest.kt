// app/src/test/java/com/example/levelupgamer/viewmodel/PerfilViewModelTest.kt
package com.example.levelupgamer.viewmodel

import com.example.levelupgamer.data.dao.UsuarioDao
import com.example.levelupgamer.data.model.UsuarioEntity
import com.example.levelupgamer.data.repository.UsuarioRepository
import com.example.levelupgamer.data.session.SessionManager
import com.example.levelupgamer.data.user.Role
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.*

@OptIn(ExperimentalCoroutinesApi::class)
class PerfilViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    // ---- DAO falso en memoria ----
    private class FakeUsuarioDao(
        private val storage: MutableList<UsuarioEntity>
    ) : UsuarioDao {
        override suspend fun findByEmail(email: String) =
            storage.firstOrNull { it.email.trim().lowercase() == email.trim().lowercase() }

        override suspend fun findByEmailAndPassword(email: String, password: String) =
            storage.firstOrNull {
                it.email.trim().lowercase() == email.trim().lowercase() &&
                        it.password.trim() == password.trim()
            }

        override suspend fun findById(id: Int) = storage.firstOrNull { it.id == id }

        override suspend fun insert(usuario: UsuarioEntity): Long {
            val i = storage.indexOfFirst { it.id == usuario.id }
            if (i >= 0) storage[i] = usuario else storage.add(usuario)
            return usuario.id.toLong()
        }

        override suspend fun insertAll(usuarios: List<UsuarioEntity>) { usuarios.forEach { insert(it) } }

        override suspend fun update(usuario: UsuarioEntity) {
            val i = storage.indexOfFirst { it.id == usuario.id }
            if (i >= 0) storage[i] = usuario else storage.add(usuario)
        }

        override suspend fun delete(usuario: UsuarioEntity) { storage.removeIf { it.id == usuario.id } }

        override suspend fun countAll(): Int = storage.size
    }

    private fun daoConBase(): Pair<UsuarioDao, MutableList<UsuarioEntity>> {
        val base = mutableListOf(
            UsuarioEntity(
                id = 10,
                nombre = "Patricia",
                email = "paty@duoc.cl",
                password = "123456",
                esDuoc = true,
                role = Role.USER.name,
                vendedorId = null,
                telefono = "999111222",
                direccion = "Pasaje Uno",
                numero = "123",
                comuna = "Maipú",
                region = "RM"
            )
        )
        return FakeUsuarioDao(base) to base
    }

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        // Mockear SIEMPRE antes de crear el VM (init{} llama cargar()).
        mockkObject(SessionManager)
        every { SessionManager.safeUserId() } returns 10
        every { SessionManager.currentUserPassword } returns "123456"
        every { SessionManager.currentUserName = any() } just Runs
        every { SessionManager.esDuoc = any() } just Runs
    }

    @AfterEach
    fun tearDown() {
        unmockkObject(SessionManager)
        Dispatchers.resetMain()
    }

    @Test
    fun `cargar llena el uiState con los datos del usuario`() = runTest(dispatcher) {
        val (dao, _) = daoConBase()
        // ⬇️ Usar el MISMO dispatcher del test dentro del repo
        val repo = UsuarioRepository(dao, dispatcher)

        val vm = PerfilViewModel(repo)
        advanceUntilIdle()

        val s = vm.uiState
        Assertions.assertEquals(10, s.id)
        Assertions.assertEquals("Patricia", s.nombre)
        Assertions.assertEquals("paty@duoc.cl", s.email)
        Assertions.assertTrue(s.esDuoc)
        Assertions.assertEquals(Role.USER, s.role)
        Assertions.assertEquals("999111222", s.telefono)
        Assertions.assertEquals("Pasaje Uno", s.direccion)
        Assertions.assertEquals("123", s.numero)
        Assertions.assertEquals("Maipú", s.comuna)
        Assertions.assertEquals("RM", s.region)
        Assertions.assertNull(s.error)
        Assertions.assertFalse(s.isLoading)
    }

    @Test
    fun `guardar persiste cambios y refresca sesion y uiState`() = runTest(dispatcher) {
        val (dao, base) = daoConBase()
        val repo = UsuarioRepository(dao, dispatcher)
        val vm = PerfilViewModel(repo)
        advanceUntilIdle()

        // Cambios
        vm.onNombreChange("Patricia Actualizada")
        vm.onTelefonoChange("987654321")
        vm.onDireccionChange("Calle Dos")
        vm.onNumeroChange("456")
        vm.onComunaChange("La Florida")
        vm.onRegionChange("RM")

        vm.guardar()
        advanceUntilIdle()

        // DB
        val enDb = base.first { it.id == 10 }
        Assertions.assertEquals("Patricia Actualizada", enDb.nombre)
        Assertions.assertEquals("987654321", enDb.telefono)
        Assertions.assertEquals("Calle Dos", enDb.direccion)
        Assertions.assertEquals("456", enDb.numero)
        Assertions.assertEquals("La Florida", enDb.comuna)
        Assertions.assertEquals("RM", enDb.region)

        // SessionManager
        verify { SessionManager.currentUserName = "Patricia Actualizada" }
        verify { SessionManager.esDuoc = true }

        // UI state refleja lo guardado
        val s = vm.uiState
        Assertions.assertEquals("Patricia Actualizada", s.nombre)
        Assertions.assertEquals("987654321", s.telefono)
        Assertions.assertEquals("Calle Dos", s.direccion)
        Assertions.assertEquals("456", s.numero)
        Assertions.assertEquals("La Florida", s.comuna)
        Assertions.assertEquals("RM", s.region)
        Assertions.assertTrue(s.savedOk)
        Assertions.assertNull(s.error)
        Assertions.assertFalse(s.isLoading)
    }
}
