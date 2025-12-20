package com.alexandrenavarro.catlovers.data.network

import android.util.Log
import com.alexandrenavarro.catlovers.data.network.model.NetworkCatBreedDetail
import com.alexandrenavarro.catlovers.data.network.model.NetworkCatBreedPreview

interface CatBreedRemoteDataSource {

    suspend fun fetchCatBreeds(
        page: Int = 0,
        pageSize: Int = 10
    ): Result<List<NetworkCatBreedPreview>>

    suspend fun fetchCatBreed(id: String): Result<NetworkCatBreedDetail>
}

internal class CatBreedRemoteDataSourceImpl(
    private val catBreedApi: CatBreedApi,
) : CatBreedRemoteDataSource {

    override suspend fun fetchCatBreeds(
        page: Int,
        pageSize: Int
    ): Result<List<NetworkCatBreedPreview>> =
        safeApiCall {
            Log.d("CatBreedRemoteDataSource", "Fetching cat breeds page $page")
            catBreedApi.fetchCatBreeds(limit = pageSize, page = page) }

    override suspend fun fetchCatBreed(id: String): Result<NetworkCatBreedDetail> =
        safeApiCall { catBreedApi.fetchCatBreed(id) }
}