package com.atri.seduley.ui.theme.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

/**
 * 通用列表弹窗（纯展示容器，无状态、无选中逻辑）
 *
 * 用法：直接传入多个 ListItem 或任意 Composable 即可
 * 不使用 LazyColumn, 改用 verticalScroll() 支持灵活布局
 *
 * @param title 弹窗标题（可选）
 * @param showDialog 是否显示弹窗
 * @param onDismiss 关闭弹窗回调
 * @param itemContent 传入多个 Composable 列表项
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDialog(
    title: String? = null,
    showDialog: Boolean,
    onDismiss: () -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    itemContent: @Composable () -> Unit
) {
    if (!showDialog) return

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(),
        content = {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
            ) {
                // 标题
                if (!title.isNullOrEmpty()) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // 可滚动区域
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    itemContent()
                }
            }
        }
    )
}