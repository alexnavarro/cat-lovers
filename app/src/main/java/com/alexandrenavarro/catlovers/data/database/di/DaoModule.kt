package com.alexandrenavarro.catlovers.data.database.di

import com.alexandrenavarro.catlovers.data.database.CatBreedsDao
import com.alexandrenavarro.catlovers.data.database.CatBreedsDatabase
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
    fun providesCatBreedsDao(database: CatBreedsDatabase): CatBreedsDao = database.catBreedsDao()

    @Provides
    @Singleton
    fun providesFavoriteDao(database: CatBreedsDatabase): FavoriteDao = database.favoriteDao()
}