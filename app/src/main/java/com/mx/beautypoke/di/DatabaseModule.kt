package com.mx.beautypoke.di

import androidx.room.Room
import com.mx.beautypoke.data.local.database.BeautyPokeDatabase
import org.koin.dsl.module

val databaseModule = module {

    single {
        Room.databaseBuilder(
            get(),
            BeautyPokeDatabase::class.java,
            "beautypoke_database"
        ).build()
    }

    single {
        get<BeautyPokeDatabase>().pokemonDao()
    }
}
