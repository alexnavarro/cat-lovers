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
    @SerializedName("life_span")
    val lifeSpan: String?,
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
    averageLifeSpan = lifeSpan?.toAverageLifeSpan() ?: 0
)

private fun String.toAverageLifeSpan(): Int {
    val lowerLifeSpan = substringBefore(" - ").toIntOrNull()
    val higherLifeSpan = substringAfter(" - ").toIntOrNull()

    if (lowerLifeSpan == null || higherLifeSpan == null) {
        return 0
    }

    return (lowerLifeSpan + higherLifeSpan) / 2
}