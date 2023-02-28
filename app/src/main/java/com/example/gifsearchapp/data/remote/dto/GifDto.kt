package com.example.gifsearchapp.data.remote.dto

import com.example.gifsearchapp.domain.model.Gif

data class GifDto(
    val id: String,
    val username: String,
    val title: String,
    val rating: String,
    val images: Images
) {
    fun toGif() = Gif(
        id = id,
        username = username,
        title = title,
        rating = rating,
        url = images.original.url
    )
}

