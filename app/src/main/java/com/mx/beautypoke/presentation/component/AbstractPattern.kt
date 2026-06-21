package com.mx.beautypoke.presentation.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mx.beautypoke.domain.model.PokemonType
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AbstractPattern(
    type: PokemonType,
    modifier: Modifier = Modifier,
    patternColor: Color = Color.White.copy(alpha = 0.08f)
) {
    Canvas(modifier = modifier) {
        when (type) {
            PokemonType.PSYCHIC -> drawGeometricPattern(patternColor)
            PokemonType.GHOST -> drawSmokePattern(patternColor)
            PokemonType.DRAGON -> drawRingPattern(patternColor)
            else -> drawCirclePattern(patternColor)
        }
    }
}

private fun DrawScope.drawGeometricPattern(color: Color) {
    val cx = size.width / 2
    val cy = size.height / 2
    val maxR = minOf(size.width, size.height) * 0.4f
    for (i in 0 until 12) {
        val angle = Math.toRadians((i * 30).toDouble())
        val x = cx + (maxR * cos(angle)).toFloat()
        val y = cy + (maxR * sin(angle)).toFloat()
        val r = maxR * 0.3f
        drawCircle(color = color, radius = r, center = Offset(x, y))
    }
}

private fun DrawScope.drawSmokePattern(color: Color) {
    val cx = size.width / 2
    val cy = size.height / 2
    for (i in 0 until 8) {
        val angle = Math.toRadians((i * 45).toDouble())
        val r = minOf(size.width, size.height) * (0.15f + i * 0.03f)
        val x = cx + (r * cos(angle)).toFloat()
        val y = cy + (r * sin(angle)).toFloat()
        drawCircle(color = color, radius = r * 0.5f, center = Offset(x, y))
    }
}

private fun DrawScope.drawRingPattern(color: Color) {
    val cx = size.width / 2
    val cy = size.height / 2
    val maxR = minOf(size.width, size.height) * 0.4f
    for (i in 0 until 5) {
        val r = maxR * (0.3f + i * 0.15f)
        drawCircle(color = color, radius = r, center = Offset(cx, cy), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF4A2E1A)
@Composable
private fun AbstractPatternFirePreview() {
    AbstractPattern(
        type = PokemonType.FIRE,
        modifier = Modifier.size(200.dp),
        patternColor = Color.White.copy(alpha = 0.08f)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF4A1A2E)
@Composable
private fun AbstractPatternPsychicPreview() {
    AbstractPattern(
        type = PokemonType.PSYCHIC,
        modifier = Modifier.size(200.dp),
        patternColor = Color.White.copy(alpha = 0.08f)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1A2E)
@Composable
private fun AbstractPatternGhostPreview() {
    AbstractPattern(
        type = PokemonType.GHOST,
        modifier = Modifier.size(200.dp),
        patternColor = Color.White.copy(alpha = 0.08f)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1A4A)
@Composable
private fun AbstractPatternDragonPreview() {
    AbstractPattern(
        type = PokemonType.DRAGON,
        modifier = Modifier.size(200.dp),
        patternColor = Color.White.copy(alpha = 0.08f)
    )
}

private fun DrawScope.drawCirclePattern(color: Color) {
    val cx = size.width / 2
    val cy = size.height / 2
    val maxR = minOf(size.width, size.height) * 0.4f
    for (i in 0 until 6) {
        val angle = Math.toRadians((i * 60).toDouble())
        val r = maxR * 0.6f
        val x = cx + (r * cos(angle)).toFloat()
        val y = cy + (r * sin(angle)).toFloat()
        drawCircle(color = color, radius = maxR * 0.15f, center = Offset(x, y))
    }
}
