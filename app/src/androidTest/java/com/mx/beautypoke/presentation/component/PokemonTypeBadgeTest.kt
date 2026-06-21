package com.mx.beautypoke.presentation.component

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.mx.beautypoke.domain.model.PokemonType
import org.junit.Rule
import org.junit.Test

class PokemonTypeBadgeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun rounded_displaysTypeName() {
        composeTestRule.setContent {
            PokemonTypeBadge(
                type = PokemonType.FIRE,
                style = PokemonTypeBadgeStyle.ROUNDED
            )
        }

        composeTestRule.onNodeWithText("Fire").assertIsDisplayed()
    }

    @Test
    fun circular_displaysTypeNameWithDot() {
        composeTestRule.setContent {
            PokemonTypeBadge(
                type = PokemonType.WATER,
                style = PokemonTypeBadgeStyle.CIRCULAR
            )
        }

        composeTestRule.onNodeWithText("  Water").assertIsDisplayed()
    }

    @Test
    fun customColors_areApplied() {
        composeTestRule.setContent {
            PokemonTypeBadge(
                type = PokemonType.GRASS,
                containerColor = Color.Red,
                contentColor = Color.Blue
            )
        }

        composeTestRule.onNodeWithText("Grass").assertIsDisplayed()
    }

    @Test
    fun displaysAllTypes() {
        PokemonType.entries.forEach { type ->
            composeTestRule.setContent {
                PokemonTypeBadge(type = type)
            }
            composeTestRule.onNodeWithText(type.displayName).assertIsDisplayed()
        }
    }
}
