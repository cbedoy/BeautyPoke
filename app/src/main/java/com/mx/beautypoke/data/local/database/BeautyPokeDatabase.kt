package com.mx.beautypoke.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mx.beautypoke.data.local.dao.PokemonDao
import com.mx.beautypoke.data.local.entity.PokemonEntity

@Database(entities = [PokemonEntity::class], version = 1, exportSchema = false)
abstract class BeautyPokeDatabase : RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao
}
