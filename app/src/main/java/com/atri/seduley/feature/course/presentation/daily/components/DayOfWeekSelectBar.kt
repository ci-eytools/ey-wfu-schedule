package com.atri.seduley.feature.course.presentation.daily.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate

@Composable
fun DayOfWeekSelectBar(
    onClick: (LocalDate) -> Unit,
    selected: LocalDate,
    modifier: Modifier = Modifier
) {
    val dayMap = mapOf(
        1 to "Mon",
        2 to "Tue",
        3 to "Wed",
        4 to "Thu",
        5 to "Fri",
        6 to "Sat",
        7 to "Sun"
    )

    val now = LocalDate.now()
    val selectDayOfWeek = selected.dayOfWeek.value
    val weekStart = selected.with(DayOfWeek.MONDAY)

    Row(
        modifier = modifier
            .fillMaxWidth()
    ) {
        repeat(7) { index ->

            val date = weekStart.plusDays(index.toLong())
            val isSelected = selectDayOfWeek == date.dayOfWeek.value
            val isToday = now == date

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = { onClick(date) }),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = dayMap[date.dayOfWeek.value] ?: "Unknow",
                    modifier = Modifier,
                    color = if (selectDayOfWeek == index + 1)
                        MaterialTheme.colorScheme.primary else Color.Gray,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Light
                )
                Text(
                    text = date.dayOfMonth.toString(),
                    modifier = Modifier,
                    color = if (selectDayOfWeek == index + 1)
                        MaterialTheme.colorScheme.primary else Color.Black,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                HorizontalDivider(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .fillMaxWidth(0.5f),
                    thickness = 3.dp,
                    color = when {
                        isSelected -> MaterialTheme.colorScheme.primary
                        isToday -> Color.Gray
                        else -> Color.Transparent
                    }
                )
            }
        }
    }
}


@Preview
@Composable
fun DayOfWeekSelectBarPreview() {
    DayOfWeekSelectBar(
        onClick = {},
        selected = LocalDate.of(2025, 9, 9)
    )
}