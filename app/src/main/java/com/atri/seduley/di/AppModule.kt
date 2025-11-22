package com.atri.seduley.di

import android.app.Application
import androidx.room.Room
import com.atri.seduley.data.local.StudentDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
}