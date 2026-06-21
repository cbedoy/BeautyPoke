package com.mx.beautypoke.di

import com.mx.beautypoke.domain.usecase.GetPokemonRangeUseCase
import com.mx.beautypoke.presentation.viewmodel.PokemonDetailViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val useCaseModule = module {

    factory {
        GetPokemonRangeUseCase(repository = get())
    }
}

val viewModelModule = module {

    viewModel {
        PokemonDetailViewModel(getPokemonRangeUseCase = get())
    }
}

val appModules = listOf(networkModule, databaseModule, repositoryModule, useCaseModule, viewModelModule)
