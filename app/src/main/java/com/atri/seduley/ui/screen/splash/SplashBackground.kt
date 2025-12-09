package com.atri.seduley.ui.screen.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import java.io.File

/**
 * 开屏页
 */
@Composable
fun SplashBackground(
    file: File,
    modifier: Modifier = Modifier
) {
    val painter = rememberAsyncImagePainter(model = file)
    Box {
        Image(
            painter = painter,
            contentDescription = "Splash",
            modifier = modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}