package com.atri.seduley.navigation

/**
 * 路由节点
 */
sealed class Screen(val route: String) {
    object DailySchedule : Screen("schedule/daily")
    object Settings : Screen("settings")
}