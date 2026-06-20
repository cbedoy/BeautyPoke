package com.mx.beautypoke

import android.app.Application
import com.mx.beautypoke.di.appModules
import org.koin.core.context.startKoin

class BeautyPokeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(appModules)
        }
    }
}
