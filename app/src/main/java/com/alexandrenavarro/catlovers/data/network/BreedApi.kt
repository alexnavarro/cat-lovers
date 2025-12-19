package com.alexandrenavarro.catlovers.data.network

import com.alexandrenavarro.catlovers.data.network.model.NetworkBreedDetail
import com.alexandrenavarro.catlovers.data.network.model.NetworkBreedPreview
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BreedApi {

    @GET("/v1/breeds")
    suspend fun fetchBreeds(
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): Response<List<NetworkBreedPreview>>

    @GET("/v1/breeds/{id}")
    suspend fun fetchBreed(
        @Path("id") id: String
    ): Response<List<NetworkBreedDetail>>
}