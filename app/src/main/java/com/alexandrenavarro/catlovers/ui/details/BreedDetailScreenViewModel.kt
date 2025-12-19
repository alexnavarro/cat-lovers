package com.alexandrenavarro.catlovers.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexandrenavarro.catlovers.data.network.Result
import com.alexandrenavarro.catlovers.data.repository.BreedRepository
import com.alexandrenavarro.catlovers.data.repository.FavoriteRepository
import com.alexandrenavarro.catlovers.domain.model.BreedDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed class BreedDetailUiState {
    object Loading : BreedDetailUiState()
    data class Success(val breedDetail: BreedDetail, val isFavorite: Boolean = false) :
        BreedDetailUiState()

    object Error : BreedDetailUiState()
}

@HiltViewModel
class BreedDetailScreenViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val breedRepository: BreedRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val breedId: String = checkNotNull(savedStateHandle["breedId"])
    private val imageId: String = checkNotNull(savedStateHandle["imageId"])

    private val breedApiFlow = flow {
        emit(breedRepository.getBreedDetail(breedId))
    }

    private val isFavoriteFlow = favoriteRepository.isFavorite(imageId)

    val uiState: StateFlow<BreedDetailUiState> = combine(
        breedApiFlow,
        isFavoriteFlow
    ) { breedResult, isFavorite ->
        when (breedResult) {
            is Result.Success -> BreedDetailUiState.Success(
                breedDetail = breedResult.data,
                isFavorite = isFavorite
            )

            is Result.Error -> BreedDetailUiState.Error
            else -> BreedDetailUiState.Loading
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BreedDetailUiState.Loading)


    fun onFavoriteToggle(imageId: String) {
        viewModelScope.launch {
            if (uiState.value is BreedDetailUiState.Success) {
                val currentFav = (uiState.value as BreedDetailUiState.Success).isFavorite
                if (currentFav) {
                    favoriteRepository.deleteFavorite(imageId)
                } else {
                    favoriteRepository.addFavorite(imageId)
                }
            }
        }
    }
}