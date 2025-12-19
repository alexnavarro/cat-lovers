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
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import com.alexandrenavarro.catlovers.ui.catslist.BreedsScreen
import com.alexandrenavarro.catlovers.ui.details.BreedDetailScreen
import com.alexandrenavarro.catlovers.ui.favorites.FavoritesScreen

@Serializable
sealed interface AppNavKey

@Serializable
data object BreedsScreen : AppNavKey

@Serializable
data object Favorites : AppNavKey

@Serializable
data class BreedDetail(val breedId: String) : AppNavKey

@Composable
fun CatLoversApp() {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showNavBar = currentDestination?.hasRoute<BreedDetail>() == false

    NavigationSuiteScaffold(
        layoutType = if (showNavBar) NavigationSuiteType.NavigationBar
        else NavigationSuiteType.None, // This hides the bar
        navigationSuiteItems = {
            item(
                icon = { Icon(Icons.Default.Home, null) },
                label = { Text("Cats List") },
                selected = currentDestination?.hasRoute<BreedsScreen>() == true,
                onClick = { navController.navigate(BreedsScreen) }
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
            startDestination = BreedsScreen
        ) {
            composable<BreedsScreen> {
                BreedsScreen(onCatClicked = { id ->
                    navController.navigate(BreedDetail(id))
                })
            }

            composable<Favorites> {
                FavoritesScreen(onFavoriteClicked = { id ->
                    navController.navigate(BreedDetail(id))
                })
            }

            composable<BreedDetail> { backStackEntry ->
                val args = backStackEntry.toRoute<BreedDetail>()
                BreedDetailScreen(
                    breedId = args.breedId,
//                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}