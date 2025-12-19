package com.alexandrenavarro.catlovers.data.network

import com.alexandrenavarro.catlovers.data.network.model.NetworkBreedDetail
import com.alexandrenavarro.catlovers.data.network.model.NetworkBreedPreview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface BreedRemoteDataSource {

    suspend fun fetchBreeds(
        page: Int = 0,
        pageSize: Int = 10
    ): Result<List<NetworkBreedPreview>>

    suspend fun fetchBreed(id: String): Result<NetworkBreedDetail>
}

internal class BreedRemoteDataSourceImpl(
    private val breedApi: BreedApi,
) : BreedRemoteDataSource {

    override suspend fun fetchBreeds(
        page: Int,
        pageSize: Int
    ): Result<List<NetworkBreedPreview>> =
        safeApiCall { breedApi.fetchBreeds(limit = pageSize, page = page) }

    override suspend fun fetchBreed(id: String): Result<NetworkBreedDetail> =
        safeApiCall { breedApi.fetchBreed(id) }
}

internal suspend fun <T> safeApiCall(apiCall: suspend () -> retrofit2.Response<T>): Result<T> =
    withContext(Dispatchers.IO) {
        try {
            val response = apiCall()

            if (!response.isSuccessful) {
                return@withContext Result.Error(Exception(response.message()))
            }

            Result.Success(response.body() ?: error("Empty response body"))

        } catch (e: Exception) {
            Result.NetworkError(e)
        }
    }