package com.example.gifsearchapp.util

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class NetworkStatusListener @Inject constructor(context: Context) {

    private val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

    val networkStatus = callbackFlow {
        val networkStatusCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onUnavailable() {
                trySend(Connection.Unavailable)
            }

            override fun onLost(network: Network) {
                trySend(Connection.Unavailable)
            }

            override fun onAvailable(network: Network) {
                trySend(Connection.Available)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NET_CAPABILITY_INTERNET)
            .build()
        cm.registerNetworkCallback(request, networkStatusCallback)

        if (isNetworkAvailable()) trySend(Connection.Available)
        else trySend(Connection.Unavailable)

        awaitClose {
            cm.unregisterNetworkCallback(networkStatusCallback)
        }
    }

    private fun isNetworkAvailable() = cm.run {
        getNetworkCapabilities(activeNetwork)?.run {
            hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } ?: false
    }

}