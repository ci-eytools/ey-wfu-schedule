package com.atri.seduley.feature.setting.presentation.util

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import java.io.File

/**
 * 通用的选择 + 裁剪图片工具
 *
 * @param activity Activity 上下文
 * @param imageName 输出文件名
 * @param aspectRatioX 裁剪宽比 (默认 1f)
 * @param aspectRatioY 裁剪高比 (默认 1f)
 * @param onSuccess 成功回调
 * @param onCancel 取消回调
 */
@Composable
fun rememberImageCropper(
    activity: Activity,
    imageName: String,
    aspectRatioX: Float = 1f,
    aspectRatioY: Float = 1f,
    onSuccess: (Uri?) -> Unit,
    onCancel: () -> Unit,
): () -> Unit {
    // 1. 接收裁剪结果
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

    // 2. 选择图片后 -> 进入裁剪
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val destUri = Uri.fromFile(File(activity.cacheDir, imageName))

            // 配置 UCrop 外观
            val options = UCrop.Options().apply {
                setToolbarTitle("裁剪图片")
                setToolbarColor(Color.BLACK)         // 工具栏背景
                setActiveControlsWidgetColor(Color.BLACK) // 主题色
                setToolbarWidgetColor(Color.WHITE)   // 返回和确认按钮颜色
                setCompressionFormat(Bitmap.CompressFormat.JPEG)
                setCompressionQuality(100)           // 清晰度

                setAllowedGestures(
                    UCropActivity.SCALE,
                    UCropActivity.NONE,
                    UCropActivity.ALL
                )
            }

            val intent = UCrop.of(it, destUri)
                .withAspectRatio(aspectRatioX, aspectRatioY)
                .withOptions(options)
                .getIntent(activity)

            cropLauncher.launch(intent)
        }
    }

    // 3. 返回触发方法
    return { pickImageLauncher.launch("image/*") }
}