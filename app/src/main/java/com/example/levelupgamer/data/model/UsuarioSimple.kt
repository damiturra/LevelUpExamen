package com.example.levelupgamer.data.model

import com.example.levelupgamer.data.user.Role

data class UsuarioSimple(
    val nombre: String,
    val email: String,
    val password: String,
    val esDuoc: Boolean = false,
    val role: Role = Role.USER,
    val vendedorId: Long? = null   // para la ruta homeVendedor/{vendedorId}
)

sealed class ResultadoRegistro {
    data class Exito(val usuario: UsuarioSimple, val esDuoc: Boolean) : ResultadoRegistro()
    data class Error(val mensaje: String) : ResultadoRegistro()
}
