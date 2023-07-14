package com.example.todoapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.todoapp.R
import com.example.todoapp.ui.viewmodel.ToDoViewModel
import javax.inject.Inject

class MainFragmentUIElements @Inject constructor(
    private val viewModel: ToDoViewModel,
    private val navController: NavController
) {

    @Composable
    fun AddButton(modifier: Modifier = Modifier) {
        FloatingActionButton(
            modifier = modifier, onClick = {
                viewModel.currentItem.value = null
                navController.navigate(FragmentMainDirections.actionMainToDoFragmentToChangeToDoFragment())
            },
            containerColor = Color.Blue
        ) {
            Icon(painter = painterResource(id = R.drawable.ic_fab_add), contentDescription = null, tint = Color.White)
        }
    }

    @Composable
    fun RefreshButton(modifier: Modifier) {

        val internetStatus = viewModel.internetStatus.observeAsState()

        val loadStatus = viewModel.progressBarIsActive.observeAsState()

        IconButton(
            modifier = modifier,
            onClick = {
                if (internetStatus.value == true) viewModel.refreshList() else {
                    // Snackbar
                }
            }
        ) {
            if (loadStatus.value != true) {
                Icon(
                    painter = painterResource(
                        id = if (internetStatus.value == true
                        ) R.drawable.ic_refresh_data else R.drawable.ic_internet_not_available
                    ), contentDescription = null,
                    tint = colorResource(
                        id = if (internetStatus.value == true
                        ) R.color.blue else R.color.red
                    )
                )
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.size(30.dp),
                    color = colorResource(id = R.color.blue),
                    strokeWidth = 3.dp
                )
            }

        }
    }

    @Composable
    fun MakeNotification() {

    }

    @Composable
    fun ShowHideEyeButton(modifier: Modifier = Modifier) {

        val show = viewModel.showCompletedTasks.observeAsState()

        IconButton(
            modifier = modifier.clickable {
                viewModel.setShowCompletedTasksState(!(show.value ?: true))
            }, onClick = {
                viewModel.setShowCompletedTasksState(!(show.value ?: true))
            }) {
            Icon(
                painter = painterResource(id = if (show.value != false) R.drawable.ic_show_completed_tasks else R.drawable.ic_hide_completed_tasks),
                contentDescription = null,
                tint = colorResource(id = R.color.blue)
            )
        }
    }

    @Composable
    fun CompletedTaskCounter(modifier: Modifier = Modifier) {
        val count = viewModel.completedTaskCount.observeAsState()
        Text(text = "Completed - ${count.value}", fontSize = 16.sp)
    }

    @Composable
    fun MyDealsText(modifier: Modifier = Modifier) {
        Text(text = "My deals", fontSize = 32.sp, fontWeight = FontWeight.Bold)
    }

}