package com.alexandrenavarro.catlovers.data.repository

import com.alexandrenavarro.catlovers.data.model.BreedPreview
import com.alexandrenavarro.catlovers.data.network.BreedRemoteDataSource
import com.alexandrenavarro.catlovers.data.network.Result
import com.alexandrenavarro.catlovers.data.network.model.toExternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class DefaultBreedRepository constructor(
    private val breedRemoteDataSource: BreedRemoteDataSource
): BreedRepository {

    private val breedsFlow = MutableStateFlow<List<BreedPreview>>(emptyList())

    override suspend fun refreshBreeds(): Result<Unit> {
       val result = breedRemoteDataSource.fetchBreeds()

        if (result is Result.Success) {
            breedsFlow.value = result.data.map { it.toExternalModel() }
        }

        return Result.Success(Unit)
    }

    override fun getBreeds(): Flow<List<BreedPreview>> = breedsFlow
}