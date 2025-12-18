package com.alexandrenavarro.catlovers.data.repository

import androidx.paging.PagingData
import com.alexandrenavarro.catlovers.domain.model.BreedPreview
import com.alexandrenavarro.catlovers.data.network.Result
import kotlinx.coroutines.flow.Flow

interface BreedRepository {

    fun getBreeds(): Flow<PagingData<BreedPreview>>

    suspend fun addFavorite(imageId: String): Result<Unit>

    suspend fun deleteFavorite(imageId: String): Result<Unit>
}