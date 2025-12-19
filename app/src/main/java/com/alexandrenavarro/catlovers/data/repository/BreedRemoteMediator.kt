package com.alexandrenavarro.catlovers.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.alexandrenavarro.catlovers.data.database.BreedsDatabase
import com.alexandrenavarro.catlovers.data.database.model.BreedRemoteKey
import com.alexandrenavarro.catlovers.data.network.BreedRemoteDataSource
import com.alexandrenavarro.catlovers.data.network.Result
import com.alexandrenavarro.catlovers.data.network.model.toBreedPreviewEntity
import com.alexandrenavarro.catlovers.domain.model.BreedPreview

@OptIn(ExperimentalPagingApi::class)
internal class BreedRemoteMediator(
    private val breedRemoteDataSource: BreedRemoteDataSource,
    private val breedDataBase: BreedsDatabase,
) : RemoteMediator<Int, BreedPreview>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, BreedPreview>
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
            val response = breedRemoteDataSource.fetchBreeds(
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
                    breedDataBase.breedsDao().clearAll()
                    breedDataBase.breedRemoteKeyDao().clearRemoteKeys()
                }

                val keys = breeds.map {
                    BreedRemoteKey(
                        breedId = it.id,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (endReached) null else page + 1
                    )
                }

                breedDataBase.breedRemoteKeyDao().insertAll(keys)
                breedDataBase.breedsDao().insertAll(breeds.map { it.toBreedPreviewEntity() })
            }

            MediatorResult.Success(endOfPaginationReached = endReached)

        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(breedId: String): BreedRemoteKey? =
        breedDataBase.breedRemoteKeyDao().remoteKeyById(breedId)

    private suspend fun getRemoteKeyForLastItem(breedId: String): BreedRemoteKey? =
        breedDataBase.breedRemoteKeyDao().remoteKeyById(breedId)
}