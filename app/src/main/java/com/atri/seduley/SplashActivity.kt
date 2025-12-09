package com.atri.seduley

import android.R.attr.duration
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.atri.seduley.core.util.Const
import com.atri.seduley.domain.result.Result
import com.atri.seduley.domain.usecase.SystemConfUseCase
import com.atri.seduley.ui.screen.splash.SplashBackground
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {

    @Inject
    lateinit var systemConfUseCase: SystemConfUseCase

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var startAnimation by remember { mutableStateOf(false) }
            val alphaAnim by animateFloatAsState(
                targetValue = if (startAnimation) 1f else 0f,
                animationSpec = tween(durationMillis = 300, easing = FastOutLinearInEasing),
                label = ""
            )

            val context = LocalContext.current
            val activity = context as Activity
            val file = File(activity.cacheDir, Const.SPLASH_IMAGE_NAME)
            val duration = when (val info = systemConfUseCase.getSplashDuration()) {
                is Result.Success -> info.value
                is Result.Error -> Const.DEFAULT_SPLASH_DURATION
            }
            val isShowSplash = file.exists() && duration != 0
            Box(Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(R.drawable.before_splash),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                if (isShowSplash) {
                    SplashBackground(
                        file,
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(alphaAnim),
                    )
                }
            }

            LaunchedEffect(Unit) {
                startAnimation = true
                if (isShowSplash) {
                    delay(duration.toLong())
                }

                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}