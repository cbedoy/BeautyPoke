package com.mx.beautypoke.presentation.screen

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.mx.beautypoke.domain.model.Pokemon
import com.mx.beautypoke.domain.model.PokemonCarouselUiState
import com.mx.beautypoke.domain.model.PokemonColor
import com.mx.beautypoke.domain.model.PokemonStat
import com.mx.beautypoke.domain.model.PokemonType
import com.mx.beautypoke.presentation.viewmodel.PokemonDetailViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Rule
import org.junit.Test

class PokemonDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loadingScreen_displaysProgressIndicator() {
        val viewModel = mockViewModel(state = PokemonCarouselUiState.Loading)

        composeTestRule.setContent {
            PokemonDetailScreen(viewModel = viewModel, onBackClick = {})
        }

        composeTestRule.onNodeWithTag("loading_indicator").assertExists()
    }

    @Test
    fun errorScreen_displaysMessageAndRetryButton() {
        val viewModel = mockViewModel(
            state = PokemonCarouselUiState.Error("Failed to load")
        )

        composeTestRule.setContent {
            PokemonDetailScreen(viewModel = viewModel, onBackClick = {})
        }

        composeTestRule.onNodeWithText("Failed to load").assertExists()
        composeTestRule.onNodeWithText("Retry").assertExists()
    }

    @Test
    fun errorScreen_retryButton_callsOnRetry() {
        val viewModel = mockViewModel(
            state = PokemonCarouselUiState.Error("Failed to load")
        )

        composeTestRule.setContent {
            PokemonDetailScreen(viewModel = viewModel, onBackClick = {})
        }

        composeTestRule.onNodeWithText("Retry").performClick()
        verify { viewModel.onRetry() }
    }

    @Test
    fun successScreen_displaysPokemonName() {
        val pokemon = createPokemon(id = 1, name = "Bulbasaur")
        val viewModel = mockViewModel(
            state = PokemonCarouselUiState.Success(
                pokemonList = listOf(pokemon),
                currentIndex = 0
            )
        )

        composeTestRule.setContent {
            PokemonDetailScreen(viewModel = viewModel, onBackClick = {})
        }

        composeTestRule.onNodeWithText("Bulbasaur").assertExists()
    }

    @Test
    fun successScreen_displaysPokemonNumber() {
        val pokemon = createPokemon(id = 25, name = "Pikachu")
        val viewModel = mockViewModel(
            state = PokemonCarouselUiState.Success(
                pokemonList = listOf(pokemon),
                currentIndex = 0
            )
        )

        composeTestRule.setContent {
            PokemonDetailScreen(viewModel = viewModel, onBackClick = {})
        }

        composeTestRule.onNodeWithText("N°025").assertExists()
    }

    @Test
    fun successScreen_displaysDescription() {
        val pokemon = createPokemon(
            id = 4,
            name = "Charmander",
            description = "Obviously prefers hot places."
        )
        val viewModel = mockViewModel(
            state = PokemonCarouselUiState.Success(
                pokemonList = listOf(pokemon),
                currentIndex = 0
            )
        )

        composeTestRule.setContent {
            PokemonDetailScreen(viewModel = viewModel, onBackClick = {})
        }

        composeTestRule.onNodeWithText("Obviously prefers hot places.").assertExists()
    }

    @Test
    fun successScreen_displaysCategory() {
        val pokemon = createPokemon(
            id = 7,
            name = "Squirtle",
            category = "Tiny Turtle Pokémon"
        )
        val viewModel = mockViewModel(
            state = PokemonCarouselUiState.Success(
                pokemonList = listOf(pokemon),
                currentIndex = 0
            )
        )

        composeTestRule.setContent {
            PokemonDetailScreen(viewModel = viewModel, onBackClick = {})
        }

        composeTestRule.onNodeWithText("Tiny Turtle Pokémon").assertExists()
    }

    @Test
    fun successScreen_displaysWeightAndHeight() {
        val pokemon = createPokemon(
            id = 6,
            name = "Charizard",
            weight = 905,
            height = 17
        )
        val viewModel = mockViewModel(
            state = PokemonCarouselUiState.Success(
                pokemonList = listOf(pokemon),
                currentIndex = 0
            )
        )

        composeTestRule.setContent {
            PokemonDetailScreen(viewModel = viewModel, onBackClick = {})
        }

        composeTestRule.onNodeWithText("90.5 kg").assertExists()
        composeTestRule.onNodeWithText("1.7 m").assertExists()
    }

    @Test
    fun successScreen_displaysStatNames() {
        val stats = listOf(
            PokemonStat("HP", 45),
            PokemonStat("Attack", 49),
            PokemonStat("Defense", 49)
        )
        val pokemon = createPokemon(
            id = 1,
            name = "Bulbasaur",
            stats = stats
        )
        val viewModel = mockViewModel(
            state = PokemonCarouselUiState.Success(
                pokemonList = listOf(pokemon),
                currentIndex = 0
            )
        )

        composeTestRule.setContent {
            PokemonDetailScreen(viewModel = viewModel, onBackClick = {})
        }

        composeTestRule.onNodeWithText("HP").assertExists()
        composeTestRule.onNodeWithText("Attack").assertExists()
        composeTestRule.onNodeWithText("Defense").assertExists()
    }

    @Test
    fun successScreen_displaysTypeBadges() {
        val pokemon = createPokemon(
            id = 1,
            name = "Bulbasaur",
            types = listOf(PokemonType.GRASS, PokemonType.POISON)
        )
        val viewModel = mockViewModel(
            state = PokemonCarouselUiState.Success(
                pokemonList = listOf(pokemon),
                currentIndex = 0
            )
        )

        composeTestRule.setContent {
            PokemonDetailScreen(viewModel = viewModel, onBackClick = {})
        }

        composeTestRule.onNodeWithText("Grass").assertExists()
        composeTestRule.onNodeWithText("Poison").assertExists()
    }

    @Test
    fun backButton_callsOnBackClick() {
        var backClicked = false
        val pokemon = createPokemon(id = 1, name = "Bulbasaur")
        val viewModel = mockViewModel(
            state = PokemonCarouselUiState.Success(
                pokemonList = listOf(pokemon),
                currentIndex = 0
            )
        )

        composeTestRule.setContent {
            PokemonDetailScreen(
                viewModel = viewModel,
                onBackClick = { backClicked = true }
            )
        }

        composeTestRule.onNodeWithContentDescription("Back").performClick()
        assert(backClicked)
    }

    private fun mockViewModel(state: PokemonCarouselUiState): PokemonDetailViewModel {
        val mock = mockk<PokemonDetailViewModel>(relaxed = true)
        val stateFlow = MutableStateFlow(state).asStateFlow()
        every { mock.uiState } returns stateFlow
        return mock
    }

    private fun createPokemon(
        id: Int,
        name: String,
        description: String = "A Pokemon.",
        category: String = "Seed",
        weight: Int = 69,
        height: Int = 7,
        types: List<PokemonType> = listOf(PokemonType.GRASS),
        stats: List<PokemonStat> = listOf(PokemonStat("HP", 45))
    ) = Pokemon(
        id = id,
        name = name,
        imageUrl = "https://example.com/$id.png",
        types = types,
        stats = stats,
        height = height,
        weight = weight,
        description = description,
        color = PokemonColor.GREEN,
        category = category
    )
}
