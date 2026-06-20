package com.mx.beautypoke.domain.model

object TypeWeaknesses {
    private val chart = mapOf(
        PokemonType.NORMAL to listOf(PokemonType.FIGHTING),
        PokemonType.FIRE to listOf(PokemonType.WATER, PokemonType.ROCK, PokemonType.GROUND),
        PokemonType.WATER to listOf(PokemonType.ELECTRIC, PokemonType.GRASS),
        PokemonType.ELECTRIC to listOf(PokemonType.GROUND),
        PokemonType.GRASS to listOf(PokemonType.FIRE, PokemonType.ICE, PokemonType.POISON, PokemonType.FLYING, PokemonType.BUG),
        PokemonType.ICE to listOf(PokemonType.FIRE, PokemonType.FIGHTING, PokemonType.ROCK, PokemonType.STEEL),
        PokemonType.FIGHTING to listOf(PokemonType.FLYING, PokemonType.PSYCHIC, PokemonType.FAIRY),
        PokemonType.POISON to listOf(PokemonType.GROUND, PokemonType.PSYCHIC),
        PokemonType.GROUND to listOf(PokemonType.WATER, PokemonType.GRASS, PokemonType.ICE),
        PokemonType.FLYING to listOf(PokemonType.ROCK, PokemonType.ELECTRIC, PokemonType.ICE),
        PokemonType.PSYCHIC to listOf(PokemonType.BUG, PokemonType.GHOST, PokemonType.DARK),
        PokemonType.BUG to listOf(PokemonType.FIRE, PokemonType.FLYING, PokemonType.ROCK),
        PokemonType.ROCK to listOf(PokemonType.WATER, PokemonType.GRASS, PokemonType.FIGHTING, PokemonType.GROUND, PokemonType.STEEL),
        PokemonType.GHOST to listOf(PokemonType.GHOST, PokemonType.DARK),
        PokemonType.DRAGON to listOf(PokemonType.ICE, PokemonType.DRAGON, PokemonType.FAIRY),
        PokemonType.DARK to listOf(PokemonType.FIGHTING, PokemonType.BUG, PokemonType.FAIRY),
        PokemonType.STEEL to listOf(PokemonType.FIRE, PokemonType.FIGHTING, PokemonType.GROUND),
        PokemonType.FAIRY to listOf(PokemonType.POISON, PokemonType.STEEL)
    )

    fun weaknessesOf(type: PokemonType): List<PokemonType> = chart[type] ?: emptyList()
}
