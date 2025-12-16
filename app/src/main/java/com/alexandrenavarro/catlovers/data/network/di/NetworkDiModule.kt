package com.alexandrenavarro.catlovers.data.network.di

import com.alexandrenavarro.catlovers.BuildConfig
import com.alexandrenavarro.catlovers.data.network.AuthHeaderInterceptor
import com.alexandrenavarro.catlovers.data.network.BreedApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkDiModule {

    @Provides
    @Singleton
    fun provideBaseUrl(): String = "https://api.thecatapi.com"

    @Provides
    @Singleton
    fun provideAuthInterceptor(): AuthHeaderInterceptor =
        AuthHeaderInterceptor(BuildConfig.API_KEY)

    @Provides
    fun provideOkHttpClient(authHeaderInterceptor: AuthHeaderInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authHeaderInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(baseUrl: String, client: OkHttpClient): Retrofit = Retrofit.Builder()
        .client(client)
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideBreedsApiService(retrofit: Retrofit): BreedApi =
        retrofit.create(BreedApi::class.java)
}