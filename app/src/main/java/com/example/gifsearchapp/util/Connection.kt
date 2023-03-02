package com.example.gifsearchapp.util

sealed class Connection {
    object Available : Connection()
    object Unavailable : Connection()
}
