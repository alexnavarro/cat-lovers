package com.alexandrenavarro.catlovers.data.network

import com.alexandrenavarro.catlovers.data.network.model.NetworkBreedPreview
import com.alexandrenavarro.catlovers.data.network.model.NetworkFavorite
import com.alexandrenavarro.catlovers.data.network.model.NetworkFavoriteResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface BreedApi {

    @GET("/v1/breeds")
    suspend fun fetchBreeds(
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): Response<List<NetworkBreedPreview>>

    @POST("/v1/favourites")
    suspend fun favorite(@Body networkFavorite: NetworkFavorite): Response<NetworkFavoriteResponse>

}