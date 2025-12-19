package com.alexandrenavarro.catlovers.data.di

import com.alexandrenavarro.catlovers.data.repository.BreedRepository
import com.alexandrenavarro.catlovers.data.repository.BreedRepositoryImpl
import com.alexandrenavarro.catlovers.data.repository.FavoriteRepositoryImpl
import com.alexandrenavarro.catlovers.data.repository.FavoriteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    internal abstract fun bindsFavoriteRepository(favoriteRepository: FavoriteRepositoryImpl): FavoriteRepository


    @Binds
    internal abstract fun bindsBreedRepository(breedRepository: BreedRepositoryImpl): BreedRepository
}
