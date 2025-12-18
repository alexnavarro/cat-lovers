package com.alexandrenavarro.catlovers.ui.catslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.alexandrenavarro.catlovers.data.network.Result
import com.alexandrenavarro.catlovers.data.repository.BreedRepository
import com.alexandrenavarro.catlovers.data.repository.FavoriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BreedsScreenViewModel @Inject constructor(
    private val breedRepository: BreedRepository,
    private val favoriteRepository: FavoriteRepository,
): ViewModel()  {

    val breeds = breedRepository
    .getBreeds()
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
                    _events.send(
                        FavoriteUiEvent.Error("Failed to update favorite")
                    )
                }

                is Result.NetworkError -> {
                    _events.send(
                        FavoriteUiEvent.Error("No internet connection")
                    )
                }

                is Result.Success -> {
                    _events.send(
                        if (isFavorite)
                            FavoriteUiEvent.FavoriteRemoved
                        else
                            FavoriteUiEvent.FavoriteAdded
                    )
                }
            }
        }
    }
}

sealed interface FavoriteUiEvent {
    data object FavoriteAdded : FavoriteUiEvent
    data object FavoriteRemoved : FavoriteUiEvent
    data class Error(val message: String) : FavoriteUiEvent
}