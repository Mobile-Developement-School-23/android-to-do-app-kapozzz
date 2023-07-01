package com.example.todoapp.model

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

class ToDoApplication : Application() {

    lateinit var applicationScope: CoroutineScope
    lateinit var settings: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        settings = getSharedPreferences(SP_NAME, MODE_PRIVATE)
        applicationScope = CoroutineScope(Dispatchers.IO)
    }

    override fun onTerminate() {
        super.onTerminate()
        applicationScope.cancel()
    }

    fun isFirstStart(): Boolean {
        val answer = settings.getBoolean(FIRST_START, true)
        if (answer) settings.edit().putBoolean(FIRST_START, false).apply()
        return answer
    }

    fun isInternetAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val capabilities =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    companion object {
        private const val SP_NAME = "settings"
        private const val FIRST_START ="first start"
    }
}