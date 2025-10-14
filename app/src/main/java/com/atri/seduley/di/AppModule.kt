package com.atri.seduley.di

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.atri.seduley.core.security.CryptoManager
import com.atri.seduley.feature.course.data.data_score.ClazzDatabase
import com.atri.seduley.feature.course.data.data_score.CourseDatabase
import com.atri.seduley.feature.course.data.repository.BaseInfoRepositoryImpl
import com.atri.seduley.feature.course.data.repository.ClazzRepositoryImpl
import com.atri.seduley.feature.course.data.repository.CourseRepositoryImpl
import com.atri.seduley.feature.course.domain.repository.BaseInfoRepository
import com.atri.seduley.feature.course.domain.repository.ClazzRepository
import com.atri.seduley.feature.course.domain.repository.CourseRepository
import com.atri.seduley.feature.setting.data.repository.UserCredentialRepositoryImpl
import com.atri.seduley.feature.setting.domain.repository.UserCredentialRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCourseDatabase(app: Application): CourseDatabase {
        return Room.databaseBuilder(
            app,
            CourseDatabase::class.java,
            CourseDatabase.DATABASE_NAME
        ).build()
    }
    @Provides
    @Singleton
    fun provideCourseRepository(db: CourseDatabase): CourseRepository {
        return CourseRepositoryImpl(db.courseDao)
    }

    @Provides
    @Singleton
    fun provideClazzDatabase(app: Application): ClazzDatabase {
        return Room.databaseBuilder(
            app,
            ClazzDatabase::class.java,
            ClazzDatabase.DATABASE_NAME
        ).build()
    }
    @Provides
    @Singleton
    fun provideClazzRepository(db: ClazzDatabase): ClazzRepository {
        return ClazzRepositoryImpl(db.clazzDao)
    }

    @Provides
    @Singleton
    fun provideUserCredentialRepository(
        @DataStoreModule.UserPrefs dataStore: DataStore<Preferences>,
        cryptoManager: CryptoManager
    ): UserCredentialRepository {
        return UserCredentialRepositoryImpl(dataStore, cryptoManager)
    }

    @Provides
    @Singleton
    fun provideBaseInfoRepository(
        @DataStoreModule.UserPrefs dataStore: DataStore<Preferences>
    ): BaseInfoRepository {
        return BaseInfoRepositoryImpl(dataStore)
    }
}