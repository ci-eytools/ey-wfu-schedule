package com.atri.seduley.ui.theme.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.atri.seduley.navigation.Screen
import com.atri.seduley.navigation.safeNavigate

@Composable
fun BottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 26.dp, end = 26.dp, bottom = 26.dp)
                .clip(shape = RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
        ) {
            NavItem(
                currentRoute = currentRoute,
                route = Screen.DailySchedule.route,
                icon = { Icon(Icons.Outlined.DateRange, contentDescription = "Daily") },
                onClick = {
                    navController.safeNavigate(Screen.DailySchedule.route)
                },
                modifier = Modifier.weight(1f)
            )
            NavItem(
                currentRoute = currentRoute,
                route = Screen.Settings.route,
                icon = { Icon(Icons.Outlined.Settings, contentDescription = "Settings") },
                onClick = { navController.safeNavigate(Screen.Settings.route) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun NavItem(
    currentRoute: String?,
    route: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cRouteNN: String =
        currentRoute.takeIf { !currentRoute.isNullOrEmpty() } ?: Screen.DailySchedule.route
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier,
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                modifier = Modifier
                    .clickable(
                        onClick = onClick
                    )
                    .fillMaxWidth()
                    .padding(top = 14.dp, bottom = 14.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon()
            }
            SignDivider(
                modifier = Modifier.padding(bottom = 5.dp),
                color = if (route == cRouteNN) MaterialTheme.colorScheme.primary
                else Color.Transparent
            )
        }
    }
}

@Composable
fun SignDivider(
    color: Color,
    modifier: Modifier = Modifier
) {
    @SuppressLint("UnusedBoxWithConstraintsScope")
    BoxWithConstraints(
        modifier = modifier
            .height(3.dp)
            .fillMaxWidth(0.2f)
            .clip(RoundedCornerShape(50))
            .background(Color.Transparent)
    ) {
        val targetFraction = if (color != Color.Transparent) 1f else 0f

        val animatedFraction by animateFloatAsState(
            targetValue = targetFraction,
            animationSpec = tween(
                durationMillis = 300,
                easing = { fraction -> fraction * fraction * (3 - 2 * fraction) } // easeInOut
            )
        )

        Box(
            modifier = Modifier
                .height(3.dp)
                .fillMaxWidth(animatedFraction)
                .background(color)
                .align(Alignment.Center)
        )
    }
}

@Preview(heightDp = 600, widthDp = 300, showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
//    BottomNavigationBar()
}