package com.example.gifsearchapp.domain.repository

import com.example.gifsearchapp.domain.model.Gif
import com.example.gifsearchapp.domain.model.GifSearchResponse
import com.example.gifsearchapp.util.Resource
import retrofit2.Response

interface GifSearchRepository {
    suspend fun searchGifs(
        query: String,
        offset: Int,
        rating: String? = null
    ) : Resource<GifSearchResponse>

    suspend fun getGifById(
        id: String
    ) : Resource<Gif>
}