package com.atri.seduley.data.local.datastore.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.atri.seduley.data.local.datastore.SystemConfigurationDatastore
import com.atri.seduley.data.local.datastore.UserCredentialDatastore
import com.atri.seduley.data.local.datastore.security.CryptoManager
import com.atri.seduley.di.DataStoreModule
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatastoreModule {

    @Provides
    @Singleton
    fun provideUserCredentialDatastore(
        @DataStoreModule.User dataStore: DataStore<Preferences>,
        cryptoManager: CryptoManager
    ) = UserCredentialDatastore(dataStore, cryptoManager)

    @Provides
    @Singleton
    fun provideSystemConfigurationDatastore(
        @DataStoreModule.System dataStore: DataStore<Preferences>
    ) = SystemConfigurationDatastore(dataStore)
}