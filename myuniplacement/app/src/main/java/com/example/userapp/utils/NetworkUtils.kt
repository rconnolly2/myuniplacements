package com.example.userapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.getSystemService

object NetworkUtils {
    fun isOnline(context: Context): Boolean {
        val manager = context.getSystemService<ConnectivityManager>() ?: return false
        val network = manager.activeNetwork ?: return false
        val info = manager.getNetworkCapabilities(network) ?: return false
        return info.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}