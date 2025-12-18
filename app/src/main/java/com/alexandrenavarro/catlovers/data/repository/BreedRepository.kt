package com.alexandrenavarro.catlovers.data.repository

import androidx.paging.PagingData
import com.alexandrenavarro.catlovers.domain.model.BreedPreview
import kotlinx.coroutines.flow.Flow

interface BreedRepository {

    fun getBreeds(query: String?): Flow<PagingData<BreedPreview>>
}