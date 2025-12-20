package com.alexandrenavarro.catlovers.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexandrenavarro.catlovers.data.network.Result
import com.alexandrenavarro.catlovers.data.repository.CatBreedRepository
import com.alexandrenavarro.catlovers.data.repository.FavoriteRepository
import com.alexandrenavarro.catlovers.domain.model.CatBreedDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.inc


sealed class CatBreedDetailUiState {
    object Loading : CatBreedDetailUiState()
    data class Success(val catBreedDetail: CatBreedDetail, val isFavorite: Boolean = false) :
        CatBreedDetailUiState()

    object Error : CatBreedDetailUiState()
}

@HiltViewModel
class CatBreedDetailScreenViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val catBreedRepository: CatBreedRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val breedId: String = checkNotNull(savedStateHandle["breedId"])
    private val imageId: String = checkNotNull(savedStateHandle["imageId"])

    private val _retryTrigger = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val breedApiFlow = _retryTrigger
        .flatMapLatest {
            flow {
                emit(catBreedRepository.getCatBreedDetail(breedId))
            }
        }
        .shareIn(viewModelScope, SharingStarted.Lazily, replay = 1)

    private val isFavoriteFlow = favoriteRepository.isFavorite(imageId)

    val uiState: StateFlow<CatBreedDetailUiState> = combine(
        breedApiFlow,
        isFavoriteFlow
    ) { breedResult, isFavorite ->
        when (breedResult) {
            is Result.Success -> CatBreedDetailUiState.Success(
                catBreedDetail = breedResult.data,
                isFavorite = isFavorite
            )

            is Result.Error -> CatBreedDetailUiState.Error
            else -> CatBreedDetailUiState.Loading
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CatBreedDetailUiState.Loading)


    fun onFavoriteToggle() {
        viewModelScope.launch {
            if (uiState.value is CatBreedDetailUiState.Success) {
                val currentFav = (uiState.value as CatBreedDetailUiState.Success).isFavorite
                if (currentFav) {
                    favoriteRepository.deleteFavorite(imageId)
                } else {
                    favoriteRepository.addFavorite(imageId)
                }
            }
        }
    }

    fun retry() {
        _retryTrigger.value++
    }
}