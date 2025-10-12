package com.atri.seduley.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.composable
import com.atri.seduley.feature.course.presentation.daily.DailyScheduleScreen
import com.atri.seduley.feature.setting.presentation.SettingsScreen
import com.atri.seduley.ui.theme.components.BottomNavigationBar
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNav() {
    val navController = rememberAnimatedNavController()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { _ ->
        AnimatedNavHost(
            navController = navController,
            startDestination = Screen.DailySchedule.route
        ) {
            composable(
                route = Screen.DailySchedule.route,
                enterTransition = { EnterTransition.None },
                exitTransition = { null },
                popEnterTransition = { EnterTransition.None },
                popExitTransition = { null }
            ) {
                Column(modifier = Modifier.padding(bottom = 77.dp)) {
                    DailyScheduleScreen(navController)
                }
            }

            composable(
                route = Screen.Settings.route,
                enterTransition = {
                    if (initialState.destination.route == Screen.DailySchedule.route) {
                        slideInHorizontally(
                            initialOffsetX = { 1000 },
                            animationSpec = tween(700, easing = LinearOutSlowInEasing)
                        )
                    } else EnterTransition.None
                },
                exitTransition = { null },
                popEnterTransition = { EnterTransition.None },
                popExitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = tween(700, easing = LinearOutSlowInEasing)
                    ) + fadeOut(animationSpec = tween(500))
                }
            ) {
                Column(modifier = Modifier.padding(bottom = 77.dp)) {
                    SettingsScreen(navController)
                }
            }
        }
    }
}