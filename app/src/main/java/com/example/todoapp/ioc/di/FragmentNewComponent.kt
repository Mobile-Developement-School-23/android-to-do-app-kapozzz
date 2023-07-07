package com.example.todoapp.ioc.di

import com.example.todoapp.ioc.di.model.FragmentComponent
import com.example.todoapp.ioc.di.modules.AppModule
import com.example.todoapp.ioc.di.modules.FragmentModule
import com.example.todoapp.ui.FragmentNew
import dagger.BindsInstance
import dagger.Component

@Component(modules = [FragmentModule::class, AppModule::class])
interface FragmentNewComponent {

    fun inject(instance: FragmentNew)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun fragmentComponent(fragmentComponent: FragmentComponent): Builder

        fun build(): FragmentNewComponent
    }

}