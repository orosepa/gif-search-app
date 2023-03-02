package com.example.gifsearchapp.di

import android.content.Context
import com.example.gifsearchapp.data.remote.GifSearchApi
import com.example.gifsearchapp.data.remote.GifSearchRepositoryImpl
import com.example.gifsearchapp.domain.repository.GifSearchRepository
import com.example.gifsearchapp.util.Constants
import com.example.gifsearchapp.util.NetworkStatusListener
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GifSearchModule {

    @Singleton
    @Provides
    fun provideGiphyApi() : GifSearchApi =
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GifSearchApi::class.java)

    @Singleton
    @Provides
    fun provideGifSearchRepository(api: GifSearchApi) : GifSearchRepository =
        GifSearchRepositoryImpl(api)

    @Singleton
    @Provides
    fun provideNetworkStatusListener(@ApplicationContext context: Context): NetworkStatusListener =
        NetworkStatusListener(context)
}