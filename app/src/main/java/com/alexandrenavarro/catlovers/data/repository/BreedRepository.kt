package com.alexandrenavarro.catlovers.data.repository

import androidx.paging.PagingData
import com.alexandrenavarro.catlovers.domain.model.BreedPreview
import kotlinx.coroutines.flow.Flow

/***
I create create interfaces for repositories to be able to mock them
on unit tests. It was easy to create fake classes when it return streams
***/
interface BreedRepository {

    fun getBreeds(query: String?): Flow<PagingData<BreedPreview>>
}