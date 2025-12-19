package com.alexandrenavarro.catlovers.data.repository

import com.alexandrenavarro.catlovers.data.network.BreedRemoteDataSource
import com.alexandrenavarro.catlovers.data.network.Result
import com.alexandrenavarro.catlovers.data.network.model.NetworkBreedDetail
import com.alexandrenavarro.catlovers.data.network.model.NetworkBreedPreview

class FakeBreedRemoteDataSource(
    private val result: Result<List<NetworkBreedPreview>> = Result.Error(Exception("Not implemented")),
    private val breedDetail: Result<NetworkBreedDetail> = Result.Error(Exception("Not implemented"))
) :
    BreedRemoteDataSource {

    override suspend fun fetchBreeds(
        page: Int,
        pageSize: Int
    ): Result<List<NetworkBreedPreview>> = result

    override suspend fun fetchBreed(id: String): Result<NetworkBreedDetail> = breedDetail
}