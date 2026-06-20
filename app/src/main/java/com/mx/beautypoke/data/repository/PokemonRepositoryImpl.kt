package com.mx.beautypoke.data.repository

import com.mx.beautypoke.data.remote.RetrofitClient
import com.mx.beautypoke.data.remote.mapper.PokemonMapper
import com.mx.beautypoke.domain.model.Pokemon
import com.mx.beautypoke.domain.repository.PokemonRepository

class PokemonRepositoryImpl : PokemonRepository {

    private val api = RetrofitClient.apiService
    private val mapper = PokemonMapper

    override suspend fun getPokemonDetail(id: Int): Result<Pokemon> {
        return runCatching {
            val pokemonResponse = api.getPokemon(id)
            val speciesResponse = api.getPokemonSpecies(id)
            mapper.mapToDomain(pokemonResponse, speciesResponse)
        }
    }
}
