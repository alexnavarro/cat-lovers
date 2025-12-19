package com.alexandrenavarro.catlovers.data.network.model

import com.alexandrenavarro.catlovers.domain.model.CatBreedDetail
import com.google.gson.annotations.SerializedName

data class NetworkCatBreedDetail(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("reference_image_id")
    val imageId: String?,
    @SerializedName("origin")
    val origin: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("temperament")
    val temperament: String,
)

fun NetworkCatBreedDetail.toExternalModel() = CatBreedDetail(
    id = id,
    name = name,
    imageUrl = imageId?.let { "https://cdn2.thecatapi.com/images/$it.jpg" },
    imageId = imageId,
    origin = origin,
    description = description,
    temperament = temperament
)
