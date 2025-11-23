package com.atri.seduley.ui.screen.schedule

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.atri.seduley.ui.navigation.Screen
import com.atri.seduley.ui.screen.schedule.components.DailyScheduleContent
import com.atri.seduley.ui.screen.schedule.components.DailyScheduleTopBar
import com.atri.seduley.ui.screen.schedule.components.InfoText
import com.atri.seduley.ui.screen.schedule.components.ToTodayButton
import com.atri.seduley.ui.viewmodel.ScheduleViewModel
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun DailyScheduleScreen(
    navController: NavController,
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val success = uiState as? ScheduleUiState.Success

    val selectedDate = success?.selectedDate ?: viewModel.dateCache.selectedDate
    val startDate = viewModel.dateCache.startDate
    val endDate = viewModel.dateCache.endDate

    Scaffold(
        topBar = {
            DailyScheduleTopBar(
                selectedDate = selectedDate,
                startDate = startDate,
                endDate = endDate,
                onDayOfWeekSelect = { viewModel.onEvent(ScheduleEvent.SwitchDate(it)) },
                onSwitchWeek = { viewModel.onEvent(ScheduleEvent.SwitchWeek(it)) }
            )
        },

        floatingActionButton = {
            if (navController.currentDestination?.route == Screen.DailySchedule.route) {
                ToTodayButton(
                    selectedDate = selectedDate,
                    onClick = { viewModel.onEvent(ScheduleEvent.SwitchDate(LocalDate.now())) }
                )
            }
        }
    ) { innerPadding ->

        when (uiState) {

            is ScheduleUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is ScheduleUiState.Success -> {
                DailyScheduleContent(
                    selectedDate = success!!.selectedDate,
                    courses = success.courses,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            is ScheduleUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    InfoText(
                        selectedDate = selectedDate,
                        text = (uiState as ScheduleUiState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
