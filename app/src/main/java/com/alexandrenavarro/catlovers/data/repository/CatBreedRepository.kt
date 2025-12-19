package com.alexandrenavarro.catlovers.data.repository

import androidx.paging.PagingData
import com.alexandrenavarro.catlovers.data.network.Result
import com.alexandrenavarro.catlovers.domain.model.CatBreedDetail
import com.alexandrenavarro.catlovers.domain.model.CatBreedPreview
import kotlinx.coroutines.flow.Flow

/***
I create create interfaces for repositories to be able to mock them
on unit tests. It was easy to create fake classes when it return streams
***/
interface CatBreedRepository {

    fun getCatBreeds(query: String?): Flow<PagingData<CatBreedPreview>>

    suspend fun getCatBreedDetail(breedId: String): Result<CatBreedDetail>
}