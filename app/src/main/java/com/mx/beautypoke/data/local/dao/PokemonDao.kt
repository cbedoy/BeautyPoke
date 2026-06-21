package com.mx.beautypoke.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mx.beautypoke.data.local.entity.PokemonEntity

@Dao
interface PokemonDao {

    @Upsert
    suspend fun upsert(pokemon: PokemonEntity)

    @Upsert
    suspend fun upsertAll(pokemonList: List<PokemonEntity>)

    @Query("SELECT * FROM pokemon WHERE id = :id")
    suspend fun getById(id: Int): PokemonEntity?

    @Query("SELECT * FROM pokemon WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<Int>): List<PokemonEntity>

    @Query("SELECT id FROM pokemon")
    suspend fun getAllCachedIds(): List<Int>
}
