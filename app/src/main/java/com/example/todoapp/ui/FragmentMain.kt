package com.example.todoapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentMainLayoutBinding
import com.example.todoapp.ioc.di.model.FragmentComponent
import com.example.todoapp.ioc.di.DaggerFragmentMainComponent
import javax.inject.Inject


class FragmentMain : Fragment(){

    private lateinit var binding: FragmentMainLayoutBinding

    @Inject lateinit var fragmentMainViewController: FragmentMainViewController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_layout, container, false)

        val component = FragmentComponent(
            requireActivity(),
            binding.root,
            viewLifecycleOwner
        )

        DaggerFragmentMainComponent
            .builder()
            .fragmentComponent(component)
            .build()
            .inject(this)

        Log.d("Controller state", "${fragmentMainViewController.recyclerView}")
        fragmentMainViewController.setViews()
        return binding.root
    }
}

