package com.mx.beautypoke.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mx.beautypoke.domain.model.PokemonColor
import com.mx.beautypoke.domain.model.PokemonType

fun PokemonType.resolveColor(): PokemonColor = when (this) {
    PokemonType.NORMAL -> PokemonColor.GRAY
    PokemonType.FIRE -> PokemonColor.RED
    PokemonType.WATER -> PokemonColor.BLUE
    PokemonType.ELECTRIC -> PokemonColor.YELLOW
    PokemonType.GRASS -> PokemonColor.GREEN
    PokemonType.ICE -> PokemonColor.BLUE
    PokemonType.FIGHTING -> PokemonColor.RED
    PokemonType.POISON -> PokemonColor.PURPLE
    PokemonType.GROUND -> PokemonColor.BROWN
    PokemonType.FLYING -> PokemonColor.BLUE
    PokemonType.PSYCHIC -> PokemonColor.PINK
    PokemonType.BUG -> PokemonColor.GREEN
    PokemonType.ROCK -> PokemonColor.BROWN
    PokemonType.GHOST -> PokemonColor.PURPLE
    PokemonType.DRAGON -> PokemonColor.PURPLE
    PokemonType.DARK -> PokemonColor.BLACK
    PokemonType.STEEL -> PokemonColor.GRAY
    PokemonType.FAIRY -> PokemonColor.PINK
}

@Composable
fun PokemonTypeBadge(
    type: PokemonType,
    modifier: Modifier = Modifier,
    containerColor: Color = Color(type.resolveColor().hex).copy(alpha = 0.2f),
    contentColor: Color = Color(type.resolveColor().hex)
) {
    Text(
        text = type.displayName,
        color = contentColor,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(containerColor)
            .padding(horizontal = 16.dp, vertical = 6.dp)
    )
}
