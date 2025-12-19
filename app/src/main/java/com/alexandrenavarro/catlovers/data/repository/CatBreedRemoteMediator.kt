package com.alexandrenavarro.catlovers.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.alexandrenavarro.catlovers.data.database.CatBreedsDatabase
import com.alexandrenavarro.catlovers.data.database.model.CatBreedRemoteKey
import com.alexandrenavarro.catlovers.data.network.CatBreedRemoteDataSource
import com.alexandrenavarro.catlovers.data.network.Result
import com.alexandrenavarro.catlovers.data.network.model.toCatBreedPreviewEntity
import com.alexandrenavarro.catlovers.domain.model.CatBreedPreview

@OptIn(ExperimentalPagingApi::class)
internal class CatBreedRemoteMediator(
    private val catBreedRemoteDataSource: CatBreedRemoteDataSource,
    private val breedDataBase: CatBreedsDatabase,
) : RemoteMediator<Int, CatBreedPreview>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CatBreedPreview>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> 1

            LoadType.PREPEND -> {
                val firstItem = state.firstItemOrNull()
                    ?: return MediatorResult.Success(endOfPaginationReached = true)
                getRemoteKeyForFirstItem(firstItem.id)?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = true)
            }

            LoadType.APPEND -> {
                val lastItem = state.lastItemOrNull()
                    ?: return MediatorResult.Success(endOfPaginationReached = true)
                getRemoteKeyForLastItem(lastItem.id)?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = true)
            }
        }

        return try {
            val adjustedPage = page - 1
            val response = catBreedRemoteDataSource.fetchCatBreeds(
                page = adjustedPage,
                pageSize = state.config.pageSize
            )

            val breeds = when (response) {
                is Result.Success -> response.data
                is Result.Error -> return MediatorResult.Error(response.exception)
                is Result.NetworkError -> return MediatorResult.Error(response.exception)
            }

            val endReached = breeds.isEmpty()

            breedDataBase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    breedDataBase.catBreedsDao().clearAll()
                    breedDataBase.catBreedsRemoteKeyDao().clearRemoteKeys()
                }

                val keys = breeds.map {
                    CatBreedRemoteKey(
                        breedId = it.id,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (endReached) null else page + 1
                    )
                }

                breedDataBase.catBreedsRemoteKeyDao().insertAll(keys)
                breedDataBase.catBreedsDao().insertAll(breeds.map { it.toCatBreedPreviewEntity() })
            }

            MediatorResult.Success(endOfPaginationReached = endReached)

        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(breedId: String): CatBreedRemoteKey? =
        breedDataBase.catBreedsRemoteKeyDao().remoteKeyById(breedId)

    private suspend fun getRemoteKeyForLastItem(breedId: String): CatBreedRemoteKey? =
        breedDataBase.catBreedsRemoteKeyDao().remoteKeyById(breedId)
}