package com.alexandrenavarro.catlovers.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alexandrenavarro.catlovers.data.database.model.CatBreedPreviewEntity
import com.alexandrenavarro.catlovers.data.database.model.CatBreedRemoteKey
import com.alexandrenavarro.catlovers.data.database.model.FavoriteEntity

@Database(
    entities = [CatBreedPreviewEntity::class, CatBreedRemoteKey::class, FavoriteEntity::class],
    version = 1
)
abstract class CatBreedsDatabase : RoomDatabase() {

    abstract fun catBreedsDao(): CatBreedsDao

    abstract fun catBreedsRemoteKeyDao(): CatBreedsRemoteKeyDao

    abstract fun favoriteDao(): FavoriteDao
}