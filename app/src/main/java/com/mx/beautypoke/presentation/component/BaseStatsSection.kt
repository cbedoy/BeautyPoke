package com.mx.beautypoke.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mx.beautypoke.domain.model.Pokemon

@Composable
fun BaseStatsSection(
    pokemon: Pokemon,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 24.dp)) {
        pokemon.stats.forEach { stat ->
            StatBar(
                stat = stat,
                barColor = Color(pokemon.color.hex)
            )
        }
    }
}
