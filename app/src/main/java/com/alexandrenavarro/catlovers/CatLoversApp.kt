package com.alexandrenavarro.catlovers

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.alexandrenavarro.catlovers.ui.catslist.CatBreedsScreen
import com.alexandrenavarro.catlovers.ui.details.CatBreedDetailScreen
import com.alexandrenavarro.catlovers.ui.favorites.FavoritesScree
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppNavKey

@Serializable
data object CatBreedsScreen : AppNavKey

@Serializable
data object Favorites : AppNavKey

@Serializable
data class CatBreedDetail(val breedId: String, val imageId: String) : AppNavKey

@Composable
fun CatLoversApp() {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val isOnDetailScreen = currentDestination?.hasRoute<CatBreedDetail>() == true

    NavigationSuiteScaffold(
        layoutType = if (!isOnDetailScreen) NavigationSuiteType.NavigationBar
        else NavigationSuiteType.None, // This hides the bar
        navigationSuiteItems = {
            item(
                icon = { Icon(Icons.Default.Home, null) },
                label = { Text("Cats List") },
                selected = currentDestination?.hasRoute<CatBreedsScreen>() == true,
                onClick = { navController.navigate(CatBreedsScreen) }
            )
            item(
                icon = { Icon(Icons.Default.Favorite, null) },
                label = { Text("Favorites") },
                selected = currentDestination?.hasRoute<Favorites>() == true,
                onClick = { navController.navigate(Favorites) }
            )
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = CatBreedsScreen
        ) {
            composable<CatBreedsScreen> {
                CatBreedsScreen(onCatClicked = { breedId, imageId ->
                    navController.navigate(CatBreedDetail(breedId = breedId, imageId = imageId))
                })
            }

            composable<Favorites> {
                FavoritesScree(onFavoriteClicked = { breedId, imageId ->
                    navController.navigate(CatBreedDetail(breedId = breedId, imageId = imageId))
                })
            }

            composable<CatBreedDetail> { _->
                CatBreedDetailScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}