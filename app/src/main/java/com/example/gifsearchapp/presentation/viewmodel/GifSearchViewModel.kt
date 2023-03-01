package com.example.gifsearchapp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gifsearchapp.domain.model.Gif
import com.example.gifsearchapp.domain.model.GifSearchResponse
import com.example.gifsearchapp.domain.repository.GifSearchRepository
import com.example.gifsearchapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GifSearchViewModel @Inject constructor(
    private val repository: GifSearchRepository
) : ViewModel() {
    private val _gifs: MutableLiveData<Resource<GifSearchResponse>> = MutableLiveData()
    val gifs: LiveData<Resource<GifSearchResponse>> = _gifs

    private val _currentGif: MutableLiveData<Resource<Gif>> = MutableLiveData()
    val currentGif: LiveData<Resource<Gif>> = _currentGif

    fun searchGifs(query: String, offset: Int) = viewModelScope.launch {
        _gifs.postValue(Resource.Loading())
        val response = repository.searchGifs(query, offset)
        _gifs.postValue(response)
    }

    fun getGifById(id: String) = viewModelScope.launch {
        _currentGif.postValue(Resource.Loading())
        val response = repository.getGifById(id)
        _currentGif.postValue(response)
    }
}