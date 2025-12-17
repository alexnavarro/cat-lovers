package com.alexandrenavarro.catlovers.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alexandrenavarro.catlovers.data.database.model.BreedPreviewEntity

@Database(entities = [BreedPreviewEntity::class], version = 1)
internal abstract class BreedsDatabase: RoomDatabase() {

    abstract fun breedsDao(): BreedsDao
}