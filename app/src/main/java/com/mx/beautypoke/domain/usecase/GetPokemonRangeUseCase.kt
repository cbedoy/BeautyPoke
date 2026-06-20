package com.mx.beautypoke.domain.usecase

import com.mx.beautypoke.domain.model.Pokemon
import com.mx.beautypoke.domain.repository.PokemonRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class GetPokemonRangeUseCase(
    private val repository: PokemonRepository
) {
    suspend operator fun invoke(ids: List<Int>): Result<List<Pokemon>> {
        return runCatching {
            coroutineScope {
                ids.map { id ->
                    async {
                        repository.getPokemonDetail(id)
                    }
                }.mapNotNull { deferred ->
                    deferred.await().getOrNull()
                }
            }
        }
    }
}
