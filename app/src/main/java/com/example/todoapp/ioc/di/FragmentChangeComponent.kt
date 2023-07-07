package com.example.todoapp.ioc.di

import com.example.todoapp.ioc.di.model.FragmentComponent
import com.example.todoapp.ioc.di.modules.AppModule
import com.example.todoapp.ioc.di.modules.FragmentModule
import com.example.todoapp.ui.FragmentChange
import dagger.BindsInstance
import dagger.Component


@Component(modules = [FragmentModule::class, AppModule::class])
interface FragmentChangeComponent {

    fun inject(instance: FragmentChange)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun fragmentComponent(fragmentComponent: FragmentComponent): Builder

        fun build(): FragmentChangeComponent
    }

}