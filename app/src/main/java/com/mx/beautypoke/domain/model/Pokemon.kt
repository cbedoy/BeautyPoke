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
    val color: PokemonColor
)

data class PokemonStat(
    val name: String,
    val value: Int,
    val maxValue: Int = 255
)

enum class PokemonType(val displayName: String) {
    NORMAL("Normal"),
    FIRE("Fuego"),
    WATER("Agua"),
    ELECTRIC("Eléctrico"),
    GRASS("Planta"),
    ICE("Hielo"),
    FIGHTING("Lucha"),
    POISON("Veneno"),
    GROUND("Tierra"),
    FLYING("Volador"),
    PSYCHIC("Psíquico"),
    BUG("Bicho"),
    ROCK("Roca"),
    GHOST("Fantasma"),
    DRAGON("Dragón"),
    DARK("Siniestro"),
    STEEL("Acero"),
    FAIRY("Hada")
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
