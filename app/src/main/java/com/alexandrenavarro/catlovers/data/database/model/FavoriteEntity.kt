package com.alexandrenavarro.catlovers.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorites",
    foreignKeys = [
        ForeignKey(
            entity = BreedPreviewEntity::class,
            parentColumns = ["image_id"],
            childColumns = ["image_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["image_id"])]
)
data class FavoriteEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "image_id")
    val imageId: String
)
