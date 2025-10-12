package com.atri.seduley.feature.course.presentation.daily.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atri.seduley.R
import com.atri.seduley.feature.course.domain.entity.dto.CourseDetail
import com.atri.seduley.feature.course.presentation.daily.util.sectionToTimeStr
import com.atri.seduley.feature.course.presentation.daily.util.timeToSection
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

@Composable
fun DateDisplay(
    section: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        val (startTimeStr, endTimeStr) = sectionToTimeStr(section)
        Text(
            text = startTimeStr,
            modifier = Modifier,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = endTimeStr,
            modifier = Modifier,
            color = Color.Gray,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Light
        )
    }
}

@Composable
fun TimeLine(
    selectedDate: LocalDate,
    section: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        val primaryColor = MaterialTheme.colorScheme.primary

        // 1.画圆动画 0 -> 600
        val sweep = remember { Animatable(0f) }
        LaunchedEffect(selectedDate) {
            sweep.snapTo(0f)
            sweep.animateTo(
                targetValue = 360f,
                animationSpec = tween(600, easing = FastOutSlowInEasing)
            )
        }
        Canvas(
            modifier = Modifier.size(24.dp)
        ) {
            // 外圈（空心圆）
            val strokeWidth = 6f
            val diameter = size.minDimension - strokeWidth  // 给线条留出空间
            val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
            val arcSize = Size(diameter, diameter)
            drawArc(
                color = primaryColor,
                startAngle = -90f,
                sweepAngle = sweep.value,
                useCenter = false,
                style = Stroke(width = strokeWidth),
                topLeft = topLeft,
                size = arcSize
            )

            if (LocalDate.now() == selectedDate &&
                (timeToSection(LocalTime.now()) ?: -1) == section
            ) {
                // 内圈（实心圆）
                drawCircle(
                    color = primaryColor,
                    radius = 16f
                )
            }
        }

        // 2.画线动画 600 -> 1000
        val lineLength = remember { Animatable(0f) }
        LaunchedEffect(selectedDate) {
            lineLength.snapTo(0f)
            lineLength.animateTo(
                targetValue = 1f,
                animationSpec = tween(600, easing = FastOutLinearInEasing)
            )
        }
        Canvas(
            modifier = Modifier
                .fillMaxHeight()
                .width(5.dp)
                .padding(top = 3.dp, bottom = 3.dp)
        ) {
            // 当前能用的总高度
            val fullHeight = size.height

            // 动画值 (0f ~ 1f)
            val progress = lineLength.value

            drawLine(
                color = primaryColor,
                start = Offset(x = size.width / 2, y = 0f),
                end = Offset(x = size.width / 2, y = fullHeight * progress),
                strokeWidth = 4f
            )
        }
    }
}

@Composable
fun MaskedSlideIn(
    key: Any?,
    modifier: Modifier = Modifier,
    maskHeight: Dp? = null,
    durationMillis: Int = 420,
    content: @Composable () -> Unit
) {
    BoxWithConstraints(
        modifier = if (maskHeight != null) modifier.height(maskHeight) else modifier
    ) {
        // 可见区宽度（Dp -> Px）
        val density = LocalDensity.current
        val maskWidthPx by rememberUpdatedState(newValue = with(density) { maxWidth.toPx() })

        val targetAlpha = remember { Animatable(0f) }
        val offsetX = remember { Animatable(-maskWidthPx) }

        LaunchedEffect(key) {
            offsetX.snapTo(-maskWidthPx)
            offsetX.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis, easing = FastOutSlowInEasing)
            )
        }
        LaunchedEffect(key) {
            targetAlpha.snapTo(0f)
            targetAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis, easing = FastOutSlowInEasing)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(0.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(targetAlpha.value)
                    .graphicsLayer {
                        translationX = offsetX.value
                    }
            ) {
                content()
            }
        }
    }
}

@Composable
fun DetailCard(
    selectedDate: LocalDate,
    courseDetail: CourseDetail,
    modifier: Modifier = Modifier
) {
    MaskedSlideIn(
        key = selectedDate to courseDetail.section to courseDetail.courseName,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, end = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            val nowSection = timeToSection(LocalTime.now()) ?: -1
            Card(
                modifier = Modifier
                    .fillMaxSize(),
                colors = CardDefaults
                    .cardColors(
                        if (
                            selectedDate == LocalDate.now()
                            && nowSection == courseDetail.section
                        )
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.secondary
                    )
            ) {
                Text(
                    text = courseDetail.courseName,
                    modifier = Modifier
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.simyou)),
                    fontSize = 18.sp
                )
                Text(
                    text = courseDetail.location,
                    modifier = Modifier
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun DailyScheduleItem(
    selectedDate: LocalDate,
    courseDetail: CourseDetail,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        // 3.逐渐浮现时间信息 0 -> 1000
        val targetAlpha = remember { Animatable(0f) }
        LaunchedEffect(selectedDate) {
            targetAlpha.snapTo(0f)
            targetAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(1000)
            )
        }

        DateDisplay(
            section = courseDetail.section,
            modifier = Modifier
                .weight(1f)
                .alpha(targetAlpha.value)
        )
        TimeLine(
            selectedDate = selectedDate,
            section = courseDetail.section,
            modifier = Modifier.weight(1f)
        )
        DetailCard(
            selectedDate = selectedDate,
            courseDetail = courseDetail,
            modifier = Modifier.weight(2f)
        )
    }
}

@Composable
fun DailyScheduleContent(
    selectedDate: LocalDate,
    courseList: List<CourseDetail>,
    modifier: Modifier = Modifier
) {
    if (courseList.isEmpty()) {
        Column(
            modifier = Modifier
                .padding(top = 66.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            InfoText(
                selectedDate = selectedDate,
                text = if (selectedDate == LocalDate.now()) "今日无课, 好好休息吧" else
                    "当日无课",
                color = MaterialTheme.colorScheme.primary
            )
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = 36.dp)
                .verticalScroll(rememberScrollState())
        ) {
            courseList.forEach { courseDetail ->
                DailyScheduleItem(
                    selectedDate = selectedDate,
                    courseDetail = courseDetail,
                    modifier = Modifier
                        .height(224.dp)
                )
            }
        }
    }
}

@Composable
fun InfoText(
    selectedDate: LocalDate,
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {

    val alpha = remember { Animatable(0f) }
    val offsetY = remember { Animatable(20f) }

    LaunchedEffect(selectedDate) {
        alpha.snapTo(0f)
        offsetY.snapTo(20f)
        alpha.animateTo(
            1f,
            animationSpec = tween(500, easing = FastOutSlowInEasing))
        offsetY.animateTo(
            0f,
            animationSpec = tween(500, easing = FastOutSlowInEasing))
    }

    Column(
        modifier = modifier.graphicsLayer {
            this.alpha = alpha.value
            translationY = offsetY.value
        }
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color,
            fontFamily = FontFamily(Font(R.font.simyou))
        )
    }
}

@Preview
@Composable
fun InfoTextPreview() {
/*    InfoText(
        selectedDate = LocalDate.now(),
        text = "√ 今日无课, 好好休息吧~",
        color = MaterialTheme.colorScheme.onPrimary
    )*/
}