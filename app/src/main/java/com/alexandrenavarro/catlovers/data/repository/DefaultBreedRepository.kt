package com.alexandrenavarro.catlovers.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.alexandrenavarro.catlovers.data.database.BreedsDatabase
import com.alexandrenavarro.catlovers.data.database.model.FavoriteEntity
import com.alexandrenavarro.catlovers.domain.model.BreedPreview
import com.alexandrenavarro.catlovers.data.network.BreedRemoteDataSource
import com.alexandrenavarro.catlovers.data.network.FavoriteRemoteDataSource
import com.alexandrenavarro.catlovers.data.network.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class DefaultBreedRepository @Inject constructor(
    private val breedRemoteDataSource: BreedRemoteDataSource,
    private val favoriteRemoteDataSource: FavoriteRemoteDataSource,
    private val breedDataBase: BreedsDatabase,
): BreedRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getBreeds(): Flow<PagingData<BreedPreview>> =
        Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            remoteMediator = BreedRemoteMediator(breedRemoteDataSource, breedDataBase),
            pagingSourceFactory = { breedDataBase.breedsDao().pagingSource() }
        ).flow

    override suspend fun addFavorite(imageId: String): Result<Unit> {
        return try {
            when (val result = favoriteRemoteDataSource.favorite(imageId)) {
                is Result.Success -> {
                    breedDataBase.favoriteDao().insertFavorite(
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
                breedDataBase.favoriteDao().findFavoriteByImageId(imageId) ?: return Result.Error(
                    Exception("Favorite not found")
                )

            when (val result = favoriteRemoteDataSource.deleteFavorite(favorite.id)) {
                is Result.Success -> {
                    breedDataBase.favoriteDao().deleteById(favorite.id)
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
}