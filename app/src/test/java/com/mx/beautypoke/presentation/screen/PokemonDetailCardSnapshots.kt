package com.mx.beautypoke.presentation.screen

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.mx.beautypoke.domain.model.Pokemon
import com.mx.beautypoke.domain.model.PokemonColor
import com.mx.beautypoke.domain.model.PokemonStat
import com.mx.beautypoke.domain.model.PokemonType
import org.junit.Rule
import org.junit.Test

class PokemonDetailCardSnapshots {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        theme = "android:Theme.Material.Light.NoActionBar"
    )

    @Test
    fun charizardDetail() {
        paparazzi.snapshot {
            PokemonDetailCard(
                pokemon = Pokemon(
                    id = 6,
                    name = "Charizard",
                    imageUrl = "",
                    types = listOf(PokemonType.FIRE, PokemonType.FLYING),
                    stats = listOf(
                        PokemonStat(name = "HP", value = 78),
                        PokemonStat(name = "Attack", value = 84),
                        PokemonStat(name = "Defense", value = 78),
                        PokemonStat(name = "Sp. Atk", value = 109),
                        PokemonStat(name = "Sp. Def", value = 85),
                        PokemonStat(name = "Speed", value = 100)
                    ),
                    height = 17,
                    weight = 905,
                    description = "Spits fire that is hot enough to melt boulders. Known to cause forest fires unintentionally.",
                    color = PokemonColor.RED,
                    category = "Flame Pokémon"
                )
            )
        }
    }

    @Test
    fun blastoiseDetail() {
        paparazzi.snapshot {
            PokemonDetailCard(
                pokemon = Pokemon(
                    id = 9,
                    name = "Blastoise",
                    imageUrl = "",
                    types = listOf(PokemonType.WATER),
                    stats = listOf(
                        PokemonStat(name = "HP", value = 79),
                        PokemonStat(name = "Attack", value = 83),
                        PokemonStat(name = "Defense", value = 100),
                        PokemonStat(name = "Sp. Atk", value = 85),
                        PokemonStat(name = "Sp. Def", value = 105),
                        PokemonStat(name = "Speed", value = 78)
                    ),
                    height = 16,
                    weight = 855,
                    description = "It crushes its foe under its heavy body to cause fainting. In a pinch, it will withdraw inside its shell.",
                    color = PokemonColor.BLUE,
                    category = "Shellfish Pokémon"
                )
            )
        }
    }

    @Test
    fun gengarDetail() {
        paparazzi.snapshot {
            PokemonDetailCard(
                pokemon = Pokemon(
                    id = 94,
                    name = "Gengar",
                    imageUrl = "",
                    types = listOf(PokemonType.GHOST, PokemonType.POISON),
                    stats = listOf(
                        PokemonStat(name = "HP", value = 60),
                        PokemonStat(name = "Attack", value = 65),
                        PokemonStat(name = "Defense", value = 60),
                        PokemonStat(name = "Sp. Atk", value = 130),
                        PokemonStat(name = "Sp. Def", value = 75),
                        PokemonStat(name = "Speed", value = 110)
                    ),
                    height = 15,
                    weight = 405,
                    description = "Under a full moon, this Pokémon likes to mimic the shadows of people and laugh at their fright.",
                    color = PokemonColor.PURPLE,
                    category = "Shadow Pokémon"
                )
            )
        }
    }
}
