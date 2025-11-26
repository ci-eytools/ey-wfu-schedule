package com.atri.seduley.core.network.di

import com.atri.seduley.core.network.interceptors.LoggingInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import java.net.CookieManager
import java.net.CookiePolicy
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): LoggingInterceptor = LoggingInterceptor()

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: LoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .cookieJar(JavaNetCookieJar(CookieManager().apply {
                setCookiePolicy(CookiePolicy.ACCEPT_ALL)
            }))
            .build()
    }
}