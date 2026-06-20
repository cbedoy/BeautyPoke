package com.mx.beautypoke.domain.model

sealed interface PokemonCarouselUiState {
    data object Loading : PokemonCarouselUiState
    data class Success(
        val pokemonList: List<Pokemon>,
        val currentIndex: Int
    ) : PokemonCarouselUiState
    data class Error(val message: String) : PokemonCarouselUiState
}
