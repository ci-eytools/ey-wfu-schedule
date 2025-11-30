package com.atri.seduley.core.alarm.di

import com.atri.seduley.core.alarm.AlarmService
import com.atri.seduley.core.alarm.impl.AlarmServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AlarmModule {

    @Binds
    @Singleton
    abstract fun bindAlarmService(
        impl: AlarmServiceImpl
    ): AlarmService
}