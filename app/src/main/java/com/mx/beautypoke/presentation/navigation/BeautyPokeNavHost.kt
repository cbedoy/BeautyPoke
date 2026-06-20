package com.mx.beautypoke.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mx.beautypoke.presentation.screen.PokemonDetailScreen
import com.mx.beautypoke.presentation.viewmodel.PokemonDetailViewModel

object Routes {
    const val POKEMON_DETAIL = "pokemon_detail"
}

@Composable
fun BeautyPokeNavHost(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Routes.POKEMON_DETAIL
    ) {
        composable(Routes.POKEMON_DETAIL) {
            val viewModel: PokemonDetailViewModel = viewModel()
            PokemonDetailScreen(
                viewModel = viewModel,
                onBackClick = { /* No previous screen to go back to */ }
            )
        }
    }
}
