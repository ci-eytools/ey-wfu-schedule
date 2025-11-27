package com.atri.seduley.ui.screen.splash

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import com.atri.seduley.R
import com.atri.seduley.core.util.Const
import java.io.File

/**
 * 开屏页
 */
@Composable
fun SplashBackground(
    isDefaultSplash: Boolean,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    val activity = context as Activity

    val painter = rememberAsyncImagePainter(
        model = if (isDefaultSplash) R.drawable.default_splash_background
        else File(
            activity.cacheDir,
            Const.SPLASH_IMAGE_NAME
        )
    )
    Box {
        Image(
            painter = painter,
            contentDescription = "Splash",
            modifier = modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }

}