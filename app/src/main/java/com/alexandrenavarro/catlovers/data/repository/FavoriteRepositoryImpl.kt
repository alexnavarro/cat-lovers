package com.alexandrenavarro.catlovers.data.repository

import com.alexandrenavarro.catlovers.data.database.FavoriteDao
import com.alexandrenavarro.catlovers.data.database.model.FavoriteEntity
import com.alexandrenavarro.catlovers.data.network.FavoriteRemoteDataSource
import com.alexandrenavarro.catlovers.data.network.Result
import com.alexandrenavarro.catlovers.domain.model.FavoriteBreed
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteRemoteDataSource: FavoriteRemoteDataSource,
    private val favoriteDao: FavoriteDao
) : FavoriteRepository {

    override suspend fun addFavorite(imageId: String): Result<Unit> {
        return when (val result = favoriteRemoteDataSource.favorite(imageId)) {
            is Result.Success -> {
                executeLocalOperation {
                    favoriteDao.insertFavorite(
                        FavoriteEntity(id = result.data, imageId = imageId)
                    )
                }
            }

            is Result.Error -> result
            is Result.NetworkError -> result
        }
    }

    override suspend fun deleteFavorite(imageId: String): Result<Unit> {
        val favorite = favoriteDao.findFavoriteByImageId(imageId)
            ?: return Result.Error(Exception("Favorite not found"))

        return when (val result = favoriteRemoteDataSource.deleteFavorite(favorite.id)) {
            is Result.Success -> {
                executeLocalOperation {
                    favoriteDao.deleteById(favorite.id)
                }
            }

            is Result.Error -> result
            is Result.NetworkError -> result
        }
    }

    override fun getFavoriteBreeds(): Flow<List<FavoriteBreed>> =
        favoriteDao.getFavoriteBreeds()

    override fun isFavorite(imageId: String): Flow<Boolean> =
        favoriteDao.isFavorite(imageId)

    private suspend inline fun executeLocalOperation(
        block: suspend () -> Unit
    ): Result<Unit> {
        return try {
            block()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}