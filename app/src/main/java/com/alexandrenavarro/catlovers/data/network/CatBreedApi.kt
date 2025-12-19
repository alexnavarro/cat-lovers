package com.alexandrenavarro.catlovers.data.network

import com.alexandrenavarro.catlovers.data.network.model.NetworkCatBreedDetail
import com.alexandrenavarro.catlovers.data.network.model.NetworkCatBreedPreview
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CatBreedApi {

    @GET("/v1/breeds")
    suspend fun fetchCatBreeds(
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): Response<List<NetworkCatBreedPreview>>

    @GET("/v1/breeds/{id}")
    suspend fun fetchCatBreed(
        @Path("id") id: String
    ): Response<NetworkCatBreedDetail>
}