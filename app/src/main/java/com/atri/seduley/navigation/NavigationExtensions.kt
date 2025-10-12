package com.atri.seduley.navigation

import androidx.navigation.NavController

fun NavController.safeNavigate(route: String) {
    if (this.currentDestination?.route != route) {
        this.navigate(route) {
            launchSingleTop = true
            popUpTo(this@safeNavigate.graph.startDestinationId) { saveState = true }
            restoreState = true
        }
    }
}