package com.atri.seduley.feature.setting.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.atri.seduley.feature.setting.presentation.components.NestScroll
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // 收集一次性事件
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is SettingUiEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(event.message)
                }

                SettingUiEvent.NavigateBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = { }
    ) { _ ->
        when (uiState) {
            is SettingUiState.Loading -> {
                Box {
                    LoadingDialog()
                    NestScroll(
                        externalResetTrigger = viewModel.externalResetTrigger,
                        studentId = viewModel.studentId,
                        onEvent = { viewModel.onEvent(it) }
                    )
                }
            }

            is SettingUiState.Success -> {
                val studentId = (uiState as SettingUiState.Success).studentId
                NestScroll(
                    externalResetTrigger = viewModel.externalResetTrigger,
                    studentId = studentId,
                    onEvent = { viewModel.onEvent(it) }
                )
            }

            SettingUiState.Idle -> {
                NestScroll(
                    externalResetTrigger = viewModel.externalResetTrigger,
                    studentId = viewModel.studentId,
                    onEvent = { viewModel.onEvent(it) }
                )
            }
        }
    }
}

@Composable
fun LoadingDialog(
    text: String = "加载中, 请勿关闭软件"
) {
    Dialog(onDismissRequest = { /* 禁止关闭 */ }) {
        Box(
            modifier = Modifier
                .size(180.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = text,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

