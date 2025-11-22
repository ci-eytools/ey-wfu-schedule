package com.atri.seduley.data.ml.di

import android.content.Context
import com.atri.seduley.data.ml.TFLiteCaptchaRecognizer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MLModule {

    @Provides
    @Singleton
    fun provideCaptchaRecognizer(
        @ApplicationContext context: Context
    ) = TFLiteCaptchaRecognizer(context)
}