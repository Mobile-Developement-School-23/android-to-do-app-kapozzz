package com.example.todoapp.di.model

import android.app.Activity
import android.view.View
import androidx.lifecycle.LifecycleOwner

data class FragmentComponent(
    val activity: Activity,
    val rootView: View,
    val lifecycleOwner: LifecycleOwner
)