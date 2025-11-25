package com.atri.seduley.ui.screen.setting

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
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
import com.atri.seduley.ui.screen.setting.components.NestScroll
import com.atri.seduley.ui.viewmodel.SettingViewModel
import kotlinx.coroutines.flow.collectLatest

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingViewModel = hiltViewModel()
) {
    // 从 ViewModel 中收集响应式状态
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val studentId by viewModel.studentId.collectAsState()
    val systemConf by viewModel.systemConf.collectAsState()

    LaunchedEffect(viewModel) {
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
        Box {
            NestScroll(
                studentId = studentId,
                systemConf = systemConf,
                onEvent = viewModel::onEvent
            )

            if (uiState is SettingUiState.Loading) {
                LoadingDialog((uiState as SettingUiState.Loading).message)
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