package com.example.todoapp.ui

import android.app.Activity
import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.navigation.NavController
import com.example.todoapp.R
import com.example.todoapp.ui.model.Importance
import com.example.todoapp.ui.viewmodel.ToDoViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class ChangeFragmentUIElements @Inject constructor(
    private val viewModel: ToDoViewModel,
    private val navController: NavController
) {

    @Composable
    fun CloseAndSaveButton(modifier: Modifier = Modifier) {

        val currentItem = viewModel.currentItem.observeAsState()

        Box(modifier = modifier.fillMaxWidth()) {

            // close button
            IconButton(modifier = Modifier.align(Alignment.TopStart), onClick = {
                navController.popBackStack()
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = null
                )
            }

            // save button
            if (currentItem.value?.done != true) TextButton(
                modifier = Modifier.align(Alignment.TopEnd),
                onClick = {
                    viewModel.saveOrUpdateItem(
                        currentItem.value ?: throw IllegalStateException("invalid current item")
                    )
                }) {
                Text(
                    text = stringResource(id = R.string.save_button_text),
                    color = Color.Blue,
                    fontSize = 22.sp
                )
            }
            else {
                Text(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 10.dp),
                    text = stringResource(id = R.string.task_complete),
                    color = Color.Green,
                    fontSize = 22.sp
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun EditTextField(modifier: Modifier = Modifier) {

        Card(
            modifier = modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {

                val currentText = remember {
                    mutableStateOf("")
                }

                TextField(
                    value = currentText.value,
                    onValueChange = {
                        currentText.value = it
                    },
                    readOnly = viewModel.currentItem.value?.done ?: false,
                )
            }
        }

    }

    @Composable
    fun ShowImportance(modifier: Modifier = Modifier) {

        Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {

            Text(
                text = stringResource(id = R.string.importance_text),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            val show = remember {
                mutableStateOf(false)
            }

            val currentItem = viewModel.currentItem.observeAsState()

            TextButton(
                enabled = !(currentItem.value?.done ?: false),
                onClick = { show.value = true }) {

                Text(
                    text = stringResource(
                        id = if (currentItem.value?.importance != null) when (currentItem.value?.importance
                            ?: Importance.BASIC) {
                            Importance.IMPORTANT -> R.string.urgent
                            else -> R.string.low
                        } else R.string.no
                    )
                )

            }

            if (show.value) {
                Popup(
                    onDismissRequest = {
                        show.value = false
                    },
                    offset = IntOffset(400, 0)
                ) {
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextButton(
                                enabled = !(currentItem.value?.done ?: true),
                                onClick = {
                                    show.value = false
                                    viewModel.currentItem.value?.importance = Importance.BASIC
                                }) {

                                Text(
                                    text = stringResource(id = R.string.no)
                                )
                            }

                            Spacer(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(40.dp)
                                    .background(Color.White)
                            )

                            TextButton(
                                enabled = !(currentItem.value?.done ?: true),
                                onClick = {
                                    show.value = false
                                    viewModel.currentItem.value?.importance = Importance.LOW
                                }) {

                                Text(
                                    text = stringResource(id = R.string.low)
                                )

                            }

                            Spacer(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(40.dp)
                                    .background(Color.White)
                            )

                            TextButton(
                                enabled = !(currentItem.value?.done ?: true),
                                onClick = {
                                    show.value = false
                                    viewModel.currentItem.value?.importance = Importance.IMPORTANT
                                }) {

                                Text(
                                    text = stringResource(id = R.string.urgent)
                                )
                            }
                        }
                    }

                }

            }
        }
    }

    @Composable
    fun SwitchDatePicker(activity: Activity) {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale("ru", "RU"))

        val currentItem = viewModel.currentItem.observeAsState()

        val datePicker = DatePickerDialog(activity)

        datePicker.setOnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            val cal = Calendar.getInstance()
            cal.set(year, monthOfYear, dayOfMonth)
            val date = cal.time

            val formattedDate = dateFormat.format(date)

            currentItem.value?.deadline = date
        }

        Switch(checked = false, onCheckedChange = {
            if (it) {
                val cal = Calendar.getInstance()
                val day = cal.get(Calendar.DAY_OF_MONTH)
                val month = cal.get(Calendar.MONTH)
                val year = cal.get(Calendar.YEAR)
                datePicker.show()
                datePicker.updateDate(year, month, day)
            } else {
                currentItem.value?.deadline = null
            }
        })
    }

    @Composable
    fun ShowDeadline() {

        val currentItem = viewModel.currentItem.observeAsState()

        Column(modifier = Modifier.padding(start = 8.dp)) {
            if (currentItem.value?.deadline != null) {
                Text(
                    text = stringResource(id = R.string.deadline_date), fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "${currentItem.value?.deadline}",
                    color = Color.Blue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            if (currentItem.value?.created_at != null) {
                Text(
                    text = stringResource(id = R.string.creation_date),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(text = "${currentItem.value?.changed_at}", color = Color.Blue)
            }

        }
    }

    @Composable
    fun DeleteButton() {
        Row {
            Text(text = stringResource(id = R.string.delete))
            Icon(
                painter = painterResource(id = R.drawable.ic_delete),
                contentDescription = null,
                tint = Color.Blue
            )
        }
    }
}