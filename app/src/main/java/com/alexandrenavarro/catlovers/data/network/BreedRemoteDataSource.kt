package com.alexandrenavarro.catlovers.data.network

import com.alexandrenavarro.catlovers.data.network.model.NetworkBreedPreview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
interface BreedRemoteDataSource {

    suspend fun fetchBreeds(): Result<List<NetworkBreedPreview>>
}

internal class DefaultBreedRemoteDataSource constructor(
    private val breedApi: BreedApi,
) : BreedRemoteDataSource {

    override suspend fun fetchBreeds(): Result<List<NetworkBreedPreview>> =
        withContext(Dispatchers.IO) {
            try {
                val response = breedApi.fetchBreeds(limit = 10, page = 0)

                if (!response.isSuccessful) {
                    return@withContext Result.Error(Exception(response.message()))
                }

                Result.Success(response.body() ?: emptyList())

            } catch (e: Exception) {
                Result.NetworkError(e)
            }
        }
}