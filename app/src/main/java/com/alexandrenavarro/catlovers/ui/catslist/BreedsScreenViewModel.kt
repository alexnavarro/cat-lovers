package com.alexandrenavarro.catlovers.ui.catslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexandrenavarro.catlovers.data.repository.BreedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel()
class BreedsScreenViewModel @Inject constructor(
    private val breedRepository: BreedRepository,
): ViewModel()  {

    val breeds = breedRepository.getBreeds().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            breedRepository.refreshBreeds()
        }
    }
}