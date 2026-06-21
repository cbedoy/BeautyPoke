package com.mx.beautypoke.data.local.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mx.beautypoke.data.local.entity.PokemonEntity
import com.mx.beautypoke.domain.model.Pokemon
import com.mx.beautypoke.domain.model.PokemonColor
import com.mx.beautypoke.domain.model.PokemonStat
import com.mx.beautypoke.domain.model.PokemonType

object PokemonEntityMapper {

    private val gson = Gson()

    fun toDomain(entity: PokemonEntity): Pokemon {
        val typeListType = object : TypeToken<List<String>>() {}.type
        val types: List<PokemonType> = gson.fromJson<List<String>>(entity.typesJson, typeListType)
            .map { name -> try { PokemonType.valueOf(name.uppercase()) } catch (_: IllegalArgumentException) { PokemonType.NORMAL } }

        val statListType = object : TypeToken<List<PokemonStatData>>() {}.type
        val stats: List<PokemonStat> = gson.fromJson<List<PokemonStatData>>(entity.statsJson, statListType)
            .map { PokemonStat(name = it.name, value = it.value, maxValue = 255) }

        return Pokemon(
            id = entity.id,
            name = entity.name,
            imageUrl = entity.imageUrl,
            types = types,
            stats = stats,
            height = entity.height,
            weight = entity.weight,
            description = entity.description,
            color = try { PokemonColor.valueOf(entity.colorName.uppercase()) } catch (_: IllegalArgumentException) { PokemonColor.GRAY },
            category = entity.category
        )
    }

    fun toEntity(pokemon: Pokemon): PokemonEntity {
        val typesJson = gson.toJson(pokemon.types.map { it.name })
        val statsJson = gson.toJson(pokemon.stats.map { PokemonStatData(it.name, it.value) })

        return PokemonEntity(
            id = pokemon.id,
            name = pokemon.name,
            imageUrl = pokemon.imageUrl,
            typesJson = typesJson,
            statsJson = statsJson,
            height = pokemon.height,
            weight = pokemon.weight,
            description = pokemon.description,
            colorName = pokemon.color.name,
            category = pokemon.category
        )
    }

    private data class PokemonStatData(
        val name: String,
        val value: Int
    )
}
