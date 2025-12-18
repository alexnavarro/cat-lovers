package com.alexandrenavarro.catlovers.data.network

import com.alexandrenavarro.catlovers.data.network.model.NetworkFavorite
import com.alexandrenavarro.catlovers.data.network.model.NetworkFavoriteResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface FavoriteApi {

    @POST("/v1/favourites")
    suspend fun favorite(@Body networkFavorite: NetworkFavorite): Response<NetworkFavoriteResponse>

    @DELETE("/v1/favourites/{id}")
    suspend fun deleteFavorite(@Path("id") id: Long): Response<Unit>
}