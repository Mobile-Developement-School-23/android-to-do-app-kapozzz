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
import com.example.todoapp.ioc.di.DaggerFragmentNewComponent
import javax.inject.Inject

class FragmentNew : Fragment() {

    private lateinit var binding: FragmentNewRedactTodoLayoutBinding

    @Inject
    lateinit var fragmentNewViewController: FragmentNewViewController

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

        DaggerFragmentNewComponent.builder()
            .fragmentComponent(component)
            .build()
            .inject(this)

        if (savedInstanceState != null) fragmentNewViewController.configurationChange()

        fragmentNewViewController.changeElements()

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(FLAG, 1)
        fragmentNewViewController.saveItemState()
    }

    companion object {
        const val FLAG = "LKMEFEOFKAOIDAODAOIDUHAIHDAD16753563"
    }
}