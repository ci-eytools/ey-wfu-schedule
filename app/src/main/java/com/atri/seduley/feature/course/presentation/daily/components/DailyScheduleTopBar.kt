package com.atri.seduley.feature.course.presentation.daily.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.threeten.bp.LocalDate

@Composable
fun DailyScheduleTopBar(
    startDate: LocalDate,
    endDate: LocalDate,
    selectedDate: LocalDate,
    onDayOfWeekSelect: (LocalDate) -> Unit,
    onSwitchWeek: (SwitchWeekWay) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            NowTimeTopBar(
                selected = selectedDate
            )
            SwitchWeekTopBar(
                onClick = { onSwitchWeek(it) },
                startDate = startDate,
                endDate = endDate,
                selectedDate = selectedDate
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        DayOfWeekSelectBar(
            onClick = { onDayOfWeekSelect(it) },
            selected = selectedDate
        )
    }
}

@Preview
@Composable
fun DailyScheduleTopBarPreview() {
/*    DailyScheduleTopBar(
        selectedDate = LocalDate.of(2025, 9, 8),
        onDayOfWeekSelect = {},
        onSwitchWeek = {}
    )*/
}