package com.mx.beautypoke.presentation.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.mx.beautypoke.domain.model.PokemonStat

@Composable
fun StatBar(
    stat: PokemonStat,
    barColor: Color,
    modifier: Modifier = Modifier,
    onSurfaceColor: Color = Color(0xFFE8DCF0),
    animated: Boolean = true
) {
    val progress by animateFloatAsState(
        targetValue = stat.value.toFloat() / stat.maxValue.coerceAtLeast(stat.value),
        animationSpec = tween(durationMillis = if (animated) 800 else 0),
        label = "statProgress"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stat.name,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = onSurfaceColor.copy(alpha = 0.6f),
            modifier = Modifier.width(56.dp)
        )

        Text(
            text = "${stat.value}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = onSurfaceColor,
            textAlign = TextAlign.End,
            modifier = Modifier.width(32.dp)
        )

        Box(
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(onSurfaceColor.copy(alpha = 0.15f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(barColor)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF3A3A2E)
@Composable
private fun StatBarPreview() {
    StatBar(
        stat = PokemonStat(name = "HP", value = 78),
        barColor = Color(0xFFF08030)
    )
}
