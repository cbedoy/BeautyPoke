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
import com.mx.beautypoke.domain.model.PokemonType

@Composable
fun WeaknessPill(
    type: PokemonType,
    modifier: Modifier = Modifier,
    containerColor: Color = Color(type.resolveColor().hex).copy(alpha = 0.2f),
    contentColor: Color = Color(type.resolveColor().hex)
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(contentColor)
        )
        Text(
            text = "  ${type.displayName}",
            color = contentColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF3A3A2E)
@Composable
private fun WeaknessPillPreview() {
    WeaknessPill(type = PokemonType.GRASS)
}
