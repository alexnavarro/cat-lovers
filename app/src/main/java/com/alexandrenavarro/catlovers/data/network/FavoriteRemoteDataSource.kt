package com.alexandrenavarro.catlovers.data.network

import com.alexandrenavarro.catlovers.data.network.model.NetworkFavorite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface FavoriteRemoteDataSource {

    suspend fun favorite(imageId: String): Result<Long>

    suspend fun deleteFavorite(favoriteId: Long): Result<Unit>

}

internal class DefaultFavoriteRemoteDataSource(
    private val favoriteApi: FavoriteApi,
) : FavoriteRemoteDataSource {

    override suspend fun favorite(imageId: String): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val response = favoriteApi.favorite(NetworkFavorite(imageId))

            if (!response.isSuccessful) {
                return@withContext Result.Error(Exception(response.message()))
            }

            Result.Success(response.body()?.id ?: -1)

        } catch (e: Exception) {
            Result.NetworkError(e)
        }
    }

    override suspend fun deleteFavorite(favoriteId: Long): Result<Unit> {
        TODO("Not yet implemented")
    }

}