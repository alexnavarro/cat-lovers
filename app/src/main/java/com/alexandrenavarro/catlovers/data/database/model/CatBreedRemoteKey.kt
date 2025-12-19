package com.alexandrenavarro.catlovers.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cat_breeds_remote_keys")
data class CatBreedRemoteKey(
    @PrimaryKey val breedId: String,
    val prevKey: Int?,
    val nextKey: Int?
)
