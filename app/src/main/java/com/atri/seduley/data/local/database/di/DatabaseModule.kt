package com.atri.seduley.data.local.database.di

import android.app.Application
import androidx.room.Room
import com.atri.seduley.data.local.database.CourseDao
import com.atri.seduley.data.local.database.SeduleyDatabase
import com.atri.seduley.data.local.database.StudentDao
import com.atri.seduley.data.local.database.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideSeduleyDatabase(app: Application): SeduleyDatabase {
        return Room.databaseBuilder(
            app,
            SeduleyDatabase::class.java,
            SeduleyDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideStudentDao(db: SeduleyDatabase): StudentDao = db.studentDao

    @Provides
    @Singleton
    fun provideCourseDao(db: SeduleyDatabase): CourseDao = db.courseDao

    @Provides
    @Singleton
    fun provideTaskDao(db: SeduleyDatabase): TaskDao = db.taskDao
}