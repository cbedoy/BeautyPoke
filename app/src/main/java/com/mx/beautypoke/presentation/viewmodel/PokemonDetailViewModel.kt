package com.mx.beautypoke.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mx.beautypoke.domain.model.Pokemon
import com.mx.beautypoke.domain.model.PokemonCarouselUiState
import com.mx.beautypoke.domain.usecase.GetPokemonRangeUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PokemonDetailViewModel(
    private val getPokemonRangeUseCase: GetPokemonRangeUseCase
) : ViewModel() {

    companion object {
        private const val INITIAL_RANGE_END = 5
        private val ALL_IDS = (1..151).toList()
    }

    private val _uiState = MutableStateFlow<PokemonCarouselUiState>(PokemonCarouselUiState.Loading)
    val uiState: StateFlow<PokemonCarouselUiState> = _uiState.asStateFlow()

    private val loadedPokemon = mutableMapOf<Int, Pokemon>()
    private val loadingIds = mutableSetOf<Int>()

    init {
        loadInitialRange()
    }

    fun onRetry() {
        _uiState.value = PokemonCarouselUiState.Loading
        loadedPokemon.clear()
        loadingIds.clear()
        loadInitialRange()
    }

    fun onPageSelected(index: Int) {
        val currentState = _uiState.value
        if (currentState is PokemonCarouselUiState.Success) {
            _uiState.value = currentState.copy(currentIndex = index)
            prefetchAdjacent(index)
        }
    }

    private fun loadInitialRange() {
        viewModelScope.launch {
            _uiState.value = PokemonCarouselUiState.Loading
            val initialIds = ALL_IDS.take(INITIAL_RANGE_END)
            loadingIds.addAll(initialIds)
            val result = getPokemonRangeUseCase(initialIds)
            loadingIds.removeAll(initialIds)
            result.onSuccess { pokemonList ->
                pokemonList.forEach { loadedPokemon[it.id] = it }
                emitSuccess()
            }.onFailure { e ->
                if (loadedPokemon.isEmpty()) {
                    _uiState.value = PokemonCarouselUiState.Error(
                        e.message ?: "Failed to load Pokemon"
                    )
                }
            }
        }
    }

    private fun prefetchAdjacent(currentIndex: Int) {
        val currentState = _uiState.value
        if (currentState !is PokemonCarouselUiState.Success) return

        val idsToPrefetch = mutableListOf<Int>()
        for (offset in listOf(-1, 1, 2)) {
            val targetIndex = currentIndex + offset
            if (targetIndex in ALL_IDS.indices) {
                val id = ALL_IDS[targetIndex]
                if (id !in loadedPokemon && id !in loadingIds) {
                    idsToPrefetch.add(id)
                }
            }
        }

        if (idsToPrefetch.isEmpty()) return

        loadingIds.addAll(idsToPrefetch)
        idsToPrefetch.forEach { id ->
            viewModelScope.launch {
                val result = getPokemonRangeUseCase(listOf(id))
                loadingIds.remove(id)
                result.onSuccess { pokemonList ->
                    pokemonList.forEach { loadedPokemon[it.id] = it }
                    emitSuccess()
                }
            }
        }
    }

    private fun emitSuccess() {
        val sortedList = ALL_IDS.mapNotNull { loadedPokemon[it] }
        val currentIndex = when (val state = _uiState.value) {
            is PokemonCarouselUiState.Success -> state.currentIndex
            else -> 0
        }
        _uiState.value = PokemonCarouselUiState.Success(
            pokemonList = sortedList,
            currentIndex = currentIndex.coerceIn(0, (sortedList.size - 1).coerceAtLeast(0))
        )
    }
}
