package com.example.todoapp.ioc

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.todoapp.data.usecases.PeriodicWork
import com.example.todoapp.ioc.di.ApplicationComponent
import com.example.todoapp.ioc.di.DaggerApplicationComponent
import com.example.todoapp.ui.usecases.UserNotificationHandlerImpl
import java.util.concurrent.TimeUnit

class ToDoApplication : Application() {

    var applicationComponent: ApplicationComponent? = null

    companion object {
        private lateinit var instance: ToDoApplication

        fun getInstance(): ToDoApplication {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        applicationComponent = DaggerApplicationComponent.create()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .setRequiresStorageNotLow(true)
            .build()

        val periodicWorkerRequest = PeriodicWorkRequestBuilder<PeriodicWork>(
            repeatInterval = 8,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this@ToDoApplication).enqueueUniquePeriodicWork(
            "Refresh data",
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicWorkerRequest
        )
    }

    override fun onTerminate() {
        super.onTerminate()
        applicationComponent = null
        UserNotificationHandlerImpl.getInstance().destroyObserver()
    }

    fun isInternetAvailableAppField(): Boolean {
        val connectivityManager =
            instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val capabilities =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}