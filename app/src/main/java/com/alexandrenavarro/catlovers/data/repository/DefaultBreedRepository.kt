package com.alexandrenavarro.catlovers.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.alexandrenavarro.catlovers.data.database.BreedsDatabase
import com.alexandrenavarro.catlovers.data.network.BreedRemoteDataSource
import com.alexandrenavarro.catlovers.domain.model.BreedPreview
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class DefaultBreedRepository @Inject constructor(
    private val breedRemoteDataSource: BreedRemoteDataSource,
    private val breedDataBase: BreedsDatabase,
): BreedRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getBreeds(query: String?): Flow<PagingData<BreedPreview>> {
        return if (query.isNullOrBlank()) {
            Pager(
                config = PagingConfig(pageSize = 10, enablePlaceholders = false),
                remoteMediator = BreedRemoteMediator(
                    breedRemoteDataSource,
                    breedDataBase
                ),
                pagingSourceFactory = {
                    breedDataBase.breedsDao().pagingSource(null)
                }
            ).flow
        } else {
            Pager(
                config = PagingConfig(pageSize = 10, enablePlaceholders = false),
                pagingSourceFactory = {
                    breedDataBase.breedsDao().pagingSource(query)
                }
            ).flow
        }
    }
}