package com.alexandrenavarro.catlovers.ui.catslist

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.alexandrenavarro.catlovers.domain.model.BreedPreview
import com.alexandrenavarro.catlovers.ui.theme.CatLoversTheme
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedsScreen(
    modifier: Modifier = Modifier,
    viewModel: BreedsScreenViewModel = hiltViewModel()
) {
    val breeds = viewModel.breeds.collectAsLazyPagingItems()
    val query by viewModel.query.collectAsState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val snackbarHostState = remember { SnackbarHostState() }

    val appendError = breeds.loadState.append as? LoadState.Error
    LaunchedEffect(appendError) {
        appendError?.let {
            Log.d("Paging", "append = ${breeds.loadState.append}")
            val result = snackbarHostState.showSnackbar(
                message = "Error loading more",
                actionLabel = "Try again"
            )
            if (result == SnackbarResult.ActionPerformed) {
                breeds.retry()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                FavoriteUiEvent.FavoriteAdded ->
                    snackbarHostState.showSnackbar("Added to favorites")

                FavoriteUiEvent.FavoriteRemoved ->
                    snackbarHostState.showSnackbar("Removed from favorites")

                is FavoriteUiEvent.Error ->
                    snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    BreedsScreenBackground(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.navigationBars),
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
            topBar = {
                Column {
                    TopAppBar(
                        title = { Text("Cat Lovers") }
                    )

                    DockedSearchBar(
                        query = query ?: "",
                        onQueryChange = {
                            viewModel.setQuery(it)
                            if (it.isEmpty()) {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                        },
                        onSearch = {
                            viewModel.setQuery(it)
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        },
                        active = false,
                        onActiveChange = { },
                        placeholder = { Text("Search breeds") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {}
                }
            }

        ) { padding ->
            BreedsGrid(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                breeds = breeds,
                onFavoriteClick = viewModel::onFavoriteClicked

            )
        }

        if (breeds.loadState.refresh is LoadState.Loading) {
            FullScreenLoading()
        }

        val refreshError = breeds.loadState.refresh as? LoadState.Error
        if (refreshError != null && breeds.itemCount == 0) {
            FullScreenError(
                onRetry = { breeds.retry() }
            )
        }
    }
}

@Composable
fun BreedsGrid(
    modifier: Modifier = Modifier,
    breeds: LazyPagingItems<BreedPreview>,
    onFavoriteClick: (imageId: String, isFavorite: Boolean) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        columns = GridCells.Adaptive(minSize = 160.dp),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(count = breeds.itemCount, key = breeds.itemKey { it.id }) { index ->
            val breed = breeds[index]
            breed?.let { item ->
                CatCard(
                    breed = item,
                    onFavoriteClick = onFavoriteClick
                )
            }
        }

        if (breeds.loadState.append is LoadState.Loading) {
            item {
                FooterLoading()
            }
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
            breeds = breeds.collectAsMutableLazyPagingItems(),
            onFavoriteClick = { _, _ -> }

        )
    }

}

@Composable
fun CatCard(
    modifier: Modifier = Modifier,
    breed: BreedPreview,
    onFavoriteClick: (imageId: String, isFavorite: Boolean) -> Unit,
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = minTouchTargetSize)
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
                            onClick = { onFavoriteClick(breed.imageId, breed.isFavorite) }
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
            onFavoriteClick = { _, _ -> }, breed = BreedPreview(
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

@Composable
private fun <T : Any> List<T>.collectAsMutableLazyPagingItems(): LazyPagingItems<T> {
    val flow = flowOf(PagingData.from(this))
    return flow.collectAsLazyPagingItems()
}

@Composable
fun FullScreenError(
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Error loading breeds")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onRetry) {
                Text("Try again")
            }
        }
    }
}

@Composable
fun FooterLoading() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(strokeWidth = 2.dp)
    }
}

@Composable
fun FullScreenLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
