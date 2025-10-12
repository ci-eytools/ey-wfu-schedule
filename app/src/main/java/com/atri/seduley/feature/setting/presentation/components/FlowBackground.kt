package com.atri.seduley.feature.setting.presentation.components

import android.app.Activity
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import com.atri.seduley.R
import com.atri.seduley.feature.setting.presentation.util.Assets
import com.atri.seduley.feature.setting.presentation.util.rememberImageCropper
import com.atri.seduley.ui.theme.util.ThemePreference
import com.atri.seduley.ui.theme.util.ThemeRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun FlowBackground(
    externalResetTrigger: Int, // 用于从 ViewModel 触发重置
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as Activity
    val coverFile = File(activity.cacheDir, Assets.COVER_IMAGE_NAME) // 本地缓存文件

    // imageUri 用于显示图片, version 用于强制 Coil 刷新
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var version by remember { mutableIntStateOf(0) } // 使用 mutableStateOf<Int>

    val scope = rememberCoroutineScope()

    // 初始化封面图片 URI
    LaunchedEffect(Unit) {
        val savedUriString = ThemePreference.coverUriFlow(context).firstOrNull()
        imageUri = if (!savedUriString.isNullOrBlank()) {
            savedUriString.toUri()
        } else if (coverFile.exists()) {
            Uri.fromFile(coverFile)
        } else {
            null
        }
        scope.launch { ThemeRepository.updateCoverAndSeedColorInStore(context, imageUri) }
        version++
    }

    LaunchedEffect(externalResetTrigger) {
        if (externalResetTrigger > 0) {
            imageUri = null
            version++
            scope.launch { ThemeRepository.updateCoverAndSeedColorInStore(context, null) }
        }
    }


    val startCrop = rememberImageCropper(
        activity = activity,
        imageName = Assets.COVER_IMAGE_NAME, // 裁剪后保存到本地缓存的文件名
        aspectRatioX = 16f,
        aspectRatioY = 9f,
        onSuccess = { newUri -> // 裁剪成功
            imageUri = newUri // 更新显示的 URI
            version++         // 刷新 Coil
            // 将新的 URI 和从中提取的主题色更新到 DataStore
            scope.launch { ThemeRepository.updateCoverAndSeedColorInStore(context, newUri) }
        },
        onCancel = {
            // 用户取消裁剪, 不执行任何操作，保持当前图片和主题
        }
    )

    val painter = rememberAsyncImagePainter(
        model = imageUri?.toString()?.plus("?v=$version") ?: R.drawable.default_cover
    )

    Image(
        painter = painter,
        contentDescription = "Cover",
        contentScale = ContentScale.Crop,
        modifier = modifier.clickable { startCrop() }
    )
}


@Preview
@Composable
fun FlowBackgroundPreview() {
}
