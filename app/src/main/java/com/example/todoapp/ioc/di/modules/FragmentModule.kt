package com.example.todoapp.ioc.di.modules

import android.app.Activity
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.example.todoapp.ioc.di.model.FragmentComponent
import dagger.Module
import dagger.Provides

@Module
class FragmentModule() {
    @Provides
    fun provideActivity(fragmentComponent: FragmentComponent): Activity {
        return fragmentComponent.activity
    }

    @Provides
    fun provideRootView(fragmentComponent: FragmentComponent): View {
        return fragmentComponent.rootView
    }

    @Provides
    fun provideLifeCycleOwner(fragmentComponent: FragmentComponent): LifecycleOwner {
        return fragmentComponent.lifecycleOwner
    }
}
