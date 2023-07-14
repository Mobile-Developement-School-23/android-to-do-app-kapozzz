package com.example.todoapp.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import javax.inject.Inject

class FragmentMainLayout @Inject constructor(private val utils: MainFragmentUIElements, private val list: ShowToDoList) {

    @Composable
    fun SetUI() {
        Surface() {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {

                Column(Modifier.fillMaxSize()) {
                    Box(
                        Modifier
                            .padding(top = 82.dp)
                            .padding(start = 61.dp)
                    ) {

                        Row() {
                            Box(contentAlignment = Alignment.BottomStart) {
                                Column() {
                                    with(utils) {
                                        MyDealsText()
                                        CompletedTaskCounter()
                                    }
                                }
                            }

                            Box(Modifier.fillMaxWidth().padding(end = 8.dp) ,contentAlignment = Alignment.BottomEnd) {
                                Row {
                                    utils.RefreshButton(modifier = Modifier)

                                    utils.ShowHideEyeButton(
                                        modifier = Modifier
                                    )
                                }
                            }
                        }
                    }

                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(top = 12.dp)
                    ) {

                        list.Show(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp, end = 8.dp)
                                .offset(y = 10.dp))

                        utils.AddButton (
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 8.dp, bottom = 8.dp)
                        )
                    }
                }
            }

        }
    }
}