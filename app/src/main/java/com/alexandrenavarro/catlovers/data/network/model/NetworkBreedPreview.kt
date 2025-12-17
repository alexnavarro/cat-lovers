package com.alexandrenavarro.catlovers.data.network.model

import com.alexandrenavarro.catlovers.data.database.model.BreedPreviewEntity
import com.google.gson.annotations.SerializedName

data class NetworkBreedPreview(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("image")
    val image: NetworkBreedImage?,
)

data class NetworkBreedImage(
    @SerializedName("id")
    val id: String,
    @SerializedName("url")
    val imageUrl: String,
)

fun NetworkBreedPreview.toBreedPreviewEntity() = BreedPreviewEntity(
    id = id,
    name = name,
    imageUrl = image?.imageUrl,
    imageId = image?.id,
)