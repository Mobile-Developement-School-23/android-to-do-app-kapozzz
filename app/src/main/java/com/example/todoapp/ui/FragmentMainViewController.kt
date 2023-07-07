package com.example.todoapp.ui

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.ioc.ToDoApplication
import com.example.todoapp.ui.model.TodoItem
import com.example.todoapp.ui.usecases.CustomItemTouchHelper
import com.example.todoapp.ui.usecases.OnClickCallbacks
import com.example.todoapp.ui.usecases.ToDoAdapter
import com.example.todoapp.viewmodel.ToDoViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class FragmentMainViewController @Inject constructor (
    val activity: Activity,
    val rootView: View,
    val lifecycleOwner: LifecycleOwner,
    val viewModel: ToDoViewModel,
) : OnClickCallbacks {

    val recyclerView = rootView.findViewById<RecyclerView>(R.id.main_recycler_view)

    val mainAdapter: ToDoAdapter = ToDoAdapter(this)

    fun setViews() {

        initRecyclerView()

        initObservers()

        initSwipeCallbacks()

        initListeners()

        initProgressBar()

        viewModel.refreshList()
    }

    private fun initListeners() {

        val addButton = rootView.findViewById<FloatingActionButton>(R.id.add_button)

        addButton.setOnClickListener {
            rootView.findNavController()
                .navigate(FragmentMainDirections.actionMainToDoFragmentToNewToDoFragment())
        }

        val hideOrShowComTaskButton =
            rootView.findViewById<AppCompatImageButton>(R.id.hide_or_show_com_task_button)

        hideOrShowComTaskButton.setOnClickListener {
            hideOrShowBtOnClick(hideOrShowComTaskButton)
        }

        hideOrShowRefreshButton()
    }

    private fun hideOrShowBtOnClick(bt: AppCompatImageButton) {
        if (mainAdapter.completedTasksIsVisible) {
            bt.setImageDrawable(
                ContextCompat.getDrawable(
                    activity, R.drawable.ic_hide_completed_tasks
                )
            )
            mainAdapter.completedTasksIsVisible = false
        } else {
            bt.setImageDrawable(
                ContextCompat.getDrawable(
                    activity, R.drawable.ic_show_completed_tasks
                )
            )
            mainAdapter.completedTasksIsVisible = true
        }
    }


    private fun initProgressBar() {
        val progressBar = rootView.findViewById<ProgressBar>(R.id.main_fragment_progress_bar)
        val refreshButton = rootView.findViewById<AppCompatImageButton>(R.id.refresh_button)
        viewModel.progressBarIsActive.observe(lifecycleOwner) { isActive ->
            if (isActive) {
                refreshButton.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
                refreshButton.visibility = View.VISIBLE
            }
        }
    }

    private fun hideOrShowRefreshButton() {
        val refreshButton = rootView.findViewById<AppCompatImageButton>(R.id.refresh_button)
        val noInternetButton = rootView.findViewById<AppCompatImageButton>(R.id.no_internet_button)
        if ((activity.application as ToDoApplication).isInternetAvailableAppField()) {
            refreshButton.visibility = View.VISIBLE
            noInternetButton.visibility = View.GONE
            refreshButton.setOnClickListener {
                viewModel.refreshList()
            }
        } else {
            refreshButton.visibility = View.GONE
            noInternetButton.visibility = View.VISIBLE
            noInternetButton.setOnClickListener {
                Snackbar.make(
                    activity,
                    rootView,
                    activity.getString(R.string.error400),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun initSwipeCallbacks() {
        val itemTouchHelper = CustomItemTouchHelper(
            context = activity,
            adapter = mainAdapter,
            viewModel = viewModel,
            itemView = rootView
        ).getTouchHelper()
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun initRecyclerView() {
        with(recyclerView) {
            adapter = mainAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun initObservers() {

        viewModel.snackbarWithError = { message: String ->
            Snackbar.make(
                rootView,
                message,
                Snackbar.LENGTH_LONG
            ).show()
        }

        viewModel.allToDoItems.observe(
            lifecycleOwner
        ) { newToDoList ->
            val clearList = newToDoList?.filter { it.color != Color.RED.toString() }
                ?.sortedByDescending { it.changed_at } ?: emptyList()
            mainAdapter.submitList(clearList)
        }

        viewModel.completedTaskCount.observe(lifecycleOwner) { completedTaskCount ->
            val completedTaskCountText =
                activity.resources.getString(R.string.task_complete_count) + completedTaskCount
            rootView.findViewById<TextView>(R.id.completed_todo_textView).text =
                completedTaskCountText
        }
    }

    override fun onItemClick(item: TodoItem) {
        rootView.findNavController()
            .navigate(FragmentMainDirections.actionMainToDoFragmentToChangeToDoFragment(item))
    }
}