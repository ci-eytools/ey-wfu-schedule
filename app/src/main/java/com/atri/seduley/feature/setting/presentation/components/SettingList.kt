package com.atri.seduley.feature.setting.presentation.components

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atri.seduley.R
import com.atri.seduley.feature.setting.presentation.SettingEvent
import com.atri.seduley.feature.setting.presentation.util.Assets
import com.atri.seduley.feature.setting.presentation.util.rememberImageCropper
import com.atri.seduley.ui.theme.components.ConfirmDialog

@Composable
fun SettingList(
    studentId: String?,
    onEvent: (SettingEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = "Settings",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .align(alignment = Alignment.Start)
                .padding(16.dp),
            fontSize = 36.sp,
            fontFamily = FontFamily(Font(R.font.playfairdisplay_variablefont_wght))
        )
        IdentityInfo(
            studentId = studentId,
            updateCredential = { studentId, password ->
                onEvent(
                    SettingEvent.SaveCredential(
                        studentId,
                        password
                    )
                )
            }
        )
        Spacer(modifier = Modifier.height(15.dp))
        CourseInfo(
            clearSchedules = { onEvent(SettingEvent.ClearSchedules) },
            enterSchedules = { onEvent(SettingEvent.EnterSchedules) }
        )
        Spacer(modifier = Modifier.height(15.dp))
        CommonOption(
            resetCover = { onEvent(SettingEvent.ResetCover) },
            updateSplash = { onEvent(SettingEvent.UpdateSplash) },
            resetSplash = { onEvent(SettingEvent.ResetSplash) }
        )
    }
}

@Composable
fun IdentityInfo(
    studentId: String? = null,
    updateCredential: (String, String) -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    var isShowCredentialInputDialog by remember { mutableStateOf(false) }
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        ListItem(
            settingItem = "修改账户信息",
            detail = studentId.takeIf { !it.isNullOrEmpty() } ?: "未设置",
            onClick = { isShowCredentialInputDialog = true }
        )
    }
    CredentialInputDialog(
        currentStudentId = studentId,
        showDialog = isShowCredentialInputDialog,
        onDismiss = {
            isShowCredentialInputDialog = false
        },
        onConfirm = { studentId, password -> updateCredential(studentId, password) }
    )
}

@Composable
fun CourseInfo(
    clearSchedules: () -> Unit,
    enterSchedules: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showClearAllCourseDialog by remember { mutableStateOf(false) }
    var showEnterAllCourseDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        ListItem(
            settingItem = "清空课程",
            onClick = { showClearAllCourseDialog = true },
        )
        ConfirmDialog(
            text = "是否清空课程",
            showDialog = showClearAllCourseDialog,
            onDismiss = { showClearAllCourseDialog = false },
            onConfirm = { clearSchedules() }
        )
        ListItem(
            settingItem = "拉取所有课程",
            onClick = { showEnterAllCourseDialog = true }
        )
        ConfirmDialog(
            text = "是否拉取所有课程",
            showDialog = showEnterAllCourseDialog,
            onDismiss = { showEnterAllCourseDialog = false },
            onConfirm = { enterSchedules() }
        )
    }
}

@Composable
fun CommonOption(
    resetCover: () -> Unit,
    updateSplash: () -> Unit,
    resetSplash: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showResetCoverDialog by remember { mutableStateOf(false) }
    var showResetSplashDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
    ) {
        ListItem(
            settingItem = "重置封面",
            onClick = { showResetCoverDialog = true }
        )
        ConfirmDialog(
            text = "是否重置封面",
            showDialog = showResetCoverDialog,
            onDismiss = { showResetCoverDialog = false },
            onConfirm = { resetCover() }
        )
        val context = LocalContext.current
        val activity = context as Activity
        val startCrop = rememberImageCropper(
            activity = activity,
            imageName = Assets.SPLASH_IMAGE_NAME,
            aspectRatioX = 9f,
            aspectRatioY = 16f,
            onSuccess = { updateSplash() },
            onCancel = { }
        )
        ListItem(
            settingItem = "更新开屏页",
            onClick = { startCrop() }
        )
        ListItem(
            settingItem = "重置开屏页",
            onClick = { showResetSplashDialog = true }
        )
        ConfirmDialog(
            text = "是否重置封面",
            showDialog = showResetSplashDialog,
            onDismiss = { showResetSplashDialog = false },
            onConfirm = { resetSplash() }
        )
    }
}

@Composable
fun ListItem(
    settingItem: String,
    detail: String = "",
    isShowDivider: Boolean = true,
    onClick: () -> Unit = {},
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(top = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = settingItem,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 16.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = detail,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Thin,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 6.dp)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "To",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
        if (isShowDivider) {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.onBackground,
                thickness = 0.2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun CredentialInputDialog(
    currentStudentId: String?,
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (showDialog) {

        var studentId by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var isShowPassword by remember { mutableStateOf(false) }

        AlertDialog(
            modifier = modifier,
            onDismissRequest = { onDismiss() },
            title = { Text("登录凭证") },
            text = {
                Column {
                    OutlinedTextField(
                        value = studentId,
                        onValueChange = { studentId = it },
                        label = { Text(text = "请输入学号") },
                        placeholder = {
                            Text(text = currentStudentId ?: "未设置")
                        },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(text = "请输入密码") },
                        singleLine = true,
                        visualTransformation = if (isShowPassword) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        trailingIcon = {
                            Icon(
                                painter = painterResource(
                                    id = if (isShowPassword) R.drawable.ic_eye
                                    else R.drawable.ic_eye_off
                                ),
                                contentDescription = if (isShowPassword) "隐藏密码" else "显示密码",
                                tint = Color.Gray,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { isShowPassword = !isShowPassword }
                            )
                        }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    onConfirm(studentId, password)
                    onDismiss()
                }) {
                    Text("确认")
                }
            },
            dismissButton = {
                Button(onClick = { onDismiss() }) {
                    Text("取消")
                }
            }
        )
    }
}

@Preview
@Composable
fun SettingListPreview() {
//    SettingList()
}