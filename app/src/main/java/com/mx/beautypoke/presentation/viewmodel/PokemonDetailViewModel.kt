package com.mx.beautypoke.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mx.beautypoke.domain.model.Pokemon
import com.mx.beautypoke.domain.model.PokemonColor
import com.mx.beautypoke.domain.model.PokemonDetailUiState
import com.mx.beautypoke.domain.model.PokemonStat
import com.mx.beautypoke.domain.model.PokemonType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PokemonDetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<PokemonDetailUiState>(PokemonDetailUiState.Loading)
    val uiState: StateFlow<PokemonDetailUiState> = _uiState.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    init {
        loadPokemonDetail()
    }

    fun onTabSelected(index: Int) {
        _selectedTab.value = index
    }

    fun onRetry() {
        _uiState.value = PokemonDetailUiState.Loading
        loadPokemonDetail()
    }

    private fun loadPokemonDetail() {
        viewModelScope.launch {
            delay(800)
            _uiState.value = PokemonDetailUiState.Success(
                Pokemon(
                    id = 6,
                    name = "Charizard",
                    imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/6.png",
                    types = listOf(PokemonType.FIRE, PokemonType.FLYING),
                    stats = listOf(
                        PokemonStat("HP", 78),
                        PokemonStat("Ataque", 84),
                        PokemonStat("Defensa", 78),
                        PokemonStat("Ataque Esp.", 109),
                        PokemonStat("Defensa Esp.", 85),
                        PokemonStat("Velocidad", 100)
                    ),
                    height = 17,
                    weight = 905,
                    description = "Escupe un fuego tan caliente que funde las rocas. Causa incendios forestales sin querer.",
                    color = PokemonColor.RED
                )
            )
        }
    }
}
