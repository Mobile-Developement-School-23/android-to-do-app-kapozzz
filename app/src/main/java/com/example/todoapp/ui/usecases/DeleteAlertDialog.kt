package com.example.todoapp.ui.usecases

import android.app.Activity
import android.app.AlertDialog
import android.view.View
import androidx.navigation.findNavController
import com.example.todoapp.R
import com.example.todoapp.ui.model.TodoItem
import com.example.todoapp.viewmodel.ToDoViewModel

class DeleteAlertDialog (
   val activity: Activity,
   val rootView: View,
   val item: TodoItem,
   val viewModel: ToDoViewModel
) {

    fun getDeleteDialog(): AlertDialog? {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(activity.getString(R.string.are_you_sure))
        builder.setPositiveButton(R.string.yes)
        {
                _, _ ->
            viewModel.deleteToDo(item)
            rootView.findNavController().popBackStack()
        }
        builder.setNegativeButton(R.string.no)
        {
                dialog, _ ->
            dialog.cancel()
        }
        val dialog = builder.create()
        return dialog
    }
}