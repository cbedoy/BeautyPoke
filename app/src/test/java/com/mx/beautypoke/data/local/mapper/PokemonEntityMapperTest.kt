package com.mx.beautypoke.data.local.mapper

import com.mx.beautypoke.data.local.entity.PokemonEntity
import com.mx.beautypoke.domain.model.Pokemon
import com.mx.beautypoke.domain.model.PokemonColor
import com.mx.beautypoke.domain.model.PokemonStat
import com.mx.beautypoke.domain.model.PokemonType
import org.junit.Assert.assertEquals
import org.junit.Test

class PokemonEntityMapperTest {

    private val mapper = PokemonEntityMapper

    @Test
    fun `toDomain maps entity correctly with all fields`() {
        val entity = PokemonEntity(
            id = 1,
            name = "Bulbasaur",
            imageUrl = "https://example.com/bulbasaur.png",
            typesJson = """["GRASS","POISON"]""",
            statsJson = """[{"name":"HP","value":45},{"name":"Attack","value":49}]""",
            height = 7,
            weight = 69,
            description = "A strange seed was planted on its back at birth.",
            colorName = "GREEN",
            category = "Seed Pokémon"
        )

        val pokemon = mapper.toDomain(entity)

        assertEquals(1, pokemon.id)
        assertEquals("Bulbasaur", pokemon.name)
        assertEquals("https://example.com/bulbasaur.png", pokemon.imageUrl)
        assertEquals(listOf(PokemonType.GRASS, PokemonType.POISON), pokemon.types)
        assertEquals(
            listOf(PokemonStat("HP", 45), PokemonStat("Attack", 49)),
            pokemon.stats
        )
        assertEquals(7, pokemon.height)
        assertEquals(69, pokemon.weight)
        assertEquals("A strange seed was planted on its back at birth.", pokemon.description)
        assertEquals(PokemonColor.GREEN, pokemon.color)
        assertEquals("Seed Pokémon", pokemon.category)
    }

    @Test
    fun `toDomain handles unknown type gracefully`() {
        val entity = PokemonEntity(
            id = 999,
            name = "Unknown",
            imageUrl = "",
            typesJson = """["UNKNOWN_TYPE"]""",
            statsJson = "[]",
            height = 10,
            weight = 10,
            description = "",
            colorName = "UNKNOWN_COLOR",
            category = ""
        )

        val pokemon = mapper.toDomain(entity)

        assertEquals(listOf(PokemonType.NORMAL), pokemon.types)
        assertEquals(PokemonColor.GRAY, pokemon.color)
    }

    @Test
    fun `toEntity maps domain correctly`() {
        val pokemon = Pokemon(
            id = 4,
            name = "Charmander",
            imageUrl = "https://example.com/charmander.png",
            types = listOf(PokemonType.FIRE),
            stats = listOf(PokemonStat("HP", 39)),
            height = 6,
            weight = 85,
            description = "Obviously prefers hot places.",
            color = PokemonColor.RED,
            category = "Lizard Pokémon"
        )

        val entity = mapper.toEntity(pokemon)

        assertEquals(4, entity.id)
        assertEquals("Charmander", entity.name)
        assertEquals("https://example.com/charmander.png", entity.imageUrl)
        assertEquals("""["FIRE"]""", entity.typesJson)
        assertEquals("""[{"name":"HP","value":39}]""", entity.statsJson)
        assertEquals(6, entity.height)
        assertEquals(85, entity.weight)
        assertEquals("Obviously prefers hot places.", entity.description)
        assertEquals("RED", entity.colorName)
        assertEquals("Lizard Pokémon", entity.category)
    }

    @Test
    fun `roundtrip preserves all data`() {
        val original = Pokemon(
            id = 25,
            name = "Pikachu",
            imageUrl = "https://example.com/pikachu.png",
            types = listOf(PokemonType.ELECTRIC),
            stats = listOf(
                PokemonStat("HP", 35),
                PokemonStat("Attack", 55),
                PokemonStat("Defense", 40),
                PokemonStat("Sp. Atk", 50),
                PokemonStat("Sp. Def", 50),
                PokemonStat("Speed", 90)
            ),
            height = 4,
            weight = 60,
            description = "When several of these POKéMON gather, their electricity could build and cause lightning storms.",
            color = PokemonColor.YELLOW,
            category = "Mouse Pokémon"
        )

        val entity = mapper.toEntity(original)
        val result = mapper.toDomain(entity)

        assertEquals(original.id, result.id)
        assertEquals(original.name, result.name)
        assertEquals(original.imageUrl, result.imageUrl)
        assertEquals(original.types, result.types)
        assertEquals(original.stats, result.stats)
        assertEquals(original.height, result.height)
        assertEquals(original.weight, result.weight)
        assertEquals(original.description, result.description)
        assertEquals(original.color, result.color)
        assertEquals(original.category, result.category)
    }
}
