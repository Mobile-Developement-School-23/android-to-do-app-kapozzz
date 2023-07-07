package com.example.todoapp.ioc

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.todoapp.data.client.ToDoDB
import com.example.todoapp.ioc.di.AppComponent
import com.example.todoapp.ioc.di.DaggerAppComponent
import javax.inject.Inject

class ToDoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun isInternetAvailableAppField(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val capabilities =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    companion object {
        private lateinit var instance: ToDoApplication

        fun getInstance(): ToDoApplication {
            return instance
        }
    }
}