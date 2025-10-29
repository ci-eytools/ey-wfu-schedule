package com.atri.seduley.feature.setting.presentation.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.spring
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.atri.seduley.feature.setting.domain.entity.SystemConfiguration
import com.atri.seduley.feature.setting.presentation.SettingEvent
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun NestScroll(
    studentId: String,
    systemConfiguration: SystemConfiguration,
    externalResetTrigger: Int,
    onEvent: (SettingEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val maxAspectRatio = 16f / 16f
    val minAspectRatio = 16f / 9f

    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.roundToPx() }
    val maxHeight = (screenWidthPx / maxAspectRatio)  // px
    val minHeight = (screenWidthPx / minAspectRatio)  // px

    val scrollableState = remember { CoverScrollableState(minHeight.toInt(), maxHeight.toInt()) }
    ScrollableDefaults.flingBehavior()

    // 计算当前比例：1 表示最大高度，0 表示最小高度
    val scrollFraction = (scrollableState.height - minHeight) / (maxHeight - minHeight)
    val currentAspectRatio = minAspectRatio + (maxAspectRatio - minAspectRatio) * scrollFraction

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .nestedScroll(
                remember {
                    object : NestedScrollConnection {
                        override fun onPreScroll(
                            available: Offset,
                            source: NestedScrollSource
                        ): Offset {
                            val availableY = available.y
                            if (source == NestedScrollSource.UserInput && availableY < 0) {
                                return Offset(0f, scrollableState.dispatchRawDelta(availableY))
                            }
                            return Offset.Zero
                        }

                        override fun onPostScroll(
                            consumed: Offset,
                            available: Offset,
                            source: NestedScrollSource
                        ): Offset {
                            val availableY = available.y
                            if (source == NestedScrollSource.UserInput && availableY > 0) {
                                return Offset(0f, scrollableState.dispatchRawDelta(availableY))
                            }
                            return Offset.Zero
                        }

                        override suspend fun onPreFling(available: Velocity): Velocity {
                            // 无论往上还是往下, 最后都回到 minHeight
                            scrollableState.flingBack()
                            return Velocity.Zero
                        }
                    }
                }
            )
    ) {
        // 图片区域, 使用动态 aspectRatio
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(currentAspectRatio) // 动态宽高比
        ) {
            FlowBackground(
                externalResetTrigger = externalResetTrigger,
                modifier = Modifier.matchParentSize()
            )
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .offset {
                    IntOffset(
                        x = 0,
                        y = scrollableState.height
                    )
                }
                .clip(RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp))
                .background(MaterialTheme.colorScheme.background)
        )
        // 列表跟随图片底部
        SettingList(
            modifier = Modifier
                .fillMaxSize()
                .offset {
                    IntOffset(
                        x = 0,
                        y = scrollableState.height + with(density) { -16.dp.toPx() }.toInt()
                    )
                }
                .clip(RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)),
            studentId = studentId,
            systemConfiguration = systemConfiguration,
            onEvent = { onEvent(it) },
        )
    }
}


class CoverScrollableState(
    val minValue: Int,
    val maxValue: Int
) : ScrollableState {

    var height by mutableIntStateOf(minValue) // 默认 16:9
        private set
    private var deferredConsumption = 0f

    private val scrollableState = ScrollableState { value ->
        val consume = if (value < 0) {
            // 上滑
            max(minValue.toFloat() - height, value)
        } else {
            // 下拉
            min(maxValue.toFloat() - height, value)
        }

        val currentConsume = consume + deferredConsumption
        val consumeInt = currentConsume.roundToInt()
        deferredConsumption = currentConsume - consumeInt
        height += consumeInt

        consume
    }

    override val isScrollInProgress: Boolean
        get() = scrollableState.isScrollInProgress

    override fun dispatchRawDelta(delta: Float): Float =
        scrollableState.dispatchRawDelta(delta)

    override suspend fun scroll(
        scrollPriority: MutatePriority,
        block: suspend ScrollScope.() -> Unit
    ) = scrollableState.scroll(scrollPriority, block)

    suspend fun flingBack() {
        // 松手后回到 minValue
        scroll(MutatePriority.Default) {
            val start = height
            val target = minValue
            val anim = androidx.compose.animation.core.Animatable(start.toFloat())
            anim.animateTo(
                targetValue = target.toFloat(),
                animationSpec = spring(dampingRatio = 0.6f, stiffness = 200f)
            ) {
                height = value.toInt()
            }
        }
    }
}

@Preview
@Composable
fun NestScrollPreview() {
//    NestScroll()
}
