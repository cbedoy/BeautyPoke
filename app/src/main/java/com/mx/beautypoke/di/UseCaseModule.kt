package com.mx.beautypoke.di

import com.mx.beautypoke.domain.usecase.GetPokemonDetailUseCase
import com.mx.beautypoke.presentation.viewmodel.PokemonDetailViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val useCaseModule = module {

    factory {
        GetPokemonDetailUseCase(repository = get())
    }
}

val viewModelModule = module {

    viewModel {
        PokemonDetailViewModel(getPokemonDetailUseCase = get())
    }
}

val appModules = listOf(networkModule, repositoryModule, useCaseModule, viewModelModule)
