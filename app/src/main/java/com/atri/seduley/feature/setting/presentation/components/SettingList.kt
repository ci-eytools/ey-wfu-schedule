package com.atri.seduley.feature.setting.presentation.components

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.atri.seduley.R
import com.atri.seduley.core.util.Const
import com.atri.seduley.core.util.PermissionUtil
import com.atri.seduley.core.util.PermissionUtil.hasPermission
import com.atri.seduley.feature.setting.domain.entity.SystemConfiguration
import com.atri.seduley.feature.setting.presentation.SettingEvent
import com.atri.seduley.feature.setting.presentation.util.rememberImageCropper
import com.atri.seduley.ui.theme.components.ConfirmDialog
import com.atri.seduley.ui.theme.components.ListDialog
import com.atri.seduley.ui.theme.components.SingleChoiceDialog
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

@Composable
fun SettingList(
    studentId: String?,
    systemConfiguration: SystemConfiguration,
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
            lastUpdatedCourseDate = systemConfiguration.lastUpdatedCourseDate,
            clearSchedules = { onEvent(SettingEvent.ClearSchedules) },
            enterSchedules = { onEvent(SettingEvent.EnterSchedules) }
        )
        Spacer(modifier = Modifier.height(15.dp))
        BackgroundTaskOptions(
            switchNotificationDemand = { onEvent(SettingEvent.SwitchNotificationDemand(it)) },
            switchUpdateCourseDemand = { onEvent(SettingEvent.SwitchUpdateCourseDemand(it)) },
            isNeedNotification = systemConfiguration.isNeedNotification,
            isNeedUpdateCourse = systemConfiguration.isNeedUpdateCourse
        )
        Spacer(modifier = Modifier.height(15.dp))
        PermissionOptions()
        Spacer(modifier = Modifier.height(15.dp))
        CommonOptions(
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
    lastUpdatedCourseDate: LocalDateTime,
    clearSchedules: () -> Unit,
    enterSchedules: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showClearAllCourseDialog by remember { mutableStateOf(false) }
    var showEnterAllCourseDialog by remember { mutableStateOf(false) }

    val formatTime = when {
        lastUpdatedCourseDate.isEqual(Const.NO_LAST_UPDATE_SELECTED_DATE) -> "暂无数据"
        else -> "最后更新 " + lastUpdatedCourseDate.format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        )
    }

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
            detail = formatTime,
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
fun BackgroundTaskOptions(
    switchNotificationDemand: (Boolean) -> Unit,
    switchUpdateCourseDemand: (Boolean) -> Unit,
    isNeedNotification: Boolean,
    isNeedUpdateCourse: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var showSwitchNotificationDemandDialog by remember { mutableStateOf(false) }
    var showSwitchUpdateCourseDialog by remember { mutableStateOf(false) }

    var hasNotificationPermission by remember {
        mutableStateOf(
            checkAllNotificationPermissions(
                context
            )
        )
    }
    var hasBackgroundTaskPermission by remember {
        mutableStateOf(
            checkAllBackgroundTaskPermissions(
                context
            )
        )
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                hasNotificationPermission = checkAllNotificationPermissions(context)
                hasBackgroundTaskPermission = checkAllBackgroundTaskPermissions(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        ListItem(
            settingItem = "每日课程提醒",
            detail = if (!hasNotificationPermission) "权限不足, 无法启用"
            else if (isNeedNotification) "启用 (需确认拥有自启动与后台高耗电权限)" else "禁用",
            onClick = {
                // 只有在有权限时才显示对话框
                if (hasNotificationPermission) {
                    showSwitchNotificationDemandDialog = true
                }
            }
        )
    }
    SingleChoiceDialog(
        title = "每日课程提醒",
        text = "若启用需在权限列表将所有权限打开方可正常提醒",
        options = listOf("启用", "禁用"),
        selectedIndex = if (isNeedNotification) 0 else 1,
        showDialog = showSwitchNotificationDemandDialog,
        onDismiss = { showSwitchNotificationDemandDialog = false },
        onConfirm = {
            switchNotificationDemand(it == 0)
        }
    )

    ListItem(
        settingItem = "每日更新课表",

        detail = if (!hasBackgroundTaskPermission) "权限不足, 无法启用"
        else if (isNeedUpdateCourse) "启用 (需确认拥有自启动与后台高耗电权限)" else "禁用",
        onClick = {
            if (hasBackgroundTaskPermission) {
                showSwitchUpdateCourseDialog = true
            }
        }
    )
    SingleChoiceDialog(
        title = "每日更新课表",
        text = "需打开权限列表除通知外的所有权限",
        options = listOf("启用", "禁用"),
        selectedIndex = if (isNeedUpdateCourse) 0 else 1,
        showDialog = showSwitchUpdateCourseDialog,
        onDismiss = { showSwitchUpdateCourseDialog = false },
        onConfirm = {
            switchUpdateCourseDemand(it == 0)
        }
    )
}

@Composable
fun PermissionOptions(modifier: Modifier = Modifier) {
    var showPermissionOptionsDialog by remember { mutableStateOf(false) }
    var shouldReopenPermissionOptionsDialog by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current

    var hasNotificationPermission by remember { mutableStateOf(false) }
    var hasExactAlarmPermission by remember { mutableStateOf(false) }
    var hasIgnoreBatteryPermission by remember { mutableStateOf(false) }

    fun updateAllPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            hasNotificationPermission =
                hasPermission(context, Manifest.permission.POST_NOTIFICATIONS)
        }
        hasExactAlarmPermission = PermissionUtil.hasExactAlarmPermission(context)
        hasIgnoreBatteryPermission = PermissionUtil.hasIgnoreBatteryOptimization(context)
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {

                updateAllPermissions()

                if (shouldReopenPermissionOptionsDialog) {
                    showPermissionOptionsDialog = true
                    shouldReopenPermissionOptionsDialog = false
                }
            }
        }
        updateAllPermissions()

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    ListItem(
        settingItem = "权限配置",
        onClick = { showPermissionOptionsDialog = true },
        modifier = modifier
    )

    if (showPermissionOptionsDialog) {
        ListDialog(
            title = "权限配置",
            showDialog = true,
            onDismiss = { showPermissionOptionsDialog = false }
        ) {
            fun handlePermissionClick(openSettings: () -> Unit) {
                openSettings()
                shouldReopenPermissionOptionsDialog = true
                showPermissionOptionsDialog = false
            }

            ListItem(
                settingItem = "通知",
                detail = if (hasNotificationPermission) "已拥有" else "未拥有",
                onClick = {
                    handlePermissionClick {
                        PermissionUtil.openNotificationPermission(context, null)
                    }
                }
            )
            ListItem(
                settingItem = "精确闹钟",
                detail = if (hasExactAlarmPermission) "已拥有" else "未拥有",
                onClick = {
                    handlePermissionClick {
                        PermissionUtil.openExactAlarmSettings(context)
                    }
                },
            )
            ListItem(
                settingItem = "忽略电池优化",
                detail = if (hasIgnoreBatteryPermission) "已拥有" else "未拥有",
                onClick = {
                    handlePermissionClick {
                        PermissionUtil.openIgnoreBatteryOptimization(context)
                    }
                }
            )
            ListItem(
                settingItem = "应用自启动",
                detail = "跳转至设置",
                onClick = {
                    handlePermissionClick {
                        PermissionUtil.openAppDetailsSettings(context)
                    }
                }
            )
            ListItem(
                settingItem = "允许后台高耗电",
                detail = "跳转至设置",
                onClick = {
                    handlePermissionClick {
                        PermissionUtil.openBatteryOptimizationSettings(context)
                    }
                }
            )
        }
    }
}


@Composable
fun CommonOptions(
    resetCover: () -> Unit,
    updateSplash: () -> Unit,
    resetSplash: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showResetCoverDialog by remember { mutableStateOf(false) }
    var showResetSplashDialog by remember { mutableStateOf(false) }
    var showUpdateSplashDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
    ) {
        val activity = (LocalContext.current) as Activity
        val startSplashCrop = rememberImageCropper(
            activity = activity,
            imageName = Const.SPLASH_IMAGE_NAME,
            aspectRatioX = 9f,
            aspectRatioY = 16f,
            onSuccess = { updateSplash() },
            onCancel = { }
        )
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
        ListItem(
            settingItem = "更新开屏页",
            onClick = { showUpdateSplashDialog = true }
        )
        ConfirmDialog(
            text = "是否读取相册更新开屏页",
            showDialog = showUpdateSplashDialog,
            onDismiss = { showUpdateSplashDialog = false },
            onConfirm = { startSplashCrop() }
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

/**
 * @param settingItem 设置项名称
 * @param detail 设置项详情
 * @param isShowDivider 是否显示分割线
 */
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

private fun checkAllBackgroundTaskPermissions(context: Context): Boolean {
    return PermissionUtil.hasExactAlarmPermission(context)
            && PermissionUtil.hasIgnoreBatteryOptimization(context)
}

private fun checkAllNotificationPermissions(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        hasPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                && checkAllBackgroundTaskPermissions(context)
    } else {
        checkAllBackgroundTaskPermissions(context)
    }
}

@Preview
@Composable
fun SettingListPreview() {
//    SettingList()
}