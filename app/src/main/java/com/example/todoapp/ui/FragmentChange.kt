package com.example.todoapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentNewRedactTodoLayoutBinding
import com.example.todoapp.ioc.di.model.FragmentComponent
import com.example.todoapp.ioc.di.DaggerFragmentChangeComponent
import javax.inject.Inject

open class FragmentChange : Fragment() {

    private lateinit var binding: FragmentNewRedactTodoLayoutBinding

    private lateinit var argumentsFromMain: FragmentChangeArgs

    @Inject
    lateinit var fragmentChangeViewController: FragmentChangeViewController

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
            .build()
            .inject(this)

        if (savedInstanceState != null) {
            fragmentChangeViewController.configurationChange()
        } else {
            argumentsFromMain = FragmentChangeArgs.fromBundle(requireArguments())
            fragmentChangeViewController.setArgumentsFromMain(argumentsFromMain.todoitem)
        }

        fragmentChangeViewController.setViews()

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(FLAG, 1)
        fragmentChangeViewController.saveToDoState()
    }

    companion object {
        private const val FLAG = "82031Y13971317-1319-38PCN8912-19722"
    }
}