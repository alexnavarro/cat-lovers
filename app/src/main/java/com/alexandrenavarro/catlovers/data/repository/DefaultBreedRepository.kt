package com.alexandrenavarro.catlovers.data.repository

import com.alexandrenavarro.catlovers.data.model.BreedPreview
import com.alexandrenavarro.catlovers.data.network.BreedRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class DefaultBreedRepository constructor(
    private val breedRemoteDataSource: BreedRemoteDataSource
): BreedRepository {

    private val breedsFlow = MutableStateFlow<List<BreedPreview>>(emptyList())

    override suspend fun refreshBreeds(): Result<Unit> {
        TODO("Not yet implemented")
    }

    override fun getBreeds(): Flow<List<BreedPreview>> = breedsFlow
}