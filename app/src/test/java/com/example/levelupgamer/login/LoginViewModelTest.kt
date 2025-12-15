package com.example.levelupgamer.login

import com.example.levelupgamer.data.model.UsuarioEntity
import com.example.levelupgamer.data.repository.UsuarioRepository
import com.example.levelupgamer.data.session.SessionManager
import com.example.levelupgamer.data.user.Role
import com.example.levelupgamer.viewmodel.LoginViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.*

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private lateinit var vm: LoginViewModel
    private lateinit var repo: UsuarioRepository
    private val dispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(dispatcher)
        SessionManager.clear()

        // Mock del repositorio (no usamos Room real en unit tests)
        repo = mockk(relaxed = true)

        vm = LoginViewModel(repo)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        SessionManager.clear()
    }

    @Test
    fun `login con campos vacios muestra error y no llama callback`() = runTest {
        var called = false

        vm.login { called = true }  // no seteamos email/password
        // No hay corutina en curso (retorna temprano por validación)

        Assertions.assertEquals(
            "Ingresa correo y contraseña",
            vm.uiState.error
        )
        Assertions.assertFalse(called)
        Assertions.assertNull(SessionManager.currentUserId)
    }

    @Test
    fun `login con credenciales validas actualiza sesion y llama callback`() = runTest {
        // Arrange: el repo devuelve un usuario válido (usar coEvery para suspend)
        val user = UsuarioEntity(
            id = 1,
            nombre = "Damian Duoc",
            email = "damian@duoc.cl",
            password = "123456",
            esDuoc = true,
            role = Role.USER.name,
            vendedorId = null
        )
        coEvery { repo.login("damian@duoc.cl", "123456") } returns user
        // (alternativa más laxa)
        // coEvery { repo.login(any(), any()) } returns user

        vm.onEmailChange("damian@duoc.cl")
        vm.onPasswordChange("123456")

        var called = false
        vm.login { called = true }

        // Avanzar corutinas del viewModelScope
        advanceUntilIdle()

        // Assert: callback + estado + session
        Assertions.assertTrue(called, "Debe ejecutarse el onSuccess()")
        Assertions.assertNull(vm.uiState.error)

        Assertions.assertEquals(1, SessionManager.currentUserId)
        Assertions.assertEquals("Damian Duoc", SessionManager.currentUserName)
        Assertions.assertEquals("damian@duoc.cl", SessionManager.currentUserEmail)
        Assertions.assertEquals("123456", SessionManager.currentUserPassword)
        Assertions.assertTrue(SessionManager.esDuoc)
        Assertions.assertEquals(Role.USER, SessionManager.role)
    }
}
