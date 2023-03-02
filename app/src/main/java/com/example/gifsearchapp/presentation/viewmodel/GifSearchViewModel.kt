package com.example.gifsearchapp.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gifsearchapp.R
import com.example.gifsearchapp.domain.model.Gif
import com.example.gifsearchapp.domain.model.GifSearchResponse
import com.example.gifsearchapp.domain.repository.GifSearchRepository
import com.example.gifsearchapp.util.Connection
import com.example.gifsearchapp.util.NetworkStatusListener
import com.example.gifsearchapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GifSearchViewModel @Inject constructor(
    private val repository: GifSearchRepository,
    private val networkStatusListener: NetworkStatusListener,
    @ApplicationContext context: Context
) : ViewModel() {

    companion object {
        const val TAG = "GifSearchViewModel"
    }

    private val _gifs: MutableLiveData<Resource<GifSearchResponse>> = MutableLiveData()
    val gifs: LiveData<Resource<GifSearchResponse>> = _gifs

    private val _currentGif: MutableLiveData<Resource<Gif>> = MutableLiveData()
    val currentGif: LiveData<Resource<Gif>> = _currentGif

    private var latestQuery: String = ""

    init {
        networkStatusListener.networkStatus.onEach { status ->
            when (status) {
                Connection.Available -> {
                    Log.i(TAG, "Internet connection is available!")
                    searchGifs(latestQuery)
                }
                Connection.Unavailable -> {
                    Log.i(TAG, "Internet connection is unavailable!")
                    _gifs.postValue(
                        Resource.Error(
                            message = context.getString(R.string.no_internet_connection)
                        )
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
    fun searchGifs(query: String, offset: Int = 0) = viewModelScope.launch {
        latestQuery = query
        Log.d(TAG, "Searching gifs by query `$query`")
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