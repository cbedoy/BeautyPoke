package com.mx.beautypoke.data.repository

import com.mx.beautypoke.data.local.dao.PokemonDao
import com.mx.beautypoke.data.local.mapper.PokemonEntityMapper
import com.mx.beautypoke.data.remote.api.PokemonApiService
import com.mx.beautypoke.data.remote.mapper.PokemonMapper
import com.mx.beautypoke.domain.model.Pokemon
import com.mx.beautypoke.domain.repository.PokemonRepository

class PokemonRepositoryImpl(
    private val api: PokemonApiService,
    private val dao: PokemonDao
) : PokemonRepository {

    private val mapper = PokemonMapper

    override suspend fun getPokemonDetail(id: Int): Result<Pokemon> {
        val cachedEntity = dao.getById(id)
        if (cachedEntity != null) {
            return Result.success(PokemonEntityMapper.toDomain(cachedEntity))
        }

        return runCatching {
            val pokemonResponse = api.getPokemon(id)
            val speciesResponse = api.getPokemonSpecies(id)
            val pokemon = mapper.mapToDomain(pokemonResponse, speciesResponse)
            dao.upsert(PokemonEntityMapper.toEntity(pokemon))
            pokemon
        }
    }
}
