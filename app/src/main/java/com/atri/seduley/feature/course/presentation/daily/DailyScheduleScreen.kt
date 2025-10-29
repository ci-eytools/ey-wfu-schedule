package com.atri.seduley.feature.course.presentation.daily

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.atri.seduley.feature.course.presentation.daily.components.DailyScheduleContent
import com.atri.seduley.feature.course.presentation.daily.components.DailyScheduleTopBar
import com.atri.seduley.feature.course.presentation.daily.components.InfoText
import com.atri.seduley.feature.course.presentation.daily.components.ToTodayButton
import com.atri.seduley.navigation.Screen

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun DailyScheduleScreen(
    navController: NavController,
    viewModel: DailyScheduleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState
    Scaffold(
        topBar = {
            DailyScheduleTopBar(
                selectedDate = (uiState as? DailyScheduleUiState.Success)?.selectedDate
                    ?: viewModel.dateCache.selectedDate,
                onDayOfWeekSelect = { viewModel.onEvent(DailyScheduleEvent.SwitchDate(it)) },
                onSwitchWeek = { viewModel.onEvent(DailyScheduleEvent.SwitchWeek(it)) },
                startDate = viewModel.dateCache.startDate,
                endDate = viewModel.dateCache.endDate
            )
        },
        floatingActionButton = {
            if (navController.currentDestination?.route == Screen.DailySchedule.route) {
                ToTodayButton(
                    selectedDate = (uiState as? DailyScheduleUiState.Success)?.selectedDate
                        ?: viewModel.dateCache.selectedDate,
                    onClick = { viewModel.onEvent(DailyScheduleEvent.SwitchWeek(it)) }
                )
            }
        },
        bottomBar = { }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is DailyScheduleUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                is DailyScheduleUiState.Success -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        DailyScheduleContent(
                            selectedDate = state.selectedDate,
                            courseList = state.courses
                        )
                    }
                }

                is DailyScheduleUiState.Error -> {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        InfoText(
                            selectedDate = viewModel.dateCache.selectedDate,
                            text = state.message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun DailyCoursesScreenPreview() {
//    DailyScheduleScreen()
}
