package com.example.gifsearchapp.data.remote

import com.example.gifsearchapp.domain.model.Gif
import com.example.gifsearchapp.domain.model.GifSearchResponse
import com.example.gifsearchapp.domain.repository.GifSearchRepository
import com.example.gifsearchapp.util.Resource
import retrofit2.Response

class GifSearchRepositoryImpl(
    private val api: GifSearchApi
) : GifSearchRepository {
    override suspend fun searchGifs(
        query: String,
        offset: Int,
        rating: String?
    ) : Response<GifSearchResponse> = api.searchGifs(query, offset, rating)

    override suspend fun getGifById(id: String) : Response<Gif> = api.getGifById(id)
}