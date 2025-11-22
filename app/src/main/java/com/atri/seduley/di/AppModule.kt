package com.atri.seduley.di

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.atri.seduley.data.local.datastore.SystemConfigurationDatastore
import com.atri.seduley.data.local.datastore.UserCredentialDatastore
import com.atri.seduley.data.local.datastore.security.CryptoManager
import com.atri.seduley.data.local.db.StudentDatabase
import com.atri.seduley.data.ml.TFLiteCaptchaRecognizer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideUserDatabase(app: Application): StudentDatabase {
        return Room.databaseBuilder(
            app,
            StudentDatabase::class.java,
            StudentDatabase.DATABASE_NAME
        ).build()
    }

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

    @Provides
    @Singleton
    fun provideCaptchaRecognizer(
        @ApplicationContext context: Context
    ) = TFLiteCaptchaRecognizer(context)

}