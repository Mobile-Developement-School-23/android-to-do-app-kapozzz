package com.example.todoapp.ioc.di.modules

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.todoapp.data.client.DAO
import com.example.todoapp.data.client.ToDoDB
import com.example.todoapp.data.usecases.Gson
import com.example.todoapp.data.usecases.OkHttpClient
import com.example.todoapp.data.ToDoApiService
import com.example.todoapp.ioc.ToDoApplication
import com.example.todoapp.ioc.di.model.FragmentComponent
import com.example.todoapp.ui.usecases.UserNotificationHandler
import com.example.todoapp.ui.usecases.UserNotificationHandlerImpl
import com.example.todoapp.viewmodel.ToDoViewModel
import com.example.todoapp.viewmodel.ToDoViewModelFactory
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module(includes = [NetworkModule::class, LocalDataModule::class])
class AppModule {
    @Provides
    fun provideViewModel(fragmentComponent: FragmentComponent, viewModelFactory: ToDoViewModelFactory): ToDoViewModel {
        return ViewModelProvider(
            fragmentComponent.activity as ViewModelStoreOwner,
            viewModelFactory
        )[ToDoViewModel::class.java]
    }

    @Provides
    fun provideApplication(): ToDoApplication {
        return ToDoApplication.getInstance()
    }

    @Provides
    fun provideNotificationHandler() : UserNotificationHandler {
        return UserNotificationHandlerImpl.getInstance()
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

}

@Module
class LocalDataModule {

    @Provides
    fun provideDao(application: ToDoApplication): DAO {
        return ToDoDB.getDatabase(application).mainDAO()
    }

}