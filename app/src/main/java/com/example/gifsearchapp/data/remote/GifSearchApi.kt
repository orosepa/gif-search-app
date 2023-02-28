package com.example.gifsearchapp.data.remote

import com.example.gifsearchapp.data.remote.dto.GifDto
import com.example.gifsearchapp.data.remote.dto.GifSearchResponseDto
import com.example.gifsearchapp.domain.model.GifSearchResponse
import com.example.gifsearchapp.domain.model.Gif
import com.example.gifsearchapp.util.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GifSearchApi {

    @GET("/v1/gifs/search")
    suspend fun searchGifs(
        @Query("q") query: String,
        @Query("offset") offset: Int,
        @Query("rating") rating: String? = null,
        @Query("api_key") api_key: String = Constants.API_KEY,
        @Query("limit") limit: Int = Constants.PAGE_SIZE
    ) : Response<GifSearchResponseDto>

    @GET("/v1/gifs/{gif_id}")
    suspend fun getGifById(
        @Path(value = "gif_id", encoded = true) id: String,
        @Query("api_key") api_key: String = Constants.API_KEY
    ) : Response<GifDto>
}