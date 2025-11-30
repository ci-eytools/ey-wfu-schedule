package com.atri.seduley.core.alarm.di

import android.app.AlarmManager
import android.content.Context
import com.atri.seduley.core.alarm.AlarmScheduler
import com.atri.seduley.core.alarm.impl.AlarmSchedulerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AlarmModule {

    @Provides
    @Singleton
    fun provideAlarmManager(@ApplicationContext context: Context): AlarmManager {
        return context.getSystemService(AlarmManager::class.java)
    }

    @Binds
    @Singleton
    abstract fun bindAlarmService(
        impl: AlarmSchedulerImpl
    ): AlarmScheduler
}