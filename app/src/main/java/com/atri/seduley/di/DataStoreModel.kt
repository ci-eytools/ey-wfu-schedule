package com.atri.seduley.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")
private val Context.systemDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings_prefs")

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class User

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class System

    /**
     * 用户相关
     */
    @Provides
    @Singleton
    @User
    fun provideUserDataStore(@ApplicationContext context: Context) = context.userDataStore

    /**
     * 系统配置相关
     */
    @Provides
    @Singleton
    @System
    fun provideSystemDataStore(@ApplicationContext context: Context) = context.systemDataStore
}