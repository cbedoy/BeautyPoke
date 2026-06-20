package com.mx.beautypoke.data.remote.api

import com.mx.beautypoke.data.remote.dto.PokemonResponse
import com.mx.beautypoke.data.remote.dto.PokemonSpeciesResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface PokemonApiService {

    @GET("pokemon/{id}")
    suspend fun getPokemon(@Path("id") id: Int): PokemonResponse

    @GET("pokemon-species/{id}")
    suspend fun getPokemonSpecies(@Path("id") id: Int): PokemonSpeciesResponse
}
