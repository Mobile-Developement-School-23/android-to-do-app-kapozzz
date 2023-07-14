package com.example.todoapp.di

import android.app.Activity
import androidx.navigation.NavController
import com.example.todoapp.di.model.FragmentComponent
import com.example.todoapp.di.modules.AppModule
import com.example.todoapp.di.modules.FragmentModule
import com.example.todoapp.ui.FragmentChange
import dagger.BindsInstance
import dagger.Component


@Component(modules = [FragmentModule::class, AppModule::class])
interface FragmentChangeComponent {

    fun inject(instance: FragmentChange)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun setActivity(activity: Activity): Builder

        @BindsInstance
        fun fragmentComponent(fragmentComponent: FragmentComponent): Builder

        @BindsInstance
        fun setNavController(navController: NavController): Builder

        fun build(): FragmentChangeComponent
    }

}