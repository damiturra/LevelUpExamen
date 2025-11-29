package com.example.levelupgamer.data.session

import com.example.levelupgamer.data.user.Role

object SessionManager {
    var currentUserId: Int = 1
    var currentUserName: String = "Invitado"
    var esDuoc: Boolean = false
    var role: Role = Role.USER
    var currentVendedorId: Long? = null    // 👈 NUEVO
}
