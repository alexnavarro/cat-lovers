package com.alexandrenavarro.catlovers.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.alexandrenavarro.catlovers.data.database.BreedsDatabase
import com.alexandrenavarro.catlovers.data.database.model.BreedPreviewEntity
import com.alexandrenavarro.catlovers.data.database.model.BreedRemoteKey
import com.alexandrenavarro.catlovers.data.network.BreedRemoteDataSource
import com.alexandrenavarro.catlovers.data.network.Result
import com.alexandrenavarro.catlovers.data.network.model.toBreedPreviewEntity

@OptIn(ExperimentalPagingApi::class)
internal class BreedRemoteMediator (
    private val breedRemoteDataSource: BreedRemoteDataSource,
    private val breedDataBase: BreedsDatabase,
    ): RemoteMediator<Int, BreedPreviewEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, BreedPreviewEntity>
    ): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> 1

            LoadType.PREPEND -> {
                val firstItem = state.firstItemOrNull()
                    ?: return MediatorResult.Success(true)

                breedDataBase.breedRemoteKeyDao()
                    .remoteKeyById(firstItem.id)
                    ?.prevKey
                    ?: return MediatorResult.Success(true)
            }

            LoadType.APPEND -> {
                val lastItem = state.lastItemOrNull()
                    ?: return MediatorResult.Success(true)

                breedDataBase.breedRemoteKeyDao()
                    .remoteKeyById(lastItem.id)
                    ?.nextKey
                    ?: return MediatorResult.Success(true)
            }
        }

       return try {

            val response = breedRemoteDataSource.fetchBreeds(page = page, pageSize = state.config.pageSize)

            if(response is Result.Error) {
                MediatorResult.Error(response.exception)
            }

            val breeds = (response as Result.Success).data
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

            MediatorResult.Success(endReached)

        }catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}