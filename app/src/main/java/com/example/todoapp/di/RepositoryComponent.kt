package com.example.todoapp.di


import com.example.todoapp.data.RepositoryToDo
import com.example.todoapp.di.modules.AppModule
import dagger.Component


@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(instance: RepositoryToDo)
}
