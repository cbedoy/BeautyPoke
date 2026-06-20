package com.mx.beautypoke.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PokemonSpeciesResponse(
    val color: NamedApiResource,
    @SerializedName("genera") val genera: List<GenusEntry>,
    @SerializedName("flavor_text_entries") val flavorTextEntries: List<FlavorTextEntry>
)

data class GenusEntry(
    val genus: String,
    val language: NamedApiResource
)

data class FlavorTextEntry(
    @SerializedName("flavor_text") val flavorText: String,
    val language: NamedApiResource,
    val version: NamedApiResource
)
