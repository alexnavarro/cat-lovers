package com.alexandrenavarro.catlovers.data.database.di

import com.alexandrenavarro.catlovers.data.database.BreedsDao
import com.alexandrenavarro.catlovers.data.database.BreedsDatabase
import com.alexandrenavarro.catlovers.data.database.FavoriteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DaoModule {

    @Provides
    @Singleton
    fun providesBreedsDao(breedsDatabase: BreedsDatabase): BreedsDao = breedsDatabase.breedsDao()

    @Provides
    @Singleton
    fun providesFavoriteDao(breedsDatabase: BreedsDatabase): FavoriteDao = breedsDatabase.favoriteDao()
}