package com.alexandrenavarro.catlovers.data.network

import com.alexandrenavarro.catlovers.data.network.model.NetworkBreedDetail
import com.alexandrenavarro.catlovers.data.network.model.NetworkBreedPreview

interface BreedRemoteDataSource {

    suspend fun fetchBreeds(
        page: Int = 0,
        pageSize: Int = 10
    ): Result<List<NetworkBreedPreview>>

    suspend fun fetchBreed(id: String): Result<NetworkBreedDetail>
}

internal class BreedRemoteDataSourceImpl(
    private val breedApi: BreedApi,
) : BreedRemoteDataSource {

    override suspend fun fetchBreeds(
        page: Int,
        pageSize: Int
    ): Result<List<NetworkBreedPreview>> =
        safeApiCall { breedApi.fetchBreeds(limit = pageSize, page = page) }

    override suspend fun fetchBreed(id: String): Result<NetworkBreedDetail> =
        safeApiCall { breedApi.fetchBreed(id) }
}