package com.alexandrenavarro.catlovers.ui.catslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.alexandrenavarro.catlovers.data.repository.BreedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel()
class BreedsScreenViewModel @Inject constructor(
    private val breedRepository: BreedRepository,
): ViewModel()  {

    val breeds = breedRepository
    .getBreeds()
    .cachedIn(viewModelScope)

}