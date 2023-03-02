package com.example.gifsearchapp.domain.model

import com.google.gson.annotations.SerializedName

data class Pagination(
    @SerializedName("total_count")
    val totalCount: Int,
    val offset: Int
)
