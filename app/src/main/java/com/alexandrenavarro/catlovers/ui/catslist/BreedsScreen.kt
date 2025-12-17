package com.alexandrenavarro.catlovers.ui.catslist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.alexandrenavarro.catlovers.domain.model.BreedPreview
import com.alexandrenavarro.catlovers.ui.theme.CatLoversTheme

@Composable
fun BreedsScreen(
    modifier: Modifier = Modifier,
    viewModel: BreedsScreenViewModel = hiltViewModel()
) {
    val breeds by viewModel.breeds.collectAsState()

    BreedsScreen(
        modifier = modifier,
        breeds = breeds
    )
}

@Composable
fun BreedsScreen(
    modifier: Modifier = Modifier,
    breeds: List<BreedPreview>,
) {
    BreedsScreenBackground(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.navigationBars),
    ) {
        Scaffold(
            contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
        ) { padding ->
            BreedsGrid(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                breeds = breeds,
                onFavoriteClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BreedsScreenPreview() {
    val breeds = List(20) { idx ->
        BreedPreview(
            name = "Abyssinian",
            imageUrl = "https://cdn2.thecatapi.com/images/0XYvRd7oD.jpg",
            imageId = "0XYvRd7oD",
            id = "abys$idx",
            isFavorite = false
        )
    }

    CatLoversTheme {
        BreedsScreen(
            breeds = breeds,
        )
    }

}

@Composable
fun BreedsGrid(
    modifier: Modifier = Modifier,
    breeds: List<BreedPreview>,
    onFavoriteClick: (id: String) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        columns = GridCells.Adaptive(minSize = 160.dp),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(breeds) { breed ->
            CatCard(
                breed = breed,
                onFavoriteClick = onFavoriteClick,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BreedsGridPreview() {
    val breeds = List(20) { idx ->
        BreedPreview(
            name = "Abyssinian",
            imageUrl = "https://cdn2.thecatapi.com/images/0XYvRd7oD.jpg",
            imageId = "0XYvRd7oD",
            id = "abys$idx",
            isFavorite = false
        )
    }

    CatLoversTheme {
        BreedsGrid(
            breeds = breeds,
            onFavoriteClick = {}
        )
    }

}

@Composable
fun CatCard(
    modifier: Modifier = Modifier,
    breed: BreedPreview,
    onFavoriteClick: (id: String) -> Unit,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(breed.imageUrl)
                    .crossfade(true)
                    .size(400)
                    .build(),
                contentDescription = breed.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f),
                alignment = Alignment.TopCenter,
                error = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .aspectRatio(4f / 3f)
                            .background(Color(0xFFE0E0E0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Pets,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .aspectRatio(4f / 3f)
                        )
                    }
                }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.Black.copy(alpha = 0.1f)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {

                val minTouchTargetSize = 48.dp
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().
                    heightIn(min = minTouchTargetSize)
                ) {
                    Text(
                        text = breed.name,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    if (breed.imageId?.isNotEmpty() == true) {
                        FavoriteButton(
                            isFavorite = breed.isFavorite,
                            onClick = { onFavoriteClick(breed.imageId) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CatCardPreview() {
    CatLoversTheme {
        CatCard(
            onFavoriteClick = {}, breed = BreedPreview(
                name = "Abyssinian",
                imageUrl = "https://cdn2.thecatapi.com/images/0XYvRd7oD.jpg",
                imageId = "0XYvRd7oD",
                id = "abys",
                isFavorite = false
            )
        )
    }

}

@Composable
fun CatImage(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    contentDescription: String
) {
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .size(400)
            .build(),
        contentDescription = contentDescription,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun FavoriteButton(
    isFavorite: Boolean,
    onClick: () -> Unit
) {
    FilledTonalIconButton(onClick = onClick) {
        Icon(
            imageVector = if (isFavorite)
                Icons.Filled.Favorite
            else
                Icons.Outlined.FavoriteBorder,
            contentDescription = null
        )
    }
}

@Composable
private fun BreedsScreenBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        content()
    }
}