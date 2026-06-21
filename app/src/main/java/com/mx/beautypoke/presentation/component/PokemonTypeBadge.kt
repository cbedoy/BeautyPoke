package com.mx.beautypoke.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
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

enum class PokemonTypeBadgeStyle {
    ROUNDED, CIRCULAR
}

@Composable
fun PokemonTypeBadge(
    type: PokemonType,
    modifier: Modifier = Modifier,
    style: PokemonTypeBadgeStyle = PokemonTypeBadgeStyle.ROUNDED,
    containerColor: Color = Color(type.resolveColor().hex).copy(alpha = 0.2f),
    contentColor: Color = Color(type.resolveColor().hex)
) {
    when (style) {
        PokemonTypeBadgeStyle.ROUNDED -> {
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
        PokemonTypeBadgeStyle.CIRCULAR -> {
            Row(
                modifier = modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(containerColor)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(contentColor)
                )
                Text(
                    text = "  ${type.displayName}",
                    color = contentColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1A2E)
@Composable
private fun PokemonTypeBadgeRoundedPreview() {
    PokemonTypeBadge(type = PokemonType.FIRE)
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1A2E)
@Composable
private fun PokemonTypeBadgeCircularPreview() {
    PokemonTypeBadge(
        type = PokemonType.WATER,
        style = PokemonTypeBadgeStyle.CIRCULAR
    )
}
