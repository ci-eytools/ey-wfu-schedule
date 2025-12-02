package com.atri.seduley.ui.screen.setting.components

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.atri.seduley.R
import com.atri.seduley.core.util.Const
import com.atri.seduley.core.util.PermissionUtil
import com.atri.seduley.core.util.PermissionUtil.hasPermission
import com.atri.seduley.data.local.datastore.entity.TaskWay
import com.atri.seduley.data.local.datastore.entity.getMsg
import com.atri.seduley.domain.model.SystemConf
import com.atri.seduley.ui.components.ConfirmDialog
import com.atri.seduley.ui.components.ListDialog
import com.atri.seduley.ui.components.SingleChoiceDialog
import com.atri.seduley.ui.model.StudentInfo
import com.atri.seduley.ui.model.StudentUpdate
import com.atri.seduley.ui.screen.setting.SettingEvent
import com.atri.seduley.ui.screen.setting.util.rememberImageCropper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun SettingList(
    studentId: String?,
    studentInfos: List<StudentInfo>,
    updateTime: LocalDateTime,
    systemConf: SystemConf,
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
            studentInfos = studentInfos,
            saveCredential = { studentId, password ->
                onEvent(
                    SettingEvent.SaveCredential(
                        studentId,
                        password
                    )
                )
            },
            switchCurrentId = { onEvent(SettingEvent.SwitchCredential(it)) },
            updateCredential = { onEvent(SettingEvent.UpdateCredential(it)) },
            clearCredential = { onEvent(SettingEvent.ClearCredential(it)) }
        )
        Spacer(modifier = Modifier.height(15.dp))
        CourseInfo(
            lastUpdatedCourseDate = updateTime,
            clearSchedules = { onEvent(SettingEvent.ClearCourses) },
            enterSchedules = { onEvent(SettingEvent.UpdateCourses) }
        )
        Spacer(modifier = Modifier.height(15.dp))
        BackgroundTaskOptions(
            switchNotificationDemand = { onEvent(SettingEvent.SwitchNotificationDemand(it)) },
            switchUpdateCourseDemand = { onEvent(SettingEvent.SwitchUpdateCourseDemand(it)) },
            notificationWay = systemConf.notificationWay,
            updateCourseWay = systemConf.updateCourseWay
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
    studentInfos: List<StudentInfo>,
    switchCurrentId: (String) -> Unit,
    clearCredential: (String) -> Unit,
    updateCredential: (StudentUpdate) -> Unit,
    saveCredential: (String, String) -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    var isShowCredentialInputDialog by remember { mutableStateOf(false) }
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        val nickName = studentInfos.firstOrNull { it.studentId == studentId }?.nickName
        ListItem(
            settingItem = "账户信息",
            detail = nickName ?: studentId.takeIf { !it.isNullOrEmpty() } ?: "未登录",
            onClick = { isShowCredentialInputDialog = true }
        )
    }
    ManagerCredential(
        studentInfos = studentInfos,
        showDialog = isShowCredentialInputDialog,
        onDismiss = {
            isShowCredentialInputDialog = false
        },
        currentStudentId = studentId,
        switchCurrentId = switchCurrentId,
        clearCredential = { clearCredential(it) },
        updateCredential = { updateCredential(it) },
        onConfirm = { studentId, password -> saveCredential(studentId, password) }
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
            settingItem = "清空课表",
            onClick = { showClearAllCourseDialog = true },
        )
        ConfirmDialog(
            text = "是否清空课表",
            showDialog = showClearAllCourseDialog,
            onDismiss = { showClearAllCourseDialog = false },
            onConfirm = { clearSchedules() }
        )
        ListItem(
            settingItem = "更新课表",
            detail = formatTime,
            onClick = { showEnterAllCourseDialog = true }
        )
        ConfirmDialog(
            text = "是否更新课程",
            showDialog = showEnterAllCourseDialog,
            onDismiss = { showEnterAllCourseDialog = false },
            onConfirm = { enterSchedules() }
        )
    }
}

@Composable
fun BackgroundTaskOptions(
    switchNotificationDemand: (TaskWay) -> Unit,
    switchUpdateCourseDemand: (TaskWay) -> Unit,
    notificationWay: TaskWay,
    updateCourseWay: TaskWay,
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
            detail = if (!hasNotificationPermission) "权限不足, 无法启用" else notificationWay.getMsg(),
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
        options = listOf("禁用", "自动选择", "不精确", "精确（会增加耗电）"),
        selectedIndex = notificationWay.value,
        showDialog = showSwitchNotificationDemandDialog,
        onDismiss = { showSwitchNotificationDemandDialog = false },
        onConfirm = { switchNotificationDemand(TaskWay.fromValue(it)) }
    )
    ListItem(
        settingItem = "每日更新课表",

        detail = if (!hasBackgroundTaskPermission) "权限不足, 无法启用" else updateCourseWay.getMsg(),
        onClick = {
            if (hasBackgroundTaskPermission) {
                showSwitchUpdateCourseDialog = true
            }
        }
    )
    SingleChoiceDialog(
        title = "每日更新课表",
        text = "需打开权限列表除通知外的所有权限",
        options = listOf("禁用", "自动选择", "不精确", "精确（会增加后台耗电）"),
        selectedIndex = updateCourseWay.value,
        showDialog = showSwitchUpdateCourseDialog,
        onDismiss = { showSwitchUpdateCourseDialog = false },
        onConfirm = { switchUpdateCourseDemand(TaskWay.fromValue(it)) }
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
                }
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
fun ManagerCredential(
    studentInfos: List<StudentInfo>,
    currentStudentId: String?,
    switchCurrentId: (String) -> Unit,
    clearCredential: (String) -> Unit,
    updateCredential: (StudentUpdate) -> Unit,
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (!showDialog) return
    var isUserListExpanded by remember { mutableStateOf(false) }
    var isInputSectionVisible by remember { mutableStateOf(false) }

    var studentId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var isShowPassword by remember { mutableStateOf(false) }


    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = modifier.fillMaxWidth(0.9f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // 标题
                Text(
                    text = "账户管理",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 18.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            0.5.dp,
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(8.dp)
                        )
                        .background(color = MaterialTheme.colorScheme.inversePrimary.copy(0.1f))
                        .animateContentSize()
                ) {
                    val currentUser =
                        studentInfos.firstOrNull { it.studentId == currentStudentId }
                    val otherStudents =
                        studentInfos.filter { it.studentId != currentStudentId }

                    Box(modifier = Modifier.clickable {
                        isUserListExpanded = !isUserListExpanded
                    }) {
                        CredentialItem(
                            studentId = currentUser?.studentId ?: "",
                            nickName = currentUser?.nickName ?: "",
                            clearCredential = { /* 当前登录用户不可删除 */ },
                            updateCredential = { updateCredential(it) },
                            isExpanded = isUserListExpanded,
                            modifier = Modifier.padding(end = 3.dp)
                        )
                    }

                    if (isUserListExpanded && otherStudents.isNotEmpty()) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        LazyColumn(
                            modifier = Modifier
                                .heightIn(max = 160.dp)
                        ) {
                            items(otherStudents) { studentInfo ->
                                CredentialItem(
                                    studentId = studentInfo.studentId,
                                    nickName = studentInfo.nickName,
                                    isExpanded = null,
                                    clearCredential = { clearCredential(it) },
                                    updateCredential = { updateCredential(it) },
                                    modifier = Modifier.clickable { switchCurrentId(studentInfo.studentId) }
                                )
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                    thickness = 0.5.dp,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }
                        }
                    }
                }


                Spacer(modifier = Modifier.height(24.dp))

                AnimatedVisibility(visible = isInputSectionVisible) {
                    Column {
                        OutlinedTextField(
                            value = studentId,
                            onValueChange = { studentId = it },
                            label = { Text(text = "请输入学号") },
                            placeholder = { Text(text = "添加凭证") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text(text = "请输入密码") },
                            singleLine = true,
                            visualTransformation = if (isShowPassword) VisualTransformation.None
                            else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { isShowPassword = !isShowPassword }) {
                                    Icon(
                                        painter = painterResource(
                                            id = if (isShowPassword) R.drawable.ic_eye
                                            else R.drawable.ic_eye_off
                                        ),
                                        contentDescription = if (isShowPassword) "Show password"
                                        else "Hide passwords",
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = nickname,
                                onValueChange = { nickname = it },
                                label = { Text(text = "请输入昵称（可选）") },
                                singleLine = true,
                                modifier = Modifier.weight(0.6f)
                            )
                            Spacer(modifier = Modifier.weight(0.1f))
                            Button(
                                onClick = {
                                    onConfirm(studentId, password)
                                    isInputSectionVisible = false
                                    studentId = ""
                                    password = ""
                                    nickname = ""
                                },
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 3.dp),
                                modifier = Modifier
                                    .weight(0.3f)
                            ) {
                                Text("确认添加")
                            }
                        }
                    }

                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { isInputSectionVisible = !isInputSectionVisible },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (isInputSectionVisible) "取消添加" else "添加凭证")
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDismiss) {
                        Text("关闭")
                    }
                }
            }
        }
    }
}

@Composable
fun CredentialItem(
    studentId: String,
    nickName: String,
    updateCredential: (StudentUpdate) -> Unit,
    clearCredential: (String) -> Unit,
    isExpanded: Boolean? = null,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        val isNickNull = nickName.isEmpty()
        var isShowDeleteCredentialDialog by remember { mutableStateOf(false) }
        var isShowUpdateCredentialDialog by remember { mutableStateOf(false) }

        val rotation by animateFloatAsState(
            targetValue = if (isExpanded == true) -180f else 0f,
            animationSpec = tween(durationMillis = 300),
            label = "rotationAnimation"
        )
        Column(
            modifier = Modifier
                .padding(start = 8.dp, top = 6.dp, bottom = 6.dp)
                .heightIn(min = 40.dp),
            verticalArrangement = Arrangement.Center
        ) {
            if (!isNickNull) {
                Text(
                    text = nickName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = if (!studentId.isEmpty()) studentId else "未登录",
                style = if (!isNickNull) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyLarge,
                fontWeight = if (!isNickNull) FontWeight.Thin else FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = { isShowUpdateCredentialDialog = true }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_edit),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = "UpdateCredential",
                modifier = Modifier
                    .padding(8.dp)
                    .size(24.dp)
            )
        }


        if (isExpanded != null) {
            Icon(
                painter = painterResource(R.drawable.ic_triangle_arrow),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = if (isExpanded) "Expanded" else "Collapsed",
                modifier = Modifier
                    .padding(8.dp)
                    .size(28.dp)
                    .rotate(rotation)
            )
        } else {
            IconButton(
                onClick = { isShowDeleteCredentialDialog = true }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_delete),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "DeleteCredential",
                    modifier = Modifier
                        .padding(8.dp)
                        .size(24.dp)
                )
            }
        }

        ConfirmDialog(
            text = "是否删除凭证：\n$studentId",
            showDialog = isShowDeleteCredentialDialog,
            onDismiss = { isShowDeleteCredentialDialog = false },
            onConfirm = { clearCredential(studentId) }
        )

        CredentialUpdateInputDialog(
            studentId = studentId,
            oldNickname = nickName,
            showDialog = isShowUpdateCredentialDialog,
            onDismiss = { isShowUpdateCredentialDialog = false },
            onConfirm = { updateCredential(it) }
        )
    }
}

@Composable
fun CredentialUpdateInputDialog(
    studentId: String,
    showDialog: Boolean,
    oldNickname: String,
    onDismiss: () -> Unit,
    onConfirm: (StudentUpdate) -> Unit,
    modifier: Modifier = Modifier
) {
    if (showDialog) {
        var password by remember { mutableStateOf("") }
        var nickname by remember { mutableStateOf(oldNickname) }
        var isShowPassword by remember { mutableStateOf(false) }

        AlertDialog(
            modifier = modifier,
            onDismissRequest = { onDismiss() },
            title = { Text("更新凭证") },
            text = {
                Column {
                    OutlinedTextField(
                        value = studentId,
                        onValueChange = { /* 更改凭证时禁止更改 id */ },
                        label = { Text(text = "学号不可更改") },
                        placeholder = {
                            Text(text = studentId)
                        },
                        singleLine = true,
                        enabled = false,     // 更改凭证时禁止更改 id
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(text = "新密码（留空不更改）") },
                        singleLine = true,
                        visualTransformation = if (isShowPassword) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        trailingIcon = {
                            Icon(
                                painter = painterResource(
                                    id = if (isShowPassword) R.drawable.ic_eye
                                    else R.drawable.ic_eye_off
                                ),
                                contentDescription = if (isShowPassword) "Show password"
                                else "Hide passwords",
                                tint = Color.Gray,
                                modifier = Modifier
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { isShowPassword = !isShowPassword }
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = nickname,
                        onValueChange = { newText ->
                            if (newText.length <= 16) {
                                nickname = newText
                            }
                        },
                        label = { Text(text = "昵称（留空清除，最多16字符）") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (oldNickname == nickname) {
                        onConfirm(StudentUpdate(studentId, password))
                    } else {
                        onConfirm(StudentUpdate(studentId, password, nickname))
                    }
                    onDismiss()
                }) {
                    Text("更新")
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