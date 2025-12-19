package com.alexandrenavarro.catlovers.data.network

import com.alexandrenavarro.catlovers.data.network.model.NetworkAddFavoriteRequest
import com.alexandrenavarro.catlovers.data.network.model.NetworkFavoritesResponse
import com.alexandrenavarro.catlovers.data.repository.map

interface FavoriteRemoteDataSource {

    suspend fun favorite(imageId: String): Result<Long>

    suspend fun deleteFavorite(favoriteId: Long): Result<Unit>

    suspend fun fetchFavorites(): Result<List<NetworkFavoritesResponse>>

}

internal class FavoriteRemoteDataSourceImpl(
    private val favoriteApi: FavoriteApi,
) : FavoriteRemoteDataSource {

    override suspend fun favorite(imageId: String): Result<Long> =
        safeApiCall { favoriteApi.addFavorite(NetworkAddFavoriteRequest(imageId)) }
            .map { it.id }

    override suspend fun deleteFavorite(favoriteId: Long): Result<Unit> =
        safeApiCall { favoriteApi.deleteFavorite(favoriteId) }.recoverIfNotFound()

    override suspend fun fetchFavorites(): Result<List<NetworkFavoritesResponse>> =
        safeApiCall {
            favoriteApi.fetchFavorites()
        }


    private fun Result<Unit>.recoverIfNotFound(): Result<Unit> =
        when (this) {
            is Result.Error -> {
                if (this.exception.message?.contains(
                        "INVALID_ACCOUNT",
                        ignoreCase = true
                    ) == true
                ) {
                    Result.Success(Unit)
                } else {
                    this
                }
            }

            else -> this
        }
}