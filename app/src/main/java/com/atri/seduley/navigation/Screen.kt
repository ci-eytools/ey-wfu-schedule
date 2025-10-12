package com.atri.seduley.navigation

sealed class Screen(val route: String) {
    object DailySchedule : Screen("schedule/daily")
    object Settings : Screen("settings")
}