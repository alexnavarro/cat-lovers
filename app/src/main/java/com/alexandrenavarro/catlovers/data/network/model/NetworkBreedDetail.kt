package com.alexandrenavarro.catlovers.data.network.model

import com.google.gson.annotations.SerializedName

data class NetworkBreedDetail(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("image")
    val image: NetworkBreedImage?,
    @SerializedName("origin")
    val origin: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("temperament")
    val temperament: String,
)
