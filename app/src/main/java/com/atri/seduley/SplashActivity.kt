package com.atri.seduley

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.atri.seduley.data.local.sp.SystemProvider
import com.atri.seduley.ui.screen.splash.SplashBackground
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {

    @Inject
    lateinit var systemProvider: SystemProvider

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            var alpha by remember { mutableFloatStateOf(0f) }

            val alphaAnim by animateFloatAsState(
                targetValue = alpha,
                animationSpec = tween(durationMillis = 300), // 淡入 300ms
                label = ""
            )

            Box(Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(R.drawable.default_splash_before_background),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                SplashBackground(
                    isDefaultSplash = systemProvider.isDefaultSplash(),
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(alphaAnim)
                )
            }

            LaunchedEffect(Unit) {
                alpha = 1f
                delay(300)

                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}