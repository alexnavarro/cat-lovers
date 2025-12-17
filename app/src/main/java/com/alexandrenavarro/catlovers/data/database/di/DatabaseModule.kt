package com.alexandrenavarro.catlovers.data.database.di

import android.content.Context
import androidx.room.Room
import com.alexandrenavarro.catlovers.data.database.BreedsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Provides
    @Singleton
    fun providesBreedsDatabase(@ApplicationContext context: Context): BreedsDatabase =
        Room.databaseBuilder(
            context,
            BreedsDatabase::class.java,
            "breeds-database"
        ).build()
}