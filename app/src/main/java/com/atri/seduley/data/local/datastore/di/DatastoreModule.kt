package com.atri.seduley.data.local.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.atri.seduley.data.local.datastore.CredentialDatastore
import com.atri.seduley.data.local.datastore.SystemDatastore
import com.atri.seduley.data.local.datastore.security.CryptoManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

private val Context.credentialDataStore: DataStore<Preferences> by preferencesDataStore(name = "credential_prefs")
private val Context.systemDataStore: DataStore<Preferences> by preferencesDataStore(name = "system_prefs")

@Module
@InstallIn(SingletonComponent::class)
object DatastoreModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class Credential

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class System

    /**
     * 凭证相关
     */
    @Provides
    @Singleton
    @Credential
    fun provideCredentialDataStore(@ApplicationContext context: Context) = context.credentialDataStore

    /**
     * 系统配置相关
     */
    @Provides
    @Singleton
    @System
    fun provideSystemDataStore(@ApplicationContext context: Context) = context.systemDataStore

    @Provides
    @Singleton
    fun provideCredentialDatastore(
        @Credential dataStore: DataStore<Preferences>,
        cryptoManager: CryptoManager
    ) = CredentialDatastore(dataStore, cryptoManager)

    @Provides
    @Singleton
    fun provideSystemDatastore(
        @System dataStore: DataStore<Preferences>
    ) = SystemDatastore(dataStore)
}