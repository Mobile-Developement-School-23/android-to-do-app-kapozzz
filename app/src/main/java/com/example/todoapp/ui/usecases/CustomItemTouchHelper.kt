package com.example.todoapp.ui.usecases

import android.content.Context
import android.graphics.Canvas
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.viewmodel.ToDoViewModel
import com.google.android.material.snackbar.Snackbar
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class CustomItemTouchHelper(val context: Context, val adapter: ToDoAdapter, val viewModel: ToDoViewModel, val itemView: View) {
    fun getTouchHelper(): ItemTouchHelper {
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
                val item = adapter.currentList[position]

                val swipeFlags = if (item.done) ItemTouchHelper.LEFT else
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT

                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                return makeMovementFlags(dragFlags, swipeFlags)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val item = adapter.currentList[position]
                val cl = item.color
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        viewModel.deleteToDo(item)
                        Snackbar.make(
                            itemView,
                            context.getString(R.string.remove_changes),
                            Snackbar.LENGTH_LONG
                        )
                            .setAction(context.getString(R.string.back)) {
                                viewModel.updateToDo(item.apply { color = cl })
                            }
                            .show()
                    }

                    ItemTouchHelper.RIGHT -> {
                        item.done = true
                        item.changed_at += 1
                        viewModel.updateToDo(item)
                        adapter.notifyItemChanged(position)
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
                            context,
                            R.color.color_light_red
                        )
                    )
                    .addSwipeLeftActionIcon(R.drawable.ic_delete)
                    .addSwipeRightBackgroundColor(
                        ContextCompat.getColor(
                            context,
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

        return ItemTouchHelper(swipeCallback)

    }
}
