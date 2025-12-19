package com.alexandrenavarro.catlovers.data.network

import com.alexandrenavarro.catlovers.data.network.model.NetworkFavorite
import com.alexandrenavarro.catlovers.data.repository.map

interface FavoriteRemoteDataSource {

    suspend fun favorite(imageId: String): Result<Long>

    suspend fun deleteFavorite(favoriteId: Long): Result<Unit>

}

internal class FavoriteRemoteDataSourceImpl(
    private val favoriteApi: FavoriteApi,
) : FavoriteRemoteDataSource {

    override suspend fun favorite(imageId: String): Result<Long> =
        safeApiCall { favoriteApi.favorite(NetworkFavorite(imageId)) }
            .map { it.id }

    override suspend fun deleteFavorite(favoriteId: Long): Result<Unit> =
        safeApiCall { favoriteApi.deleteFavorite(favoriteId) }
}