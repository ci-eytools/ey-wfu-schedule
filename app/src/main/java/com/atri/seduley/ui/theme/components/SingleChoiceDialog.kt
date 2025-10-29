package com.atri.seduley.ui.theme.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

/**
 * 单选选择框（支持任意数量选项）
 *
 * @param title 弹窗标题
 * @param options 选项列表
 * @param selectedIndex 当前选中项索引
 * @param showDialog 是否显示弹窗
 * @param onDismiss 关闭弹窗时调用
 * @param onConfirm 确认选择时返回选中的索引
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleChoiceDialog(
    title: String,
    text: String? = null,
    options: List<String>,
    selectedIndex: Int = -1,
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    if (showDialog) {
        var currentSelection by remember { mutableIntStateOf(selectedIndex) }

        BasicAlertDialog(
            onDismissRequest = { onDismiss() },
            properties = DialogProperties(),
            content = {
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp)
                ) {
                    // 标题
                    Text(
                        text = title,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.titleLarge
                    )

                    if (!text.isNullOrEmpty()) {
                        Text(
                            text = text,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }

                    // 选项列表
                    options.forEachIndexed { index, option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { currentSelection = index }
                                .padding(vertical = 8.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentSelection == index,
                                onClick = { currentSelection = index }
                            )
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 底部按钮
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(onClick = {
                            onConfirm(currentSelection)
                            onDismiss()
                        }) {
                            Text("确认")
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(onClick = { onDismiss() }) {
                            Text("取消")
                        }
                    }
                }
            }
        )
    }
}
