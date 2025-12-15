package com.example.levelupgamer.navigation

object Routes {
    const val LOGIN = "login"
    const val REGISTRO = "registro"

    // Home usuario con parámetro obligatorio {username}
    const val HOME_USR_PATTERN = "homeUsuario/{username}"
    fun homeUsuario(username: String) = "homeUsuario/$username"

    // Home vendedor con parámetro obligatorio {vendedorId}
    const val HOME_VEND_PATTERN = "homeVendedor/{vendedorId}"
    fun homeVendedor(id: Long) = "homeVendedor/$id"

    // Home admin/supervisor sin parámetro
    const val HOME_SUP = "homeSupervisor"
}
