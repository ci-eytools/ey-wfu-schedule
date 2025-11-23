package com.atri.seduley.di

import com.atri.seduley.data.ml.TFLiteCaptchaRecognizer
import com.atri.seduley.domain.ml.CaptchaRecognizer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MLModule {

    @Binds
    @Singleton
    abstract fun bindCaptchaRecognizer(
        impl: TFLiteCaptchaRecognizer
    ): CaptchaRecognizer
}