package com.alexandrenavarro.catlovers.data.network.model

import com.google.gson.annotations.SerializedName

data class NetworkBreedPreview(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("image")
    val image: NetworkBreedImage,
)

data class NetworkBreedImage(
    @SerializedName("id")
    val id: String,
    @SerializedName("url")
    val imageUrl: String,
)
