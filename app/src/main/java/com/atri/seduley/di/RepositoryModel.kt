package com.atri.seduley.di

import com.atri.seduley.feature.course.data.repository.InitInfoRepositoryImpl
import com.atri.seduley.feature.course.domain.repository.InitInfoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindInitInfoRepository(
        impl: InitInfoRepositoryImpl
    ): InitInfoRepository
}