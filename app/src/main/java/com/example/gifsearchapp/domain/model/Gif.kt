package com.example.gifsearchapp.domain.model

data class Gif(
    val id: String,
    val url: String,
    val username: String,
    val title: String,
    val rating: String,
    val images: Images
)
