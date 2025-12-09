package com.atri.seduley.ui.screen.setting.util

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import com.atri.seduley.core.util.Const
import com.yalantis.ucrop.UCrop
import java.io.File

/**
 * 通用的选择 + 裁剪图片工具
 *
 * @param activity Activity 上下文
 * @param aspectRatioX 裁剪宽比 (默认 1f)
 * @param aspectRatioY 裁剪高比 (默认 1f)
 * @param onSuccess 成功回调
 * @param onCancel 取消回调
 */
@Composable
fun rememberImageCropper(
    activity: Activity,
    aspectRatioX: Float = 1f,
    aspectRatioY: Float = 1f,
    onSuccess: (Uri?) -> Unit,
    onCancel: () -> Unit,
): () -> Unit {

    // 1. UCrop 裁剪结果处理
    val cropLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val resultUri = UCrop.getOutput(result.data!!)
            onSuccess(resultUri)
        } else {
            onCancel()
        }
    }

    // 2. 图片选择处理
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->

        if (uri == null) {
            onCancel()
            return@rememberLauncherForActivityResult
        }

        val mimeType = activity.contentResolver.getType(uri)

        // GIF 直接保存，不裁剪
        if (mimeType == "image/gif") {
            val destFile = File(activity.cacheDir, Const.GIF_COVER_IMAGE_NAME)
            val destUri = Uri.fromFile(destFile)

            activity.contentResolver.openInputStream(uri)?.use { input ->
                destFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            onSuccess(destUri)
            return@rememberLauncherForActivityResult
        }

        // 其他图片走 UCrop
        val destUri = Uri.fromFile(File(activity.cacheDir, Const.COVER_IMAGE_NAME))

        val options = UCrop.Options().apply {
            setToolbarTitle("裁剪图片")
            setToolbarColor(Color.BLACK)
            setActiveControlsWidgetColor(Color.BLACK)
            setToolbarWidgetColor(Color.WHITE)
            setCompressionFormat(Bitmap.CompressFormat.JPEG)
            setCompressionQuality(100)
        }

        val intent = UCrop.of(uri, destUri)
            .withAspectRatio(aspectRatioX, aspectRatioY)
            .withOptions(options)
            .getIntent(activity)

        cropLauncher.launch(intent)
    }

    /** 3. 调用方法 */
    return { pickImageLauncher.launch("image/*") }
}