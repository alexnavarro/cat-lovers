package com.alexandrenavarro.catlovers.data.database.di

import android.content.Context
import androidx.room.Room
import com.alexandrenavarro.catlovers.data.database.CatBreedsDatabase
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
    fun providesCatBreedsDatabase(@ApplicationContext context: Context): CatBreedsDatabase =
        Room.databaseBuilder(
            context,
            CatBreedsDatabase::class.java,
            "cat-breeds-database"
        ).build()
}