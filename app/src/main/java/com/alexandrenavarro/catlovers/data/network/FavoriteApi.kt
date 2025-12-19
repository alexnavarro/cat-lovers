package com.alexandrenavarro.catlovers.data.network

import com.alexandrenavarro.catlovers.data.network.model.NetworkAddFavoriteRequest
import com.alexandrenavarro.catlovers.data.network.model.NetworkAddFavoriteResponse
import com.alexandrenavarro.catlovers.data.network.model.NetworkFavoritesResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FavoriteApi {

    @POST("/v1/favourites")
    suspend fun addFavorite(@Body networkAddFavoriteRequest: NetworkAddFavoriteRequest): Response<NetworkAddFavoriteResponse>

    @DELETE("/v1/favourites/{id}")
    suspend fun deleteFavorite(@Path("id") id: Long): Response<Unit>

    @GET("/v1/favourites")
    suspend fun fetchFavorites(): Response<List<NetworkFavoritesResponse>>
}