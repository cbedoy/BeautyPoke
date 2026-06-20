package com.mx.beautypoke.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mx.beautypoke.domain.model.PokemonDetailUiState
import com.mx.beautypoke.domain.usecase.GetPokemonDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PokemonDetailViewModel(
    private val getPokemonDetailUseCase: GetPokemonDetailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PokemonDetailUiState>(PokemonDetailUiState.Loading)
    val uiState: StateFlow<PokemonDetailUiState> = _uiState.asStateFlow()

    init {
        loadPokemonDetail()
    }

    fun onRetry() {
        _uiState.value = PokemonDetailUiState.Loading
        loadPokemonDetail()
    }

    private fun loadPokemonDetail() {
        viewModelScope.launch {
            _uiState.value = PokemonDetailUiState.Loading
            val result = getPokemonDetailUseCase(6)
            _uiState.value = result.fold(
                onSuccess = { PokemonDetailUiState.Success(it) },
                onFailure = { PokemonDetailUiState.Error(it.message ?: "Unknown error") }
            )
        }
    }
}
