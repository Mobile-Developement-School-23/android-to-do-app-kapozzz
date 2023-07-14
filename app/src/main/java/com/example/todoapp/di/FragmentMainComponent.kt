package com.example.todoapp.di

import android.app.Activity
import androidx.navigation.NavController
import com.example.todoapp.di.modules.AppModule
import com.example.todoapp.di.modules.FragmentModule
import com.example.todoapp.ui.FragmentMain
import dagger.BindsInstance
import dagger.Component

@Component(modules = [FragmentModule::class, AppModule::class])
interface FragmentMainComponent {

    fun inject(instance: FragmentMain)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun setActivity(activity: Activity): Builder

        @BindsInstance
        fun setNavController(navController: NavController): Builder



        fun build(): FragmentMainComponent
    }
}