package com.alexandrenavarro.catlovers.data.repository

import com.alexandrenavarro.catlovers.domain.model.BreedPreview
import com.alexandrenavarro.catlovers.data.network.Result
import kotlinx.coroutines.flow.Flow

interface BreedRepository {

    suspend fun refreshBreeds(): Result<Unit>

    fun getBreeds(): Flow<List<BreedPreview>>
}