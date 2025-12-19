package com.alexandrenavarro.catlovers.data.repository

import com.alexandrenavarro.catlovers.data.network.FavoriteRemoteDataSource
import com.alexandrenavarro.catlovers.data.network.Result
import com.alexandrenavarro.catlovers.data.network.model.NetworkFavoritesResponse

class FakeFavoriteRemoteDataSource(
    private val favoriteResult: Result<Long>,
    private val deleteFavoriteResult: Result<Unit>,
): FavoriteRemoteDataSource {
    override suspend fun favorite(imageId: String): Result<Long>  = favoriteResult

    override suspend fun deleteFavorite(favoriteId: Long): Result<Unit> = deleteFavoriteResult

    override suspend fun fetchFavorites(): Result<List<NetworkFavoritesResponse>> {
        TODO("Not yet implemented")
    }
}