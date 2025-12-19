package com.alexandrenavarro.catlovers.data.network.model

import com.alexandrenavarro.catlovers.domain.model.BreedDetail
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

fun NetworkBreedDetail.toExternalModel() = BreedDetail(
    id = id,
    name = name,
    imageUrl = image?.imageUrl,
    imageId = image?.id,
    origin = origin,
    description = description,
    temperament = temperament
)
