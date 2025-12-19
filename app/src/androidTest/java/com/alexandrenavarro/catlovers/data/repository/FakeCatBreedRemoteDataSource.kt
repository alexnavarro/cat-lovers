package com.alexandrenavarro.catlovers.data.repository

import com.alexandrenavarro.catlovers.data.network.CatBreedRemoteDataSource
import com.alexandrenavarro.catlovers.data.network.Result
import com.alexandrenavarro.catlovers.data.network.model.NetworkCatBreedDetail
import com.alexandrenavarro.catlovers.data.network.model.NetworkCatBreedPreview

class FakeCatBreedRemoteDataSource(
    private val result: Result<List<NetworkCatBreedPreview>> = Result.Error(Exception("Not implemented")),
    private val breedDetail: Result<NetworkCatBreedDetail> = Result.Error(Exception("Not implemented"))
) :
    CatBreedRemoteDataSource {

    override suspend fun fetchCatBreeds(
        page: Int,
        pageSize: Int
    ): Result<List<NetworkCatBreedPreview>> = result

    override suspend fun fetchCatBreed(id: String): Result<NetworkCatBreedDetail> = breedDetail
}