package com.example.todoapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentNewRedactTodoLayoutBinding
import com.example.todoapp.di.model.FragmentComponent
import com.example.todoapp.di.DaggerFragmentChangeComponent
import javax.inject.Inject

open class FragmentChange : Fragment() {

    private lateinit var binding: FragmentNewRedactTodoLayoutBinding

    @Inject
    lateinit var layout: FragmentChangeLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_new_redact_todo_layout,
            container,
            false
        )

        val component = FragmentComponent(
            requireActivity(),
            binding.root,
            viewLifecycleOwner
        )

        DaggerFragmentChangeComponent.builder()
            .fragmentComponent(component)
            .setActivity(requireActivity())
            .setNavController(requireActivity().findNavController(R.id.nav_host_container))
            .build()
            .inject(this)


        return ComposeView(requireActivity()).apply {
            setContent {
                layout.SetUI()
            }
        }
    }
}