package com.mx.beautypoke.presentation.component

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.mx.beautypoke.domain.model.PokemonStat
import org.junit.Rule
import org.junit.Test

class StatBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysStatNameAndValue() {
        val stat = PokemonStat("HP", 45, maxValue = 255)

        composeTestRule.setContent {
            StatBar(stat = stat, barColor = Color.Green, animated = false)
        }

        composeTestRule.onNodeWithText("HP").assertExists()
        composeTestRule.onNodeWithText("45").assertExists()
    }

    @Test
    fun displaysAllStandardStats() {
        val stats = listOf(
            PokemonStat("HP", 100),
            PokemonStat("Attack", 80),
            PokemonStat("Defense", 70),
            PokemonStat("Sp. Atk", 90),
            PokemonStat("Sp. Def", 85),
            PokemonStat("Speed", 110)
        )

        stats.forEach { stat ->
            composeTestRule.setContent {
                StatBar(stat = stat, barColor = Color.Blue, animated = false)
            }

            composeTestRule.onNodeWithText(stat.name).assertExists()
            composeTestRule.onNodeWithText("${stat.value}").assertExists()
        }
    }
}
