package com.alexandrenavarro.catlovers.ui.details

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.alexandrenavarro.catlovers.domain.model.BreedDetail
import com.alexandrenavarro.catlovers.ui.theme.CatLoversTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: BreedDetailScreenViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },

        floatingActionButton = {
            if (state is BreedDetailUiState.Success) {
                val data = (state as BreedDetailUiState.Success)

                if (data.breedDetail.imageId != null) {
                    FavoriteFab(
                        isFavorite = data.isFavorite,
                        onClick = { viewModel.onFavoriteToggle(data.breedDetail.imageId) }
                    )
                }
            }
        }
    ) { padding ->
        when (val s = state) {
            is BreedDetailUiState.Success -> BreedDetailScreen(
                breedDetail = s.breedDetail,
                paddingValues = padding
            )

            is BreedDetailUiState.Loading -> LoadingCircle()
            is BreedDetailUiState.Error -> ErrorState()
        }
    }
}


@Composable
fun BreedDetailScreen(
    modifier: Modifier = Modifier,
    breedDetail: BreedDetail,
    paddingValues: PaddingValues
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
    ) {
        CatBreedImage(imageUrl = breedDetail.imageUrl)
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = breedDetail.name,
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.Gray
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Origin: ${breedDetail.origin}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            HorizontalDivider(
                Modifier.padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )

            Text(
                text = "Temperament",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                breedDetail.temperament.split(",").forEach { tag ->
                    SuggestionChip(
                        onClick = {},
                        label = { Text(tag.trim()) },
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "About this breed",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = breedDetail.description,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp,
                textAlign = TextAlign.Justify
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBreedDetailScreen() {

    val breedDetail = BreedDetail(
        id = "1",
        name = "Bengal",
        origin = "United States",
        temperament = "Alert, Agile, Energetic, Demanding",
        description = "The Bengal is a domesticated cat breed created from hybrids of domestic cats and the Asian leopard cat.",
        imageUrl = "https://cdn2.thecatapi.com/images/O3btzLlsO.png",
        imageId = "O3btzLlsO"
    )

    CatLoversTheme {
        BreedDetailScreen(
            breedDetail = breedDetail,
            paddingValues = PaddingValues()
        )
    }
}

@Composable
fun LoadingCircle() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorState(
    message: String = "Something went wrong",
    onRetry: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            if (onRetry != null) {
                Spacer(Modifier.height(16.dp))
                Button(onClick = onRetry) {
                    Text("Retry")
                }
            }
        }
    }
}

@Composable
fun FavoriteFab(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isFavorite) 1.2f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "FavoriteScale"
    )

    FloatingActionButton(
        onClick = onClick,
        containerColor = if (isFavorite) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        contentColor = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        shape = CircleShape
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .graphicsLayer(scaleX = scale, scaleY = scale)
        )
    }
}

@Composable
private fun CatBreedImagePlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Pets,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.size(64.dp)
        )
    }
}

@Composable
fun CatBreedImage(
    imageUrl: String?,
    modifier: Modifier = Modifier
) {
//    Box(
//        modifier = modifier
//            .fillMaxWidth()
//            .aspectRatio(4f / 3f)
//            .background(MaterialTheme.colorScheme.surfaceVariant),
//        contentAlignment = Alignment.Center
//    ) {
//        SubcomposeAsyncImage(
//            model = ImageRequest.Builder(LocalContext.current)
//                .data(imageUrl)
//                .crossfade(true)
//                .size(100)
//                .build(),
//            contentDescription = null,
//            contentScale = ContentScale.Crop,
//            alignment = Alignment.TopCenter,
//            modifier = Modifier.fillMaxSize(),
//            loading = { CatBreedImagePlaceholder() },
//            error = { CatBreedImagePlaceholder() }
//        )
//    }

    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        contentDescription = null,
        // FillWidth faz a imagem ocupar toda a largura e
        // crescer a altura proporcionalmente ao arquivo original.
        contentScale = ContentScale.FillWidth,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight() // A altura é definida pela imagem
            .background(MaterialTheme.colorScheme.surfaceVariant),
        loading = {
            CatBreedImagePlaceholder()
        },
        error = {
            CatBreedImagePlaceholder()
        }
    )
}