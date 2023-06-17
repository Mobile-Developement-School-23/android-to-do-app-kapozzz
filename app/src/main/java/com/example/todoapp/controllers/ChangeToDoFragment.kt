package com.example.todoapp.controllers

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
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

class ChangeToDoFragment : Fragment() {

    private lateinit var binding: FragmentNewRedactTodoLayoutBinding

    private lateinit var todoViewModel: ViewModel

    private lateinit var navController: NavController

    private lateinit var argumentsFromMain: ChangeToDoFragmentArgs

    private lateinit var item: TodoItem

    private var deadline: Date? = null

    private var importance: Importance? = null

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

        todoViewModel = ViewModelProvider(
            this,
            ViewModelFactory(requireActivity().application)
        )[ViewModel::class.java]

        if (savedInstanceState != null) {
            item = todoViewModel.savedToDoItem ?: throw IllegalStateException("my error")
        } else {
            argumentsFromMain = ChangeToDoFragmentArgs.fromBundle(requireArguments())
            item = argumentsFromMain.todoitem
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = binding.root.findNavController()

        // Оставляем возможность редактирования только за невыполеными задачи, выполненые можно только просмотреть/удалить
        initChangeLogic()

        initPopUp()

        initDatePicker()

        binding.closeButton.setOnClickListener {
            navController.popBackStack()
        }

        binding.deleteButton.setOnClickListener {
            deleteToDo()
        }

        binding.saveButton.setOnClickListener { saveButtonOnClick() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(FLAG, 1)

        val editText = binding.editText
        val temp = editText.text.toString()

        val itemForSave = TodoItem(
            id = item.id,
            text = temp,
            importance = importance,
            deadline = deadline,
            isCompleted = item.isCompleted,
            creationDate = item.creationDate
        )
        todoViewModel.savedToDoItem = itemForSave
    }

    private fun initChangeLogic() {
        // Помещаем все поля todoitem во вьюшки
        setData()
        // Если задача выполнена, отключаем возможность редактирования и обновления
        taskCompleted()
    }

    private fun setData() {
        with(binding) {
            // Текст дела
            val editableTitle: Editable? =
                item.text.let { Editable.Factory.getInstance().newEditable(it) }
            editText.text = editableTitle

            // Важность дела
            importance = item.importance
            importanceButton.text = when (item.importance) {
                Importance.LOW -> resources.getString(R.string.low)
                Importance.URGENT -> resources.getString(R.string.urgent).also {
                    importanceButton.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red
                        )
                    )
                }

                else -> resources.getString(R.string.no)
            }

            val converter = SimpleDateFormat("dd.MM.yy", Locale("ru","RU"))
            val convertedDeadline = item.deadline?.let { converter.format(it) }
            val convertedCreationDate = converter.format(item.creationDate)

            // Крайний срок выполнения дела
            item.deadline?.let { itemDeadline ->
                deadline = itemDeadline
                deadlineText.visibility = View.VISIBLE
                deadlineText.text = convertedDeadline
                deadlineSwitch.isChecked = true
            }

            // Дата создания - видима
            creationDate.text = convertedCreationDate
            creationTextView.visibility = View.VISIBLE

            // Кнопка удаления - работает
            binding.deleteButton.isClickable = true
            deleteButtonImage.setColorFilter(ContextCompat.getColor(requireContext(), R.color.red))
            deleteButtonText.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        }
    }

    private fun taskCompleted() {
        if (!item.isCompleted) return
        with(binding) {
            saveButton.isClickable = false
            saveButton.text = getString(R.string.task_complete)
            saveButton.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_light_green
                )
            )
            untilDo.visibility = if (deadline != null) View.VISIBLE else View.GONE
            editText.isEnabled = false
            deadlineSwitch.visibility = View.GONE
        }
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
                    importance = null
                    true
                }

                R.id.ordinary_importance_btn -> {
                    importanceButton.text = getString(R.string.low)
                    val color =
                        ContextCompat.getColor(requireContext().applicationContext, R.color.gray)
                    importanceButton.setTextColor(color)
                    importance = Importance.LOW
                    true
                }

                R.id.urgent_importance_btn -> {
                    importanceButton.text = getString(R.string.urgent)
                    val color =
                        ContextCompat.getColor(requireContext().applicationContext, R.color.red)
                    importanceButton.setTextColor(color)
                    importance = Importance.URGENT
                    true
                }

                else -> false
            }
        }

        binding.importanceButton.setOnClickListener {
            if (!item.isCompleted) popupMenu.show()
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

            deadline = date
            deadlineText.text = deadline.toString()
            deadlineText.visibility = View.VISIBLE
        }

        switch.setOnClickListener {
            if (switch.isChecked) {
                val cal = Calendar.getInstance()
                val day = cal.get(Calendar.DAY_OF_MONTH)
                val month = cal.get(Calendar.MONTH)
                val year = cal.get(Calendar.YEAR)
                datePicker.show()
                datePicker.updateDate(year, month, day)
            } else {
                deadlineText.visibility = View.GONE
                deadline = null
            }
        }
    }

    private fun deleteToDo() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.are_you_sure))
        builder.setPositiveButton(R.string.yes) { _, _ ->
            todoViewModel.deleteToDo(item.id.toString())
            navController.popBackStack()
        }
        builder.setNegativeButton(R.string.no) { dialog, _ ->
            dialog.cancel()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun saveButtonOnClick(): Boolean {
        if (item.isCompleted) return true

        val editText = binding.editText
        val temp = editText.text.toString()

        if (temp.trim().isEmpty()) {
            editText.error = getString(R.string.empty_text_error)
            return true
        }

        val newToDo = TodoItem(
            id = item.id,
            text = temp,
            importance = importance,
            deadline = deadline,
            isCompleted = false,
            creationDate = item.creationDate
        )
        todoViewModel.updateToDo(newToDo)
        navController.popBackStack()
        return true
    }

    companion object {
        private const val FLAG = "82031Y13971317-1319-38PCN8912-19722"
    }
}