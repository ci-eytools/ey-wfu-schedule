package com.atri.seduley

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.atri.seduley.feature.splash.presentation.components.SplashBackground
import com.atri.seduley.ui.theme.util.ThemePreference
import com.atri.seduley.ui.theme.util.ThemeState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var beforeAlpha by remember { mutableFloatStateOf(1f) }

            var defaultAlpha by remember { mutableFloatStateOf(0f) }

            val defaultAlphaAnim by animateFloatAsState(
                targetValue = defaultAlpha,
                label = "DefaultAlphaAnimation",
                animationSpec = tween(durationMillis = 1000)
            )

            val beforeAlphaAnim by animateFloatAsState(
                targetValue = beforeAlpha,
                label = "BeforeAlphaAnimation",
                animationSpec = tween(durationMillis = 500)
            )

            Box(Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(R.drawable.default_splash_before_background),
                    contentDescription = "Before Splash",
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(beforeAlphaAnim),
                    contentScale = ContentScale.Crop
                )

                SplashBackground(
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(defaultAlphaAnim)
                )
            }

            LaunchedEffect(Unit) {
                defaultAlpha = 1f

                val seedColor = ThemePreference.seedColorFlow(this@SplashActivity)
                    .firstOrNull() ?: ThemePreference.DEFAULT_SEED_COLOR_INT
                ThemeState.seedColor.value = seedColor
                delay(600)

                beforeAlpha = 0f
                delay(500)

                // 启动 MainActivity
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                val options = ActivityOptions.makeCustomAnimation(
                    this@SplashActivity,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                )
                startActivity(intent, options.toBundle())
                finish()
            }
        }
    }
}