package com.example.gifsearchapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class Images(
    val original: Original,
    @SerializedName("fixed_width")
    val fixedWidth: FixedWidth
)
