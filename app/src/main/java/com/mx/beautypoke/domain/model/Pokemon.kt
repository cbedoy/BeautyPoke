package com.mx.beautypoke.domain.model

data class Pokemon(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val types: List<PokemonType>,
    val stats: List<PokemonStat>,
    val height: Int,
    val weight: Int,
    val description: String,
    val color: PokemonColor,
    val category: String
)

data class PokemonStat(
    val name: String,
    val value: Int,
    val maxValue: Int = 255
)

enum class PokemonType(val displayName: String) {
    NORMAL("Normal"),
    FIRE("Fire"),
    WATER("Water"),
    ELECTRIC("Electric"),
    GRASS("Grass"),
    ICE("Ice"),
    FIGHTING("Fighting"),
    POISON("Poison"),
    GROUND("Ground"),
    FLYING("Flying"),
    PSYCHIC("Psychic"),
    BUG("Bug"),
    ROCK("Rock"),
    GHOST("Ghost"),
    DRAGON("Dragon"),
    DARK("Dark"),
    STEEL("Steel"),
    FAIRY("Fairy")
}

enum class PokemonColor(val hex: Long) {
    GREEN(0xFF74CB48),
    RED(0xFFF57D31),
    BLUE(0xFF6493EB),
    YELLOW(0xFFFDD847),
    PURPLE(0xFFA974D1),
    BROWN(0xFF8D6E5E),
    PINK(0xFFFB94A3),
    GRAY(0xFFA3A3A3),
    WHITE(0xFFEFEFEF),
    BLACK(0xFF303030)
}
