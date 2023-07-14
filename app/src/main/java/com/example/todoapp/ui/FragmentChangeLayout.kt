package com.example.todoapp.ui

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import javax.inject.Inject

class FragmentChangeLayout @Inject constructor(
    private val activity: Activity,
    private val utils: ChangeFragmentUIElements
) {

    @Composable
    fun SetUI() {

        Surface(modifier = Modifier.fillMaxSize()) {

            Column {

                with(utils) {

                    CloseAndSaveButton(modifier = Modifier.padding(start = 8.dp, end = 8.dp))

                    EditTextField(modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 40.dp))

                    ShowImportance(modifier = Modifier.padding(top = 20.dp, start = 8.dp))

                    Row {
                        ShowDeadline()
                        SwitchDatePicker(activity = activity)
                    }
                }
            }

        }

    }

}