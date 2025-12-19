package com.alexandrenavarro.catlovers.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.alexandrenavarro.catlovers.data.database.CatBreedsDatabase
import com.alexandrenavarro.catlovers.data.network.CatBreedRemoteDataSource
import com.alexandrenavarro.catlovers.data.network.Result
import com.alexandrenavarro.catlovers.data.network.model.toExternalModel
import com.alexandrenavarro.catlovers.domain.model.CatBreedDetail
import com.alexandrenavarro.catlovers.domain.model.CatBreedPreview
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
@OptIn(ExperimentalPagingApi::class)
internal class CatBreedRepositoryImpl @Inject constructor(
    private val catBreedRemoteDataSource: CatBreedRemoteDataSource,
    private val breedDataBase: CatBreedsDatabase,
) : CatBreedRepository {

    companion object {
        private const val PAGE_SIZE = 10
        private val PAGING_CONFIG = PagingConfig(
            pageSize = PAGE_SIZE,
            enablePlaceholders = false
        )
    }

    private val catBreedRemoteMediator: CatBreedRemoteMediator by lazy {
        CatBreedRemoteMediator(catBreedRemoteDataSource, breedDataBase)
    }


    override fun getCatBreeds(query: String?): Flow<PagingData<CatBreedPreview>> {
        return if (query.isNullOrBlank()) {
            createPagerWithRemoteMediator()
        } else {
            createPagerWithLocalSearch(query)
        }.flow
    }

    private fun createPagerWithRemoteMediator() = Pager(
        config = PAGING_CONFIG,
        remoteMediator = catBreedRemoteMediator,
        pagingSourceFactory = { breedDataBase.catBreedsDao().pagingSource(null) }
    )

    private fun createPagerWithLocalSearch(query: String) = Pager(
        config = PAGING_CONFIG,
        pagingSourceFactory = { breedDataBase.catBreedsDao().pagingSource(query) }
    )

    override suspend fun getCatBreedDetail(breedId: String): Result<CatBreedDetail> {
        return catBreedRemoteDataSource.fetchCatBreed(breedId)
            .map { it.toExternalModel() }
    }
}