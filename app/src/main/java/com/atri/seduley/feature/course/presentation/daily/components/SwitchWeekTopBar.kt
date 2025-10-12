package com.atri.seduley.feature.course.presentation.daily.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.threeten.bp.LocalDate

@Composable
fun SwitchWeekTopBar(
    startDate: LocalDate,
    endDate: LocalDate,
    selectedDate: LocalDate,
    onClick: (SwitchWeekWay) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .scale(0.8f),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        OutlinedButton(
            onClick = { onClick(SwitchWeekWay.PREVIOUS) },
            shape = RoundedCornerShape(30),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.secondary
            ),
            enabled = selectedDate.isAfter(startDate)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Previous"
            )
        }
        OutlinedButton(
            onClick = { onClick(SwitchWeekWay.NEXT) },
            shape = RoundedCornerShape(30),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.secondary
            ),
            enabled = selectedDate.isBefore(endDate.plusWeeks(-1))
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Next"
            )
        }
    }
}

@Composable
fun ToTodayButton(
    selectedDate: LocalDate,
    onClick: (SwitchWeekWay) -> Unit,
    modifier: Modifier = Modifier
) {
    if (selectedDate != LocalDate.now()) {
        OutlinedButton(
            modifier = modifier
                .padding(end = 16.dp, bottom = 36.dp),
            onClick = { onClick(SwitchWeekWay.NOW) },
            shape = RoundedCornerShape(50),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.onPrimary),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("今")
        }
    }
}

enum class SwitchWeekWay(val offset: Long) {
    PREVIOUS(-7),
    NOW(0),
    NEXT(7)
}