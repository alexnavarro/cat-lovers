package com.alexandrenavarro.catlovers.data.di

import com.alexandrenavarro.catlovers.data.repository.CatBreedRepository
import com.alexandrenavarro.catlovers.data.repository.CatBreedRepositoryImpl
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
    internal abstract fun bindsBreedRepository(breedRepository: CatBreedRepositoryImpl): CatBreedRepository
}
