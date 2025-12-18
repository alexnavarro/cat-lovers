package com.alexandrenavarro.catlovers.data.database.di

import com.alexandrenavarro.catlovers.data.database.BreedsDao
import com.alexandrenavarro.catlovers.data.database.BreedsDatabase
import com.alexandrenavarro.catlovers.data.database.FavoriteDao
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object DaoModule {

    fun providesBreedsDao(breedsDatabase: BreedsDatabase): BreedsDao = breedsDatabase.breedsDao()

    fun providesFavoriteDao(breedsDatabase: BreedsDatabase): FavoriteDao = breedsDatabase.favoriteDao()
}