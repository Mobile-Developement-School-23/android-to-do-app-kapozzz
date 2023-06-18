package com.example.todoapp.controllers

import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentMainLayoutBinding
import com.example.todoapp.model.ToDoAdapter
import com.example.todoapp.model.TodoItem
import com.example.todoapp.model.onClickCallbacks
import com.example.todoapp.viewmodel.ViewModel
import com.example.todoapp.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator


class MainToDoFragment : Fragment(), onClickCallbacks {

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
            ViewModelProvider(this, ViewModelFactory(requireActivity().application)).get(
                ViewModel::class.java
            )

        // Настройка recycler view
        initRecyclerView()

        // Следим за изменениями списка заметок в базе данных, и обновляем список на экране при любом изменении
        initObservers()

        // Добавляем слушатели нажатий
        initListeners()

        // Настройка свайпов, удаление/выполнение
        initSwipeCallbacks()

        binding.mainFragmentProgressBar.visibility = View.GONE
        binding.recyclerViewRootCardView.visibility = View.VISIBLE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sp = requireActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val isVariableTrue = sp.getBoolean("myVariable", false)

        if (!isVariableTrue) {
            todoViewModel.generateRandomTodoItems()
            sp.edit().putBoolean("myVariable", true).apply()
        }
    }

    override fun onItemClick(item: TodoItem) {
        binding.root.findNavController()
            .navigate(MainToDoFragmentDirections.actionMainToDoFragmentToChangeToDoFragment(item))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        todoViewModel.tasksState = !mainAdapter.completedTasksIsVisible
    }

    private fun hideOrShowBtOnClick() {
        mainAdapter.completedTasksIsVisible = todoViewModel.tasksState
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
        todoViewModel.tasksState = mainAdapter.completedTasksIsVisible
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
            mainAdapter.submitList(newToDoList?.reversed() ?: emptyList())
        }

        todoViewModel.completedTaskCount.observe(viewLifecycleOwner) { completedTaskCount ->
            val completedTaskCountText =
                resources.getString(R.string.task_complete_count) + completedTaskCount
            binding.completedTodoTextView.text = completedTaskCountText
        }
    }

    private fun initListeners() {
        binding.addButton.setOnClickListener {
            binding.root.findNavController()
                .navigate(MainToDoFragmentDirections.actionMainToDoFragmentToNewToDoFragment())
        }
        hideOrShowBtOnClick()
        binding.hideOrShowComTaskButton.setOnClickListener {
            hideOrShowBtOnClick()
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

            // Если задача выполнена, то запрещаем свайп вправо это задачи
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val position = viewHolder.bindingAdapterPosition
                val item = mainAdapter.currentList[position]

                val swipeFlags = if (item.isCompleted) ItemTouchHelper.LEFT else
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT

                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                return makeMovementFlags(dragFlags, swipeFlags)
            }

            // Обработка свайпов
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val item = mainAdapter.currentList[position]
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        todoViewModel.deleteToDo(item.id.toString())
                        Snackbar.make(
                            binding.root,
                            getString(R.string.remove_changes),
                            Snackbar.LENGTH_LONG
                        )
                            .setAction(getString(R.string.back)) {
                                todoViewModel.insertToDo(item)
                            }
                            .show()
                    }

                    ItemTouchHelper.RIGHT -> {
                        item.isCompleted = true
                        todoViewModel.updateToDo(item)
                        mainAdapter.notifyItemChanged(position)
                    }
                }
            }

            override fun isItemViewSwipeEnabled(): Boolean {
                return true
            }

            // Устанавливаем иконки и цвет для свайпов
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