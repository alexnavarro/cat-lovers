package com.alexandrenavarro.catlovers.ui.catslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.alexandrenavarro.catlovers.data.network.Result
import com.alexandrenavarro.catlovers.data.repository.CatBreedRepository
import com.alexandrenavarro.catlovers.data.repository.FavoriteRepository
import com.alexandrenavarro.catlovers.domain.model.CatBreedPreview
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class CatBreedsScreenViewModel @Inject constructor(
    private val catBreedRepository: CatBreedRepository,
    private val favoriteRepository: FavoriteRepository,
) : ViewModel() {
    private val _query = MutableStateFlow<String?>(null)
    val query: StateFlow<String?> = _query

    private val favorites =
        favoriteRepository.observeFavorites()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptySet()
            )

    val breeds: Flow<PagingData<CatBreedPreviewWithFavorite>> = query
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { q ->
            catBreedRepository.getCatBreeds(q)
        }
        .combine(favorites) { pagingData, favoriteIds ->
            pagingData.map { breed ->
                breed.toEternalModel(favoriteIds.contains(breed.imageId))
            }
        }
        .cachedIn(viewModelScope)

    private val _events = Channel<FavoriteUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onFavoriteClicked(imageId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            val result = if (isFavorite) {
                favoriteRepository.deleteFavorite(imageId)
            } else {
                favoriteRepository.addFavorite(imageId)
            }

            when (result) {
                is Result.Error -> {
                    _events.trySend(
                        FavoriteUiEvent.Error("Failed to update favorite")
                    )
                }

                is Result.NetworkError -> {
                    _events.trySend(
                        FavoriteUiEvent.Error("No internet connection")
                    )
                }

                is Result.Success -> {
                    _events.trySend(
                        if (isFavorite)
                            FavoriteUiEvent.FavoriteRemoved
                        else
                            FavoriteUiEvent.FavoriteAdded
                    )
                }
            }
        }
    }

    fun setQuery(value: String) {
        _query.value = value.ifBlank { null }//This is necessary to show the initial list
    }
}

sealed interface FavoriteUiEvent {
    data object FavoriteAdded : FavoriteUiEvent
    data object FavoriteRemoved : FavoriteUiEvent
    data class Error(val message: String) : FavoriteUiEvent
}

data class CatBreedPreviewWithFavorite(
    val id: String,
    val name: String,
    val imageUrl: String?,
    val imageId: String?,
    val isFavorite: Boolean,
)

private fun CatBreedPreview.toEternalModel(isFavorite: Boolean) = CatBreedPreviewWithFavorite(
    id = id,
    name = name,
    imageUrl = imageUrl,
    imageId = imageId,
    isFavorite = isFavorite,
)



