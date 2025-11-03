package com.example.levelupgamer.data.model

object UsuariosManager {
    private val usuarios = mutableListOf<UsuarioSimple>()

    init {
        // Usuario de prueba pre-cargado
        usuarios.add(
            UsuarioSimple(
                nombre = "Juan",
                email = "juan@test.com",
                password = "123456"
            )
        )
        // Usuario Duoc de prueba
        usuarios.add(
            UsuarioSimple(
                nombre = "María",
                email = "maria@duoc.cl",
                password = "123456"
            )
        )
    }

    fun registrarUsuario(
        nombre: String,
        email: String,
        password: String,
        fechaNacimiento: String
    ): ResultadoRegistro {
        // Validar edad
        val edad = calcularEdad(fechaNacimiento)
        if (edad < 18) {
            return ResultadoRegistro.Error("Debes ser mayor de 18 años")
        }

        // Verificar si el email ya existe
        if (usuarios.any { it.email == email }) {
            return ResultadoRegistro.Error("El correo ya está registrado")
        }

        // Verificar si es correo Duoc
        val esDuoc = email.endsWith("@duoc.cl") || email.endsWith("@duocuc.cl")

        // Crear y agregar usuario
        val nuevoUsuario = UsuarioSimple(
            nombre = nombre,
            email = email,
            password = password,
            esDuoc = esDuoc
        )
        usuarios.add(nuevoUsuario)

        return ResultadoRegistro.Exito(nuevoUsuario, esDuoc)
    }

    fun login(email: String, password: String): UsuarioSimple? {
        return usuarios.find { it.email == email && it.password == password }
    }

    private fun calcularEdad(fechaNacimiento: String): Int {
        try {
            val partes = fechaNacimiento.split("/")
            if (partes.size != 3) return 0

            val dia = partes[0].toIntOrNull() ?: return 0
            val mes = partes[1].toIntOrNull() ?: return 0
            val anio = partes[2].toIntOrNull() ?: return 0

            val anioActual = 2024
            var edad = anioActual - anio

            val mesActual = 11
            val diaActual = 3

            if (mes > mesActual || (mes == mesActual && dia > diaActual)) {
                edad--
            }

            return edad
        } catch (e: Exception) {
            return 0
        }
    }
}

data class UsuarioSimple(
    val nombre: String,
    val email: String,
    val password: String,
    val esDuoc: Boolean = false
)

sealed class ResultadoRegistro {
    data class Exito(val usuario: UsuarioSimple, val esDuoc: Boolean) : ResultadoRegistro()
    data class Error(val mensaje: String) : ResultadoRegistro()
}