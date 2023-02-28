package com.example.gifsearchapp.domain.model

data class Gif(
    val id: String,
    val urlSmall: String,
    val urlOriginal: String,
    val username: String,
    val title: String,
    val rating: String
)
