package com.mx.beautypoke.data.repository

import com.mx.beautypoke.data.remote.api.PokemonApiService
import com.mx.beautypoke.data.remote.mapper.PokemonMapper
import com.mx.beautypoke.domain.model.Pokemon
import com.mx.beautypoke.domain.repository.PokemonRepository

class PokemonRepositoryImpl(
    private val api: PokemonApiService
) : PokemonRepository {

    private val mapper = PokemonMapper

    override suspend fun getPokemonDetail(id: Int): Result<Pokemon> {
        return runCatching {
            val pokemonResponse = api.getPokemon(id)
            val speciesResponse = api.getPokemonSpecies(id)
            mapper.mapToDomain(pokemonResponse, speciesResponse)
        }
    }
}
