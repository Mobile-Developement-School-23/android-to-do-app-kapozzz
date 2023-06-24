package com.example.todoapp.controllers

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentNewRedactTodoLayoutBinding
import com.example.todoapp.model.Importance
import com.example.todoapp.model.TodoItem
import com.example.todoapp.viewmodel.ViewModel
import com.example.todoapp.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class NewToDoFragment : Fragment() {

    private lateinit var binding: FragmentNewRedactTodoLayoutBinding
    private lateinit var todoViewModel: ViewModel
    private lateinit var navController: NavController
    private var importance: Importance = Importance.LOW
    private var deadline: Date? = null

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
        todoViewModel =
            ViewModelProvider(this, ViewModelFactory(requireActivity().application))[ViewModel::class.java]

        if (savedInstanceState != null) configurationChange()

        initPopUp()

        initDatePicker()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = binding.root.findNavController()
        binding.closeButton.setOnClickListener { closeButtonOnClick() }
        binding.saveButton.setOnClickListener { saveButtonOnClick() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(FLAG, 1)

        val editText = binding.editText
        val temp = editText.text.toString()

        val itemForSave = TodoItem(
            id = UUID.randomUUID(),
            text = temp,
            importance = importance,
            deadline = deadline,
            done = false,
            created_at = Date()
        )
        todoViewModel.savedToDoItem = itemForSave
    }

    private fun configurationChange() {
        val savedItem = todoViewModel.savedToDoItem
        savedItem?.deadline?.let { savedDeadline ->
            deadline = savedDeadline
            with(binding) {
                val converter = SimpleDateFormat("dd.MM.yyyy", Locale("ru", "RU"))
                val convertedDate = converter.format(savedDeadline)
                deadlineSwitch.isChecked = true
                deadlineText.visibility = View.VISIBLE
                deadlineText.text = convertedDate
            }
        }
        importance = savedItem?.importance!!
        when(savedItem?.importance) {
            Importance.LOW -> binding.importanceButton.text = getString(R.string.no)
            Importance.BASIC -> binding.importanceButton.text = getString(R.string.low)
            Importance.IMPORTANT -> binding.importanceButton.text = getString(R.string.urgent).also {
                binding.importanceButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_light_red))
            }
            else -> throw IllegalStateException("invalid importance state")
        }
    }

    private fun closeButtonOnClick() {
        navController.popBackStack()
    }

    private fun saveButtonOnClick(): Boolean {
        val editText = binding.editText
        val temp = editText.text.toString()
        if (temp.trim().isEmpty()) {
            editText.error = getString(R.string.empty_text_error)
            return true
        }

        val completed = false
        val newToDo = TodoItem(
            text = temp,
            importance = importance,
            deadline = if (binding.deadlineSwitch.isChecked) deadline else null,
            done = completed,
            created_at = Date()
        )
        todoViewModel.insertToDo(newToDo)
        navController.popBackStack()
        return true
    }

    private fun initPopUp() {
        val popupMenu =
            androidx.appcompat.widget.PopupMenu(requireContext(), binding.importanceButton)
        popupMenu.inflate(R.menu.popup_importance_layout)
        popupMenu.setOnMenuItemClickListener {
            val importanceButton = binding.importanceButton
            when (it.itemId) {
                R.id.low_importance_btn -> {
                    importanceButton.text = getString(R.string.no)
                    val color =
                        ContextCompat.getColor(requireContext().applicationContext, R.color.gray)
                    importanceButton.setTextColor(color)
                    importance = Importance.LOW
                    true
                }

                R.id.ordinary_importance_btn -> {
                    importanceButton.text = getString(R.string.low)
                    val color =
                        ContextCompat.getColor(requireContext().applicationContext, R.color.gray)
                    importanceButton.setTextColor(color)
                    importance = Importance.BASIC
                    true
                }

                R.id.urgent_importance_btn -> {
                    importanceButton.text = getString(R.string.urgent)
                    val color =
                        ContextCompat.getColor(requireContext().applicationContext, R.color.red)
                    importanceButton.setTextColor(color)
                    importance = Importance.IMPORTANT
                    true
                }

                else -> false
            }
        }

        binding.importanceButton.setOnClickListener {
            popupMenu.show()
        }
    }

    private fun initDatePicker() {
        val datePicker = DatePickerDialog(requireContext())
        val switch = binding.deadlineSwitch
        val deadlineText = binding.deadlineText

        datePicker.setOnCancelListener {
            switch.isChecked = false
        }

        datePicker.setOnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            val cal = Calendar.getInstance()
            cal.set(year, monthOfYear, dayOfMonth)
            val date = cal.time

            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale("ru", "RU"))
            val formattedDate = dateFormat.format(date)

            deadline = date
            deadlineText.text = formattedDate
            deadlineText.visibility = View.VISIBLE
        }

        switch.setOnCheckedChangeListener { _, b ->
            if (b) {
                val cal = Calendar.getInstance()
                val day = cal.get(Calendar.DAY_OF_MONTH)
                val month = cal.get(Calendar.MONTH)
                val year = cal.get(Calendar.YEAR)
                datePicker.show()
                datePicker.updateDate(year, month, day)
            } else {
                deadlineText.visibility = View.GONE
            }
        }
    }

    companion object {
        const val FLAG = "LKMEFEOFKAOIDAODAOIDUHAIHDAD16753563"
    }
}