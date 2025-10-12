package com.atri.seduley.feature.course.presentation.daily.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atri.seduley.R
import org.threeten.bp.LocalDate

@Composable
fun NowTimeTopBar(
    selected: LocalDate,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(top = 16.dp)
    ) {
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.Center
        ) {
            val now = LocalDate.now()
            val month = now.month.name.substring(0, 1) +
                    now.month.name.substring(1).lowercase()
            Text(
                text = "${now.dayOfMonth}  $month",
                modifier = Modifier
                    .padding(start = 16.dp),
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Light
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = if (selected == now) "Today" else selected.toString(),
                modifier = Modifier
                    .padding(start = 16.dp),
                style = MaterialTheme.typography.titleLarge,
                fontSize = 36.sp,
                fontFamily = FontFamily(Font(R.font.playfairdisplay_variablefont_wght))
            )
        }
    }
}

@Preview
@Composable
fun NowTimeTopBarPreview() {
    NowTimeTopBar(
        selected = LocalDate.of(2025, 9, 8)
    )
}