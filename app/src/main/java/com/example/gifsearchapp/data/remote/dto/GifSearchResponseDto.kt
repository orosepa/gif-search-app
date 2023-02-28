package com.example.gifsearchapp.data.remote.dto

import com.example.gifsearchapp.domain.model.GifSearchResponse
import com.example.gifsearchapp.domain.model.Meta

data class GifSearchResponseDto (
    val data: List<GifDto>,
    val meta: Meta
) {
    fun toGifSearchResponse() = GifSearchResponse(
        data = data.map { it.toGif() },
        meta = meta
    )
}