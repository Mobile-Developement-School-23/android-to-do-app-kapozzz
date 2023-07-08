package com.example.todoapp.ioc.di

import com.example.todoapp.data.RepositoryToDo
import com.example.todoapp.ioc.di.modules.AppModule
import dagger.Component

@Component(modules = [AppModule::class])
interface ApplicationComponent {
    fun getRepositoryInstance(): RepositoryToDo
}