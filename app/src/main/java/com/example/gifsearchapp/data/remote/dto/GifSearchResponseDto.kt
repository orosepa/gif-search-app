package com.example.gifsearchapp.data.remote.dto

import com.example.gifsearchapp.domain.model.GifSearchResponse
import com.example.gifsearchapp.domain.model.Pagination

data class GifSearchResponseDto (
    val data: List<GifDto>,
    val pagination: Pagination
) {
    fun toGifSearchResponse() = GifSearchResponse(
        data = data.map { it.toGif() },
        pagination = pagination
    )
}