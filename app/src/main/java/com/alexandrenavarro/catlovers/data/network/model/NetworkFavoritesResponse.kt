package com.alexandrenavarro.catlovers.data.network.model

import com.alexandrenavarro.catlovers.data.database.model.FavoriteEntity
import com.google.gson.annotations.SerializedName

data class NetworkFavoritesResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("image_id")
    val imageId: String,
)

fun NetworkFavoritesResponse.toEntity() = FavoriteEntity(
    id = id,
    imageId = imageId
)