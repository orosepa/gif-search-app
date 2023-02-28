package com.example.gifsearchapp.data.remote.dto

import com.example.gifsearchapp.domain.model.Gif
import com.google.gson.annotations.SerializedName

data class GifDto(
    val id: String,
    val username: String,
    val title: String,
    val rating: String,
    val images: Images,
    @SerializedName("import_datetime")
    val importDatetime: String
) {
    fun toGif() = Gif(
        id = id,
        username = username,
        title = title,
        rating = rating,
        urlSmall = images.fixedWidth.url,
        urlOriginal = images.original.url,
        importDatetime = importDatetime
    )
}

