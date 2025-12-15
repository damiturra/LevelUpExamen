// UsuarioEntity.kt
package com.example.levelupgamer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Usuario persistido en Room.
 * El campo role se guarda como String: "USER", "VENDEDOR" o "ADMIN".
 */
@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val email: String,
    val password: String,
    val esDuoc: Boolean,
    val role: String,
    val vendedorId: Long? = null,   // solo se usa si es VENDEDOR

    // NUEVOS CAMPOS DE PERFIL
    //aca prueba 1
    val telefono: String? = null,
    val direccion: String? = null,
    val numero: String? = null,
    val comuna: String? = null,
    val region: String? = null
    //aca podriamos campos nuevos
)
