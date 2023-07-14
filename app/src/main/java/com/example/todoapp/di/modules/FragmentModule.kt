package com.example.todoapp.di.modules

import android.app.Activity
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.example.todoapp.di.model.FragmentComponent
import com.example.todoapp.ui.MainActivity
import com.example.todoapp.ui.ShowToDoList
import com.example.todoapp.ui.ShowToDoListImpl
import com.example.todoapp.ui.viewmodel.ToDoViewModel
import dagger.Module
import dagger.Provides

@Module
class FragmentModule() {

    @Provides
    fun provideRootView(fragmentComponent: FragmentComponent): View {
        return fragmentComponent.rootView
    }

    @Provides
    fun provideLifeCycleOwner(fragmentComponent: FragmentComponent): LifecycleOwner {
        return fragmentComponent.lifecycleOwner
    }

    @Provides
    fun provideShowToDoList(viewModel: ToDoViewModel, navController: NavController): ShowToDoList {
        return ShowToDoListImpl(viewModel = viewModel, navController = navController)
    }

}
