package com.alexandrenavarro.catlovers.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alexandrenavarro.catlovers.domain.model.BreedPreview

@Entity(
    tableName = "breeds",
)
data class BreedPreviewEntity (
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "image_url")
    val imageUrl: String?,
    @ColumnInfo(name = "image_id")
    val imageId: String?
)

fun BreedPreviewEntity.asExternalModel() = BreedPreview(
    id = id,
    name = name,
    imageUrl = imageUrl,
    imageId = imageId,
//    isFavorite = false//TODO FIX IT
)