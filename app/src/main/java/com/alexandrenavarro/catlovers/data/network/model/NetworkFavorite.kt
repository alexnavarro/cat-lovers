package com.alexandrenavarro.catlovers.data.network.model

import com.google.gson.annotations.SerializedName

data class NetworkFavorite(
    @SerializedName("image_id")
    val imageId: String,
)

data class NetworkFavoriteResponse(
    @SerializedName("id")
    val id: Long,
)

