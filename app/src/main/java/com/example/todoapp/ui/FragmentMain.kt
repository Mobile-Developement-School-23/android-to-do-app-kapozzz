package com.example.todoapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.myapplication.ui.theme.ApplicationTheme
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentMainLayoutBinding
import com.example.todoapp.di.DaggerFragmentMainComponent
import javax.inject.Inject

class FragmentMain : Fragment() {

    private lateinit var binding: FragmentMainLayoutBinding

    @Inject
    lateinit var layout: FragmentMainLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_layout, container, false)

        DaggerFragmentMainComponent
            .builder()
            .setActivity(requireActivity())
            .setNavController(requireActivity().findNavController(R.id.nav_host_container))
            .build()
            .inject(this)

        return ComposeView(requireContext()).apply {
            setContent {

                ApplicationTheme {

                    layout.SetUI()

                }

            }
        }
    }
}

