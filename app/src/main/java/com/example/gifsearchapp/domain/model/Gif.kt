package com.example.gifsearchapp.domain.model

import com.example.gifsearchapp.data.remote.dto.Images

data class Gif(
    val id: String,
    val url: String,
    val username: String,
    val title: String,
    val rating: String
)
