package com.alexandrenavarro.catlovers.data.repository

import com.alexandrenavarro.catlovers.data.model.BreedPreview
import kotlinx.coroutines.flow.Flow

interface BreedRepository {

    suspend fun refreshBreeds(): Result<Unit>

    fun getBreeds(): Flow<List<BreedPreview>>
}