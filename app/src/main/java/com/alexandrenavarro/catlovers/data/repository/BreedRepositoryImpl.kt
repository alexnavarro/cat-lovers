package com.alexandrenavarro.catlovers.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.alexandrenavarro.catlovers.data.database.CatBreedsDatabase
import com.alexandrenavarro.catlovers.data.network.BreedRemoteDataSource
import com.alexandrenavarro.catlovers.data.network.Result
import com.alexandrenavarro.catlovers.data.network.model.toExternalModel
import com.alexandrenavarro.catlovers.domain.model.BreedDetail
import com.alexandrenavarro.catlovers.domain.model.BreedPreview
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
@OptIn(ExperimentalPagingApi::class)
internal class BreedRepositoryImpl @Inject constructor(
    private val breedRemoteDataSource: BreedRemoteDataSource,
    private val breedDataBase: CatBreedsDatabase,
) : BreedRepository {

    companion object {
        private const val PAGE_SIZE = 10
        private val PAGING_CONFIG = PagingConfig(
            pageSize = PAGE_SIZE,
            enablePlaceholders = false
        )
    }

    private val breedRemoteMediator: BreedRemoteMediator by lazy {
        BreedRemoteMediator(breedRemoteDataSource, breedDataBase)
    }


    override fun getBreeds(query: String?): Flow<PagingData<BreedPreview>> {
        return if (query.isNullOrBlank()) {
            createPagerWithRemoteMediator()
        } else {
            createPagerWithLocalSearch(query)
        }.flow
    }

    private fun createPagerWithRemoteMediator() = Pager(
        config = PAGING_CONFIG,
        remoteMediator = breedRemoteMediator,
        pagingSourceFactory = { breedDataBase.catBreedsDao().pagingSource(null) }
    )

    private fun createPagerWithLocalSearch(query: String) = Pager(
        config = PAGING_CONFIG,
        pagingSourceFactory = { breedDataBase.catBreedsDao().pagingSource(query) }
    )

    override suspend fun getBreedDetail(breedId: String): Result<BreedDetail> {
        return breedRemoteDataSource.fetchBreed(breedId)
            .map { it.toExternalModel() }
    }
}