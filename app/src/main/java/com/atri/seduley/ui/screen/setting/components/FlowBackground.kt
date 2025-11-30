package com.atri.seduley.ui.screen.setting.components

import android.app.Activity
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.atri.seduley.R
import com.atri.seduley.core.util.Const
import com.atri.seduley.ui.components.ConfirmDialog
import com.atri.seduley.ui.screen.setting.SettingEvent
import com.atri.seduley.ui.screen.setting.util.rememberImageCropper
import java.io.File

@Composable
fun FlowBackground(
    coverVersion: Int,
    onEvent: (SettingEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as Activity
    val coverFile = File(activity.cacheDir, Const.COVER_IMAGE_NAME)

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showUpdateCoverDialog by remember { mutableStateOf(false) }

    // 初始化封面
    LaunchedEffect(coverVersion) {
        imageUri = if (coverFile.exists()) Uri.fromFile(coverFile) else null
    }

    val startCrop = rememberImageCropper(
        activity = activity,
        imageName = Const.COVER_IMAGE_NAME,
        aspectRatioX = 16f,
        aspectRatioY = 9f,
        onSuccess = { newUri ->
            imageUri = newUri
            // 通知 ViewModel 更新 DataStore 和主题
            onEvent(SettingEvent.UpdateCover)
        },
        onCancel = { /* 用户取消裁剪，不做操作 */ }
    )

    Image(
        painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(context)
                .data(imageUri?.toString()?.plus("?v=$coverVersion") ?: R.drawable.default_cover)
                .crossfade(true)
                .build()
        ),
        contentDescription = "Cover",
        contentScale = ContentScale.Crop,
        modifier = modifier.clickable { showUpdateCoverDialog = true }
    )


    ConfirmDialog(
        text = "是否读取相册更新封面",
        showDialog = showUpdateCoverDialog,
        onDismiss = { showUpdateCoverDialog = false },
        onConfirm = { startCrop() }
    )
}

@Preview
@Composable
fun FlowBackgroundPreview() {
}
