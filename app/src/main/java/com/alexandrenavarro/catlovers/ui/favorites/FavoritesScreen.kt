package com.alexandrenavarro.catlovers.ui.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.alexandrenavarro.catlovers.domain.model.FavoriteBreed
import com.alexandrenavarro.catlovers.ui.theme.CatLoversTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScree(
    modifier: Modifier = Modifier,
    onFavoriteClicked: (breedId: String, imageId: String) -> Unit,
    viewModel: FavoriteScreenViewModel = hiltViewModel()
) {
    val favorites by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.navigationBars),
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
        topBar = {
            TopAppBar(
                title = { Text("Favorites") }
            )
        }
    ) { padding ->
        FavoritesContent(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            onFavoriteClicked = onFavoriteClicked,
            favorites = favorites
        )
    }
}

@Composable
fun FavoritesContent(
    modifier: Modifier = Modifier,
    onFavoriteClicked: (breedId: String, imageId: String) -> Unit,
    favorites: List<FavoriteBreed>
) {
    if (favorites.isEmpty()) {
        EmptyFavoritesContent()
    } else {
        LazyVerticalGrid(
            modifier = modifier,
            columns = GridCells.Adaptive(minSize = 160.dp),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(favorites, key = { it.favoriteId }) { favorite ->
                FavoriteCatCard(
                    favoriteBreed = favorite,
                    onFavoriteClicked = onFavoriteClicked
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FavoritesContentPreview() {
    val favorites = List(2) { index ->
        FavoriteBreed(
            breedId = "abys$index",
            imageUrl = "https://cdn2.thecatapi.com/images/0XYvRd7oD.jpg",
            favoriteId = index.toLong(),
            lifeSpan = 19,
            imageId = "0XYvRd7oD",
        )
    }

    CatLoversTheme {
        FavoritesContent(
            favorites = favorites,
            onFavoriteClicked = { _, _ -> }
        )
    }
}

@Composable
fun FavoriteCatCard(
    modifier: Modifier = Modifier,
    favoriteBreed: FavoriteBreed,
    onFavoriteClicked: (breedId: String, imageId: String) -> Unit,
) {
    Card(
        modifier = modifier.clickable { onFavoriteClicked(favoriteBreed.breedId, favoriteBreed.imageId) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(favoriteBreed.imageUrl)
                    .crossfade(true)
                    .size(400)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f),
                alignment = Alignment.Center,
                error = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .aspectRatio(4f / 3f)
                            .background(Color(0xFFE0E0E0)),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Icon(
                            imageVector = Icons.Default.Pets,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Cake,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "AVERAGE LIFESPAN",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "${favoriteBreed.lifeSpan} years",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FavoriteCatCardPreview() {
    CatLoversTheme {
        FavoriteCatCard(
            favoriteBreed = FavoriteBreed(
                breedId = "abys",
                imageUrl = "https://cdn2.thecatapi.com/images/0XYvRd7oD.jpg",
                favoriteId = 44L,
                lifeSpan = 19,
                imageId = "0XYvRd7oD"
            ),
            onFavoriteClicked = { _, _ -> }
        )
    }
}

@Composable
fun EmptyFavoritesContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.FavoriteBorder,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "There are no favorites yet",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )
    }
}