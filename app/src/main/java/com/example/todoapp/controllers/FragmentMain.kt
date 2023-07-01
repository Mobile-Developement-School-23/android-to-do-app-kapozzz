package com.example.todoapp.controllers

import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.data.repository.NetworkResult
import com.example.todoapp.databinding.FragmentMainLayoutBinding
import com.example.todoapp.model.ToDoAdapter
import com.example.todoapp.model.ToDoApplication
import com.example.todoapp.model.TodoItem
import com.example.todoapp.model.onClickCallbacks
import com.example.todoapp.viewmodel.ViewModel
import com.example.todoapp.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator


class FragmentMain : Fragment(), onClickCallbacks {

    private lateinit var binding: FragmentMainLayoutBinding

    private lateinit var todoViewModel: ViewModel

    private lateinit var recyclerView: RecyclerView

    private lateinit var mainAdapter: ToDoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_layout, container, false)

        todoViewModel =
            ViewModelProvider(
                this,
                ViewModelFactory(requireActivity().application as ToDoApplication)
            ).get(
                ViewModel::class.java
            )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()

        initObservers()

        initSwipeCallbacks()

        initProgressBar()

        initListeners()

        if ((requireActivity().application as ToDoApplication).isInternetAvailable()) todoViewModel.updateDataFromServer()
    }

    override fun onItemClick(item: TodoItem) {
        binding.root.findNavController()
            .navigate(FragmentMainDirections.actionMainToDoFragmentToChangeToDoFragment(item))
    }

    private fun hideOrShowBtOnClick() {
        if (mainAdapter.completedTasksIsVisible) {
            binding.hideOrShowComTaskButton.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.ic_hide_completed_tasks
                )
            )
            mainAdapter.completedTasksIsVisible = false
        } else {
            binding.hideOrShowComTaskButton.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.ic_show_completed_tasks
                )
            )
            mainAdapter.completedTasksIsVisible = true
        }
    }

    private fun initProgressBar() {
        todoViewModel.progressBarIsActive.observe(viewLifecycleOwner, Observer { isActive ->
            if (isActive) {
                with(binding) {
                    mainFragmentProgressBar.visibility = View.VISIBLE
                    recyclerViewRootCardView.visibility = View.GONE
                    addButton.visibility = View.GONE
                    completedTodoTextView.visibility = View.GONE
                    hideOrShowComTaskButton.visibility = View.GONE
                    refreshButton.visibility = View.GONE
                    noInternetButton.visibility = View.GONE
                }
            } else {
                with(binding) {
                    mainFragmentProgressBar.visibility = View.GONE
                    recyclerViewRootCardView.visibility = View.VISIBLE
                    addButton.visibility = View.VISIBLE
                    completedTodoTextView.visibility = View.VISIBLE
                    hideOrShowComTaskButton.visibility = View.VISIBLE
                    hideOrShowRefreshButton()
                }
            }
        })
    }


    private fun initRecyclerView() {
        mainAdapter = ToDoAdapter(this)
        recyclerView = binding.mainRecyclerView

        with(recyclerView) {
            adapter = mainAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun initObservers() {
        todoViewModel.allToDoItems.observe(viewLifecycleOwner) { newToDoList ->
            val clearList = newToDoList?.filter { it.color != Color.RED.toString() }
                ?.sortedByDescending { it.changed_at } ?: emptyList()
            mainAdapter.submitList(clearList)
        }

        todoViewModel.completedTaskCount.observe(viewLifecycleOwner) { completedTaskCount ->
            val completedTaskCountText =
                resources.getString(R.string.task_complete_count) + completedTaskCount
            binding.completedTodoTextView.text = completedTaskCountText
        }

        todoViewModel.networkResult.observe(viewLifecycleOwner) { networkResult ->
            if (networkResult != null) {
                if (networkResult is NetworkResult.Error) Snackbar.make(
                    binding.root,
                    "Invalid server",
                    Snackbar.LENGTH_LONG
                ).show()
                if (networkResult is NetworkResult.Exception) Snackbar.make(
                    binding.root,
                    "Invalid client",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun initListeners() {
        binding.addButton.setOnClickListener {
            binding.root.findNavController()
                .navigate(FragmentMainDirections.actionMainToDoFragmentToNewToDoFragment())
        }
        binding.hideOrShowComTaskButton.setOnClickListener {
            hideOrShowBtOnClick()
        }

        hideOrShowRefreshButton()
    }

    private fun hideOrShowRefreshButton() {
        if ((requireActivity().application as ToDoApplication).isInternetAvailable()) {
            binding.refreshButton.visibility = View.VISIBLE
            binding.noInternetButton.visibility = View.GONE
            binding.refreshButton.setOnClickListener {
                todoViewModel.updateDataFromServer()
            }
        } else {
            binding.refreshButton.visibility = View.GONE
            binding.noInternetButton.visibility = View.VISIBLE
            binding.noInternetButton.setOnClickListener {
                Snackbar.make(
                    requireContext(),
                    binding.root,
                    getString(R.string.error400),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun initSwipeCallbacks() {
        val swipeCallback: ItemTouchHelper.Callback = object : ItemTouchHelper.Callback() {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun isLongPressDragEnabled(): Boolean {
                return false
            }

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val position = viewHolder.bindingAdapterPosition
                val item = mainAdapter.currentList[position]

                val swipeFlags = if (item.done) ItemTouchHelper.LEFT else
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT

                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                return makeMovementFlags(dragFlags, swipeFlags)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val item = mainAdapter.currentList[position]
                val cl = item.color
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        todoViewModel.deleteToDo(item)
                        Snackbar.make(
                            binding.root,
                            getString(R.string.remove_changes),
                            Snackbar.LENGTH_LONG
                        )
                            .setAction(getString(R.string.back)) {
                                todoViewModel.updateToDo(item.apply { color = cl })
                            }
                            .show()
                    }

                    ItemTouchHelper.RIGHT -> {
                        item.done = true
                        item.changed_at += 1
                        todoViewModel.updateToDo(item)
                        mainAdapter.notifyItemChanged(position)
                    }
                }
            }

            override fun isItemViewSwipeEnabled(): Boolean {
                return true
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {

                RecyclerViewSwipeDecorator.Builder(
                    c, recyclerView, viewHolder,
                    dX, dY, actionState, isCurrentlyActive
                )
                    .addSwipeLeftBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.color_light_red
                        )
                    )
                    .addSwipeLeftActionIcon(R.drawable.ic_delete)
                    .addSwipeRightBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.color_light_green
                        )
                    )
                    .addSwipeRightActionIcon(R.drawable.ic_complete_swipe)
                    .addCornerRadius(TypedValue.COMPLEX_UNIT_SP, 2)
                    .create()
                    .decorate()

                super.onChildDraw(
                    c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
                )
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}