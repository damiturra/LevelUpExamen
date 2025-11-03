package com.example.levelupgamer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categorias")
data class Categoria(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val iconoNombre: String
) {
    companion object {
        fun obtenerCategoriasDefault(): List<Categoria> {
            return listOf(
                Categoria(1, "Juegos de Mesa", "Juegos de estrategia y diversión", "casino"),
                Categoria(2, "Accesorios", "Controladores, auriculares y más", "headset"),
                Categoria(3, "Consolas", "PlayStation, Xbox y más", "videogame_asset"),
                Categoria(4, "Computadores Gamers", "PCs de alto rendimiento", "computer"),
                Categoria(5, "Sillas Gamers", "Comodidad para largas sesiones", "event_seat"),
                Categoria(6, "Mouse", "Precisión y control", "mouse"),
                Categoria(7, "Mousepad", "Superficie perfecta para tu mouse", "crop_square"),
                Categoria(8, "Poleras Personalizadas", "Estilo gamer único", "checkroom"),
                Categoria(9, "Polerones Gamers", "Comodidad con estilo", "dry_cleaning")
            )
        }
    }
}