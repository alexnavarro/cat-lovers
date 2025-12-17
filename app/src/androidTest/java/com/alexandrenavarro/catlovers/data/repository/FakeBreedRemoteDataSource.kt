package com.alexandrenavarro.catlovers.data.repository

import com.alexandrenavarro.catlovers.data.network.BreedRemoteDataSource
import com.alexandrenavarro.catlovers.data.network.Result
import com.alexandrenavarro.catlovers.data.network.model.NetworkBreedPreview

class FakeBreedRemoteDataSource(private val result: Result<List<NetworkBreedPreview>>) :
    BreedRemoteDataSource {

    override suspend fun fetchBreeds(
        page: Int,
        pageSize: Int
    ): Result<List<NetworkBreedPreview>> = result
}