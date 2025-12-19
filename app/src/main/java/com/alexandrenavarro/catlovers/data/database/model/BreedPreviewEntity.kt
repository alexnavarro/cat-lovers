package com.alexandrenavarro.catlovers.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "breeds",
)
data class BreedPreviewEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "image_url")
    val imageUrl: String?,
    @ColumnInfo(name = "image_id")
    val imageId: String?,
    @ColumnInfo(name = "average_life_span")
    val averageLifeSpan: Int,
)