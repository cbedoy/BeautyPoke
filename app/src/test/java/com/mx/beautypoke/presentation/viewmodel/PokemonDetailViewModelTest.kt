package com.mx.beautypoke.presentation.viewmodel

import app.cash.turbine.test
import com.mx.beautypoke.domain.model.Pokemon
import com.mx.beautypoke.domain.model.PokemonCarouselUiState
import com.mx.beautypoke.domain.model.PokemonColor
import com.mx.beautypoke.domain.model.PokemonStat
import com.mx.beautypoke.domain.model.PokemonType
import com.mx.beautypoke.domain.usecase.GetPokemonRangeUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest(testDispatcher) {
        val useCase = mockk<GetPokemonRangeUseCase>()
        coEvery { useCase.invoke(any()) } returns Result.success(emptyList())

        val viewModel = PokemonDetailViewModel(useCase)

        viewModel.uiState.test {
            assertEquals(PokemonCarouselUiState.Loading, awaitItem())
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(PokemonCarouselUiState.Success(emptyList(), 0), awaitItem())
        }
    }

    @Test
    fun `loads initial range and emits Success`() = runTest(testDispatcher) {
        val useCase = mockk<GetPokemonRangeUseCase>()
        val pokemonList = listOf(
            createPokemon(1, "Bulbasaur"),
            createPokemon(2, "Ivysaur"),
            createPokemon(3, "Venusaur"),
            createPokemon(4, "Charmander"),
            createPokemon(5, "Charmeleon")
        )
        coEvery { useCase.invoke(any()) } returns Result.success(pokemonList)

        val viewModel = PokemonDetailViewModel(useCase)

        viewModel.uiState.test {
            assertEquals(PokemonCarouselUiState.Loading, awaitItem())
            testDispatcher.scheduler.advanceUntilIdle()
            val successState = awaitItem()
            assertTrue(successState is PokemonCarouselUiState.Success)
            val success = successState as PokemonCarouselUiState.Success
            assertEquals(5, success.pokemonList.size)
            assertEquals(1, success.pokemonList[0].id)
            assertEquals(0, success.currentIndex)
        }
    }

    @Test
    fun `onRetry resets state and reloads`() = runTest(testDispatcher) {
        val useCase = mockk<GetPokemonRangeUseCase>()
        val pokemonList = listOf(createPokemon(1, "Bulbasaur"))
        coEvery { useCase.invoke(any()) } returns Result.success(pokemonList)

        val viewModel = PokemonDetailViewModel(useCase)

        viewModel.uiState.test {
            assertEquals(PokemonCarouselUiState.Loading, awaitItem())
            testDispatcher.scheduler.advanceUntilIdle()
            val successState = awaitItem()
            assertTrue(successState is PokemonCarouselUiState.Success)

            viewModel.onRetry()

            assertEquals(PokemonCarouselUiState.Loading, awaitItem())
            testDispatcher.scheduler.advanceUntilIdle()
            val newSuccessState = awaitItem()
            assertTrue(newSuccessState is PokemonCarouselUiState.Success)
        }
    }

    @Test
    fun `emits Error when use case fails with empty cache`() = runTest(testDispatcher) {
        val useCase = mockk<GetPokemonRangeUseCase>()
        coEvery { useCase.invoke(any()) } returns Result.failure(Exception("API error"))

        val viewModel = PokemonDetailViewModel(useCase)

        viewModel.uiState.test {
            assertEquals(PokemonCarouselUiState.Loading, awaitItem())
            testDispatcher.scheduler.advanceUntilIdle()
            val errorState = awaitItem()
            assertTrue(errorState is PokemonCarouselUiState.Error)
            assertEquals("API error", (errorState as PokemonCarouselUiState.Error).message)
        }
    }

    @Test
    fun `onPageSelected prefetches adjacent pokemon`() = runTest(testDispatcher) {
        val useCase = mockk<GetPokemonRangeUseCase>()
        val initialPokemon = listOf(
            createPokemon(1, "Bulbasaur"),
            createPokemon(2, "Ivysaur")
        )
        coEvery { useCase.invoke(listOf(1, 2, 3, 4, 5)) } returns Result.success(initialPokemon)
        coEvery { useCase.invoke(listOf(3)) } returns Result.success(listOf(createPokemon(3, "Venusaur")))
        coEvery { useCase.invoke(listOf(4)) } returns Result.success(listOf(createPokemon(4, "Charmander")))

        val viewModel = PokemonDetailViewModel(useCase)

        viewModel.uiState.test {
            assertEquals(PokemonCarouselUiState.Loading, awaitItem())
            testDispatcher.scheduler.advanceUntilIdle()
            val successState = awaitItem()
            var success = successState as PokemonCarouselUiState.Success
            assertEquals(2, success.pokemonList.size)
            assertEquals(0, success.currentIndex)

            viewModel.onPageSelected(1)

            success = awaitItem() as PokemonCarouselUiState.Success
            assertEquals(1, success.currentIndex)
            assertEquals(2, success.pokemonList.size)

            testDispatcher.scheduler.advanceUntilIdle()

            success = awaitItem() as PokemonCarouselUiState.Success
            assertEquals(1, success.currentIndex)
            assertTrue(success.pokemonList.size >= 3)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createPokemon(id: Int, name: String) = Pokemon(
        id = id,
        name = name,
        imageUrl = "https://example.com/$id.png",
        types = listOf(PokemonType.GRASS),
        stats = listOf(PokemonStat("HP", 45)),
        height = 7,
        weight = 69,
        description = "Description for $name.",
        color = PokemonColor.GREEN,
        category = "Seed"
    )
}
