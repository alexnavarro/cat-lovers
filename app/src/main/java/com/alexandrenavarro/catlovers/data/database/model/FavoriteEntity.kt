package com.alexandrenavarro.catlovers.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorites",
)
data class FavoriteEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "image_id")
    val imageId: String
)
