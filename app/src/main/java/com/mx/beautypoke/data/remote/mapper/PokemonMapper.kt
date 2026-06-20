package com.mx.beautypoke.data.remote.mapper

import com.mx.beautypoke.data.remote.dto.PokemonResponse
import com.mx.beautypoke.data.remote.dto.PokemonSpeciesResponse
import com.mx.beautypoke.domain.model.Pokemon
import com.mx.beautypoke.domain.model.PokemonColor
import com.mx.beautypoke.domain.model.PokemonStat
import com.mx.beautypoke.domain.model.PokemonType

object PokemonMapper {

    fun mapToDomain(pokemon: PokemonResponse, species: PokemonSpeciesResponse): Pokemon {
        return Pokemon(
            id = pokemon.id,
            name = pokemon.name.replaceFirstChar { it.uppercase() },
            imageUrl = pokemon.sprites.other.officialArtwork.frontDefault
                ?: "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${pokemon.id}.png",
            types = pokemon.types.sortedBy { it.slot }.map { mapType(it.type.name) },
            stats = pokemon.stats.map { mapStat(it.baseStat, it.stat.name) },
            height = pokemon.height,
            weight = pokemon.weight,
            description = extractDescription(species),
            color = mapColor(species.color.name),
            category = extractCategory(species)
        )
    }

    private fun extractDescription(species: PokemonSpeciesResponse): String {
        val englishEntries = species.flavorTextEntries
            .filter { it.language.name == "en" }

        val preferred = englishEntries.find { it.version.name == "sword" }
            ?: englishEntries.find { it.version.name == "x" }
            ?: englishEntries.firstOrNull()

        return preferred?.flavorText
            ?.replace("\\f".toRegex(), " ")
            ?.replace("\\n".toRegex(), " ")
            ?.replace("\\u000c".toRegex(), " ")
            ?.trim() ?: "No description available."
    }

    private fun mapType(typeName: String): PokemonType {
        return try {
            PokemonType.valueOf(typeName.uppercase())
        } catch (_: IllegalArgumentException) {
            PokemonType.NORMAL
        }
    }

    private fun mapStat(statValue: Int, statName: String): PokemonStat {
        val displayName = when (statName) {
            "hp" -> "HP"
            "attack" -> "Attack"
            "defense" -> "Defense"
            "special-attack" -> "Sp. Atk"
            "special-defense" -> "Sp. Def"
            "speed" -> "Speed"
            else -> statName.replaceFirstChar { it.uppercase() }
        }
        return PokemonStat(name = displayName, value = statValue, maxValue = 255)
    }

    private fun extractCategory(species: PokemonSpeciesResponse): String {
        return species.genera
            .find { it.language.name == "en" }
            ?.genus ?: "Unknown"
    }

    private fun mapColor(colorName: String): PokemonColor {
        return try {
            PokemonColor.valueOf(colorName.uppercase())
        } catch (_: IllegalArgumentException) {
            PokemonColor.GRAY
        }
    }
}
