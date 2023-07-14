package com.example.todoapp.di.modules

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.todoapp.data.client.DAO
import com.example.todoapp.data.client.ToDoDB
import com.example.todoapp.data.usecases.Gson
import com.example.todoapp.data.usecases.OkHttpClient
import com.example.todoapp.data.ToDoApiService
import com.example.todoapp.ToDoApplication
import com.example.todoapp.utils.UserNotificationHandler
import com.example.todoapp.utils.UserNotificationHandlerImpl
import com.example.todoapp.ui.viewmodel.ToDoViewModel
import com.example.todoapp.ui.viewmodel.ToDoViewModelFactory
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module(includes = [NetworkModule::class, LocalDataModule::class])
class AppModule {
    @Provides
    fun provideViewModel(activity: Activity, viewModelFactory: ToDoViewModelFactory): ToDoViewModel {
        return ViewModelProvider(
            activity as ViewModelStoreOwner,
            viewModelFactory
        )[ToDoViewModel::class.java]
    }

    @Provides
    fun provideApplication(): ToDoApplication {
        return ToDoApplication.getInstance()
    }

    @Provides
    fun provideNotificationHandler() : UserNotificationHandler {
        return UserNotificationHandlerImpl
    }

}

@Module
class NetworkModule {

    @Provides
    fun provideRetrofitApi(okHttpClient: OkHttpClient, gson: Gson): ToDoApiService {
        return Retrofit.Builder()
            .client(okHttpClient.getOkHttpClient)
            .baseUrl("https://beta.mrdekk.ru/todobackend/")
            .addConverterFactory(GsonConverterFactory.create(gson.getGson))
            .build()
            .create(ToDoApiService::class.java)
    }

    @Provides
    fun provideConnectivityManager(application: ToDoApplication): ConnectivityManager {
        return application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

}

@Module
class LocalDataModule {

    @Provides
    fun provideDao(application: ToDoApplication): DAO {
        return ToDoDB.getDatabase(application).mainDAO()
    }

}