package com.alexandrenavarro.catlovers.data.repository

import com.alexandrenavarro.catlovers.data.network.Result

interface FavoriteRepository {

    suspend fun addFavorite(imageId: String): Result<Unit>

    suspend fun deleteFavorite(imageId: String): Result<Unit>
}