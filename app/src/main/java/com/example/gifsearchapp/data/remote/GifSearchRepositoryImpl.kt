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
    ) : Resource <GifSearchResponse> {
        val response = api.searchGifs(query, offset, rating)
        if (response.isSuccessful)
            return Resource.Success(response.body()!!.toGifSearchResponse())
        return Resource.Error(response.message())
    }

    override suspend fun getGifById(id: String) : Resource<Gif> {
        val response = api.getGifById(id)
        if (response.isSuccessful)
            return Resource.Success(response.body()!!.toGif())
        return Resource.Error(response.message())
    }
}