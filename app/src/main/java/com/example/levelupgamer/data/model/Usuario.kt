package com.example.levelupgamer.data.model

import com.example.levelupgamer.data.user.Role

object UsuariosManager {
    private val usuarios = mutableListOf<UsuarioSimple>()

    init {
        // Usuarios DUOC (USER)
        usuarios.add(
            UsuarioSimple(
                nombre = "Damian",
                email = "damian@duoc.cl",
                password = "123456",
                esDuoc = true,
                role = Role.USER
            )
        )
        usuarios.add(
            UsuarioSimple(
                nombre = "Jean Piere",
                email = "jean@duoc.cl",
                password = "123456",
                esDuoc = true,
                role = Role.USER
            )
        )

        // Vendedores (ahora con vendedorId)
        usuarios.add(
            UsuarioSimple(
                nombre = "Damian Vendedor",
                email = "damian@vendedor.cl",
                password = "123456",
                esDuoc = false,
                role = Role.VENDEDOR,
                vendedorId = 1L
            )
        )
        usuarios.add(
            UsuarioSimple(
                nombre = "Jean Vendedor",
                email = "jean@vendedor.cl",
                password = "123456",
                esDuoc = false,
                role = Role.VENDEDOR,
                vendedorId = 2L
            )
        )

        // Admin
        usuarios.add(
            UsuarioSimple(
                nombre = "Damian Admin",
                email = "damian@admin.cl",
                password = "123456",
                esDuoc = false,
                role = Role.ADMIN
            )
        )
        usuarios.add(
            UsuarioSimple(
                nombre = "Jean Admin",
                email = "jean@admin.cl",
                password = "123456",
                esDuoc = false,
                role = Role.ADMIN
            )
        )
    }

    fun registrarUsuario(
        nombre: String,
        email: String,
        password: String,
        fechaNacimiento: String
    ): ResultadoRegistro {
        val edad = calcularEdad(fechaNacimiento)
        if (edad < 18) return ResultadoRegistro.Error("Debes ser mayor de 18 años")

        if (usuarios.any { it.email.equals(email.trim(), ignoreCase = true) }) {
            return ResultadoRegistro.Error("El correo ya está registrado")
        }

        val esDuoc = email.endsWith("@duoc.cl", true) || email.endsWith("@duocuc.cl", true)
        val nuevoUsuario = UsuarioSimple(
            nombre = nombre,
            email = email.trim(),
            password = password,
            esDuoc = esDuoc,
            role = Role.USER,        // registro por defecto como USER
            vendedorId = null        // sólo setear si haces registro de VENDEDOR
        )
        usuarios.add(nuevoUsuario)
        return ResultadoRegistro.Exito(nuevoUsuario, esDuoc)
    }

    fun login(email: String, password: String): UsuarioSimple? {
        val norm = email.trim().lowercase()
        return usuarios.find { it.email.lowercase() == norm && it.password == password }
    }

    private fun calcularEdad(fechaNacimiento: String): Int = try {
        val (d, m, a) = fechaNacimiento.split("/").map { it.toInt() }
        val anioActual = 2025
        val mesActual = 11
        val diaActual = 29
        var edad = anioActual - a
        if (m > mesActual || (m == mesActual && d > diaActual)) edad--
        edad
    } catch (_: Exception) { 0 }
}
