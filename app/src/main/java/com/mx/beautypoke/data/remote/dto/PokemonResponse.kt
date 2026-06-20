package com.mx.beautypoke.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PokemonResponse(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val types: List<PokemonTypeDto>,
    val stats: List<PokemonStatDto>,
    val sprites: PokemonSprites
)

data class PokemonTypeDto(
    val slot: Int,
    val type: NamedApiResource
)

data class PokemonStatDto(
    @SerializedName("base_stat") val baseStat: Int,
    val stat: NamedApiResource
)

data class PokemonSprites(
    val other: OtherSprites
)

data class OtherSprites(
    @SerializedName("official-artwork") val officialArtwork: OfficialArtwork
)

data class OfficialArtwork(
    @SerializedName("front_default") val frontDefault: String?
)

data class NamedApiResource(
    val name: String
)
