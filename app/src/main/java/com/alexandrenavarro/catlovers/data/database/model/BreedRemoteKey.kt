package com.alexandrenavarro.catlovers.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "breed_remote_keys")
data class BreedRemoteKey(
    @PrimaryKey val breedId: String,
    val prevKey: Int?,
    val nextKey: Int?
)
