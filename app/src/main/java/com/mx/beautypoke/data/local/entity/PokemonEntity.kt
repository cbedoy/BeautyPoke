package com.mx.beautypoke.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon")
data class PokemonEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String,
    val typesJson: String,
    val statsJson: String,
    val height: Int,
    val weight: Int,
    val description: String,
    val colorName: String,
    val category: String
)
