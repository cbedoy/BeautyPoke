package com.mx.beautypoke.di

import com.mx.beautypoke.data.repository.PokemonRepositoryImpl
import com.mx.beautypoke.domain.repository.PokemonRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<PokemonRepository> {
        PokemonRepositoryImpl(api = get())
    }
}
