package com.alexandrenavarro.catlovers.data.network

import com.alexandrenavarro.catlovers.data.network.model.NetworkBreedPreview
import com.alexandrenavarro.catlovers.data.network.model.NetworkFavorite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface BreedRemoteDataSource {

    suspend fun fetchBreeds(
        page: Int = 0,
        pageSize: Int = 10
    ): Result<List<NetworkBreedPreview>>

    suspend fun favorite(imageId: String): Result<Long>
}

internal class DefaultBreedRemoteDataSource constructor(
    private val breedApi: BreedApi,
) : BreedRemoteDataSource {

    override suspend fun fetchBreeds(
        page: Int,
        pageSize: Int
    ): Result<List<NetworkBreedPreview>> =
        withContext(Dispatchers.IO) {
            try {
                val response = breedApi.fetchBreeds(limit = pageSize, page = page)

                if (!response.isSuccessful) {
                    return@withContext Result.Error(Exception(response.message()))
                }

                Result.Success(response.body() ?: emptyList())

            } catch (e: Exception) {
                Result.NetworkError(e)
            }
        }

    override suspend fun favorite(imageId: String): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val response = breedApi.favorite(NetworkFavorite(imageId))

            if (!response.isSuccessful) {
                return@withContext Result.Error(Exception(response.message()))
            }

            Result.Success(response.body()?.id ?: -1)

        } catch (e: Exception) {
            Result.NetworkError(e)
        }
    }
}