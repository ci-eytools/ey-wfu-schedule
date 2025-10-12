package com.atri.seduley.feature.splash.presentation.components

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
import com.atri.seduley.feature.setting.presentation.util.Assets
import java.io.File

@Composable
fun SplashBackground(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val activity = context as Activity
    val splash = File(activity.cacheDir, Assets.SPLASH_IMAGE_NAME)

    val painter = rememberAsyncImagePainter(
        model = splash.takeIf { splash.exists() } ?: R.drawable.default_splash_background
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