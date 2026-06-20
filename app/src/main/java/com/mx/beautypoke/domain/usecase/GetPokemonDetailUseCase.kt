package com.mx.beautypoke.domain.usecase

import com.mx.beautypoke.domain.model.Pokemon
import com.mx.beautypoke.domain.repository.PokemonRepository

class GetPokemonDetailUseCase(
    private val repository: PokemonRepository
) {
    suspend operator fun invoke(id: Int): Result<Pokemon> {
        return repository.getPokemonDetail(id)
    }
}
