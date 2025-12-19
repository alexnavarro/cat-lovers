package com.alexandrenavarro.catlovers.data.repository

import com.alexandrenavarro.catlovers.data.network.Result
import com.alexandrenavarro.catlovers.domain.model.FavoriteBreed
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {

    suspend fun addFavorite(imageId: String): Result<Unit>

    suspend fun deleteFavorite(imageId: String): Result<Unit>

    fun getFavoriteBreeds(): Flow<List<FavoriteBreed>>
}