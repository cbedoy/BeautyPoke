package com.mx.beautypoke.presentation.theme

import androidx.compose.ui.graphics.Color
import com.mx.beautypoke.domain.model.PokemonType

data class PokemonTheme(
    val primary: Color,
    val secondary: Color,
    val surface: Color,
    val onSurface: Color
)

fun PokemonType.toTheme(): PokemonTheme = when (this) {
    PokemonType.NORMAL -> PokemonTheme(
        primary = Color(0xFFA8A878),
        secondary = Color(0xFFD4D4A0),
        surface = Color(0xFF3A3A2E),
        onSurface = Color(0xFFE0E0C8)
    )
    PokemonType.FIRE -> PokemonTheme(
        primary = Color(0xFFF08030),
        secondary = Color(0xFFF5AC78),
        surface = Color(0xFF4A2E1A),
        onSurface = Color(0xFFF0D0A0)
    )
    PokemonType.WATER -> PokemonTheme(
        primary = Color(0xFF6890F0),
        secondary = Color(0xFF9DB7F5),
        surface = Color(0xFF1A2E4A),
        onSurface = Color(0xFFA0C0F0)
    )
    PokemonType.ELECTRIC -> PokemonTheme(
        primary = Color(0xFFF8D030),
        secondary = Color(0xFFFAE078),
        surface = Color(0xFF4A3E1A),
        onSurface = Color(0xFFF0E0A0)
    )
    PokemonType.GRASS -> PokemonTheme(
        primary = Color(0xFF78C850),
        secondary = Color(0xFFA7DB8D),
        surface = Color(0xFF1E3A1A),
        onSurface = Color(0xFFB0D8A0)
    )
    PokemonType.ICE -> PokemonTheme(
        primary = Color(0xFF98D8D8),
        secondary = Color(0xFFBCE6E6),
        surface = Color(0xFF1A3A3A),
        onSurface = Color(0xFFC0E0E0)
    )
    PokemonType.FIGHTING -> PokemonTheme(
        primary = Color(0xFFC03028),
        secondary = Color(0xFFD67873),
        surface = Color(0xFF4A1A1A),
        onSurface = Color(0xFFE0A0A0)
    )
    PokemonType.POISON -> PokemonTheme(
        primary = Color(0xFFA040A0),
        secondary = Color(0xFFC183C1),
        surface = Color(0xFF3A1A3A),
        onSurface = Color(0xFFD0A0D0)
    )
    PokemonType.GROUND -> PokemonTheme(
        primary = Color(0xFFE0C068),
        secondary = Color(0xFFEAD9A0),
        surface = Color(0xFF4A3A1A),
        onSurface = Color(0xFFE0D0A0)
    )
    PokemonType.FLYING -> PokemonTheme(
        primary = Color(0xFFA890F0),
        secondary = Color(0xFFC6B7F5),
        surface = Color(0xFF2E1A4A),
        onSurface = Color(0xFFC0B0E0)
    )
    PokemonType.PSYCHIC -> PokemonTheme(
        primary = Color(0xFFF85888),
        secondary = Color(0xFFFA92B2),
        surface = Color(0xFF4A1A2E),
        onSurface = Color(0xFFE0A0B8)
    )
    PokemonType.BUG -> PokemonTheme(
        primary = Color(0xFFA8B820),
        secondary = Color(0xFFC6D36E),
        surface = Color(0xFF3A3A1A),
        onSurface = Color(0xFFD0D0A0)
    )
    PokemonType.ROCK -> PokemonTheme(
        primary = Color(0xFFB8A038),
        secondary = Color(0xFFD1C27D),
        surface = Color(0xFF3A2E1A),
        onSurface = Color(0xFFD0C0A0)
    )
    PokemonType.GHOST -> PokemonTheme(
        primary = Color(0xFF705898),
        secondary = Color(0xFFA292BC),
        surface = Color(0xFF1A1A2E),
        onSurface = Color(0xFFC4A0D8)
    )
    PokemonType.DRAGON -> PokemonTheme(
        primary = Color(0xFF7038F8),
        secondary = Color(0xFFA27DFA),
        surface = Color(0xFF1E1A4A),
        onSurface = Color(0xFFB0A0E0)
    )
    PokemonType.DARK -> PokemonTheme(
        primary = Color(0xFF705848),
        secondary = Color(0xFFA29288),
        surface = Color(0xFF1A1A1A),
        onSurface = Color(0xFFC0B0A0)
    )
    PokemonType.STEEL -> PokemonTheme(
        primary = Color(0xFFB8B8D0),
        secondary = Color(0xFFD1D1E0),
        surface = Color(0xFF2E2E3A),
        onSurface = Color(0xFFD0D0E0)
    )
    PokemonType.FAIRY -> PokemonTheme(
        primary = Color(0xFFEE99AC),
        secondary = Color(0xFFF4BDC9),
        surface = Color(0xFF4A2E3A),
        onSurface = Color(0xFFE0C0C8)
    )
}
