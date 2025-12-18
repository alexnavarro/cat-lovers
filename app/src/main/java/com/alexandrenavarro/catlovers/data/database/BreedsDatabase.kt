package com.alexandrenavarro.catlovers.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alexandrenavarro.catlovers.data.database.model.BreedPreviewEntity
import com.alexandrenavarro.catlovers.data.database.model.BreedRemoteKey
import com.alexandrenavarro.catlovers.data.database.model.FavoriteEntity

@Database(
    entities = [BreedPreviewEntity::class, BreedRemoteKey::class, FavoriteEntity::class],
    version = 1
)
abstract class BreedsDatabase : RoomDatabase() {

    abstract fun breedsDao(): BreedsDao

    abstract fun breedRemoteKeyDao(): BreedRemoteKeyDao

    abstract fun favoriteDao(): FavoriteDao
}