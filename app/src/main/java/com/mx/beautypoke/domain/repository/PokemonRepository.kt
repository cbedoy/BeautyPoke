package com.mx.beautypoke.domain.repository

import com.mx.beautypoke.domain.model.Pokemon

interface PokemonRepository {
    suspend fun getPokemonDetail(id: Int): Result<Pokemon>
}
