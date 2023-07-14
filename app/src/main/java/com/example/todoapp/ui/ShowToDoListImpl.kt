package com.example.todoapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.todoapp.R
import com.example.todoapp.ui.model.Importance
import com.example.todoapp.ui.model.TodoItem
import com.example.todoapp.ui.viewmodel.ToDoViewModel
import javax.inject.Inject

class ShowToDoListImpl @Inject constructor(
    private val viewModel: ToDoViewModel, private val navController: NavController
) : ShowToDoList {

    @Composable
    override fun Show(modifier: Modifier) {
        ToDoListLayout(modifier = modifier)
    }

    private fun onClickItemCallback(item: TodoItem) {
        viewModel.currentItem.value = item
        navController.navigate(
            FragmentMainDirections.actionMainToDoFragmentToChangeToDoFragment()
        )
    }

    @Composable
    private fun ToDoListLayout(
        modifier: Modifier = Modifier,
    ) {
        val list = viewModel.allToDoItems.observeAsState()

        Card(modifier = modifier, elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)) {
            val showCompletedTasks = viewModel.showCompletedTasks.observeAsState()
            LazyColumn(modifier = Modifier) {
                items(list.value ?: emptyList()) {
                    if (showCompletedTasks.value == false && it.done) return@items
                    ToDoItem(itemToShow = it)
                }
            }
        }
    }

    @Composable
    private fun ToDoItem(itemToShow: TodoItem) {

        val isDone = remember {
            mutableStateOf(itemToShow.done)
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(all = 8.dp)
                .clickable {
                    onClickItemCallback(itemToShow)
                },
            verticalAlignment = Alignment.CenterVertically) {

            Checkbox(modifier = Modifier.padding(end = 4.dp),
                checked = isDone.value,
                colors = checkboxColors(importance = itemToShow.importance),
                onCheckedChange = {
                    isDone.value = it
                    viewModel.updateToDo(itemToShow.apply {
                        itemToShow.done = isDone.value
                        viewModel.updateToDo(itemToShow)
                    })
                })

            if (itemToShow.importance != Importance.BASIC && !isDone.value) {
                Image(
                    modifier = Modifier.padding(end = 4.dp),
                    painter = painterResource(
                        id = when (itemToShow.importance) {
                            Importance.IMPORTANT -> R.drawable.ic_urgent_importance
                            Importance.LOW -> R.drawable.ic_low_importance
                            else -> throw IllegalStateException("invalid importance")
                        }
                    ),
                    contentDescription = null,
                )
            }

            Column(modifier = Modifier) {
                Text(
                    modifier = Modifier.padding(bottom = 4.dp),
                    text = itemToShow.text,
                    maxLines = 3,
                    fontSize = 18.sp,
                    style = TextStyle(textDecoration = if (itemToShow.done) TextDecoration.LineThrough else TextDecoration.None)
                )
                if (itemToShow.deadline != null && !isDone.value) Text(
                    text = stringResource(id = R.string.deadline_date) + itemToShow.deadline,
                    fontSize = 14.sp
                )
            }

        }
    }


    @Composable
    private fun checkboxColors(importance: Importance): CheckboxColors {
        return CheckboxDefaults.colors(
            checkedColor = colorResource(id = R.color.color_light_green),
            uncheckedColor = colorResource(
                id = if (importance != Importance.IMPORTANT) R.color.gray else R.color.red
            )
        )
    }
}