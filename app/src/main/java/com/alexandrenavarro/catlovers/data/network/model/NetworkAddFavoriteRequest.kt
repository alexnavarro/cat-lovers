package com.alexandrenavarro.catlovers.data.network.model

import com.google.gson.annotations.SerializedName

data class NetworkAddFavoriteRequest(
    @SerializedName("image_id")
    val imageId: String,
)

data class NetworkAddFavoriteResponse(
    @SerializedName("id")
    val id: Long,
)

