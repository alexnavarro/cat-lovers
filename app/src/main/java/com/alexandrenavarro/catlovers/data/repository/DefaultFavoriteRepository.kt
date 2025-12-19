package com.alexandrenavarro.catlovers.data.repository

import com.alexandrenavarro.catlovers.data.database.FavoriteDao
import com.alexandrenavarro.catlovers.data.database.model.FavoriteEntity
import com.alexandrenavarro.catlovers.data.network.FavoriteRemoteDataSource
import com.alexandrenavarro.catlovers.data.network.Result
import com.alexandrenavarro.catlovers.domain.model.FavoriteBreed
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultFavoriteRepository @Inject constructor(
    private val favoriteRemoteDataSource: FavoriteRemoteDataSource,
    private val favoriteDao: FavoriteDao
): FavoriteRepository {

    override suspend fun addFavorite(imageId: String): Result<Unit> {
        return try {
            when (val result = favoriteRemoteDataSource.favorite(imageId)) {
                is Result.Success -> {
                    favoriteDao.insertFavorite(
                        FavoriteEntity(
                            id = result.data,
                            imageId = imageId,
                        )
                    )

                    Result.Success(Unit)
                }

                is Result.Error -> {
                    Result.Error(result.exception)
                }

                is Result.NetworkError -> {
                    Result.NetworkError(result.exception)
                }

            }

        }catch (e: Exception) {
            return Result.Error(e)
        }
    }

    override suspend fun deleteFavorite(imageId: String): Result<Unit> {
        return try {
            val favorite =
                favoriteDao.findFavoriteByImageId(imageId) ?: return Result.Error(
                    Exception("Favorite not found")
                )

            when (val result = favoriteRemoteDataSource.deleteFavorite(favorite.id)) {
                is Result.Success -> {
                    favoriteDao.deleteById(favorite.id)
                    Result.Success(Unit)
                }

                is Result.Error -> {
                    Result.Error(result.exception)
                }

                is Result.NetworkError -> {
                    Result.NetworkError(result.exception)
                }

            }

        }catch (e: Exception) {
            return Result.Error(e)
        }
    }

    override fun getFavoriteBreeds(): Flow<List<FavoriteBreed>> = favoriteDao.getFavoriteBreeds()
}