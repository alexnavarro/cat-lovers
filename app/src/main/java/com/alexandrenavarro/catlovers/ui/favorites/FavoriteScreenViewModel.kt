package com.alexandrenavarro.catlovers.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexandrenavarro.catlovers.data.repository.FavoriteRepository
import com.alexandrenavarro.catlovers.domain.model.FavoriteBreed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteScreenViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
) : ViewModel() {

    init {
        viewModelScope.launch {
            favoriteRepository.syncFavorites()
        }
    }

    val uiState: StateFlow<List<FavoriteBreed>> = favoriteRepository
        .getFavoriteBreeds()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}