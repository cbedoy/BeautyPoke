package com.mx.beautypoke.data.repository

import com.mx.beautypoke.data.local.dao.PokemonDao
import com.mx.beautypoke.data.local.entity.PokemonEntity
import com.mx.beautypoke.data.local.mapper.PokemonEntityMapper
import com.mx.beautypoke.data.remote.api.PokemonApiService
import com.mx.beautypoke.data.remote.dto.PokemonResponse
import com.mx.beautypoke.data.remote.dto.PokemonSpeciesResponse
import com.mx.beautypoke.data.remote.mapper.PokemonMapper
import com.mx.beautypoke.domain.model.Pokemon
import com.mx.beautypoke.domain.model.PokemonColor
import com.mx.beautypoke.domain.model.PokemonStat
import com.mx.beautypoke.domain.model.PokemonType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PokemonRepositoryImplTest {

    private val api = mockk<PokemonApiService>()
    private val dao = mockk<PokemonDao>()
    private val repository = PokemonRepositoryImpl(api, dao)

    @Test
    fun `getPokemonDetail returns cached entity when available`() = runTest {
        val cachedEntity = PokemonEntity(
            id = 1,
            name = "Bulbasaur",
            imageUrl = "https://example.com/1.png",
            typesJson = """["GRASS","POISON"]""",
            statsJson = """[{"name":"HP","value":45}]""",
            height = 7,
            weight = 69,
            description = "Seed Pokémon.",
            colorName = "GREEN",
            category = "Seed Pokémon"
        )
        coEvery { dao.getById(1) } returns cachedEntity

        val result = repository.getPokemonDetail(1)

        assertTrue(result.isSuccess)
        val pokemon = result.getOrNull()
        assertNotNull(pokemon)
        assertEquals(1, pokemon!!.id)
        assertEquals("Bulbasaur", pokemon.name)
        assertEquals(PokemonColor.GREEN, pokemon.color)
        coVerify(exactly = 0) { api.getPokemon(any()) }
        coVerify(exactly = 0) { api.getPokemonSpecies(any()) }
    }

    @Test
    fun `getPokemonDetail fetches and caches when not cached`() = runTest {
        val pokemonResponse = mockk<PokemonResponse>(relaxed = true)
        val speciesResponse = mockk<PokemonSpeciesResponse>(relaxed = true)

        coEvery { dao.getById(1) } returns null
        coEvery { api.getPokemon(1) } returns pokemonResponse
        coEvery { api.getPokemonSpecies(1) } returns speciesResponse
        coEvery { dao.upsert(any()) } returns Unit

        val result = repository.getPokemonDetail(1)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { api.getPokemon(1) }
        coVerify(exactly = 1) { api.getPokemonSpecies(1) }
        coVerify(exactly = 1) { dao.upsert(any()) }
    }

    @Test
    fun `getPokemonDetail returns failure when API fails and not cached`() = runTest {
        val errorMessage = "Network error"
        coEvery { dao.getById(1) } returns null
        coEvery { api.getPokemon(1) } throws RuntimeException(errorMessage)

        val result = repository.getPokemonDetail(1)

        assertTrue(result.isFailure)
        assertEquals(errorMessage, result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { dao.upsert(any()) }
    }
}
