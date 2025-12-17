package com.alexandrenavarro.catlovers.data.repository

import com.alexandrenavarro.catlovers.domain.model.BreedPreview
import com.alexandrenavarro.catlovers.data.network.BreedRemoteDataSource
import com.alexandrenavarro.catlovers.data.network.Result
import com.alexandrenavarro.catlovers.data.network.model.toExternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

internal class DefaultBreedRepository @Inject constructor(
    private val breedRemoteDataSource: BreedRemoteDataSource
): BreedRepository {

    private val breedsFlow = MutableStateFlow<List<BreedPreview>>(emptyList())

    override suspend fun refreshBreeds(): Result<Unit> {
        when (val result = breedRemoteDataSource.fetchBreeds()) {
            is Result.Success -> {
                breedsFlow.value = result.data.map { it.toExternalModel() }
                return Result.Success(Unit)
            }
            is Result.Error -> {
                return Result.Error(result.exception)
            }
            is Result.NetworkError -> {
                return Result.NetworkError(result.exception)
            }
        }
    }

    override fun getBreeds(): Flow<List<BreedPreview>> = breedsFlow
}