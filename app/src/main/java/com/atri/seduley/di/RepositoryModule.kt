package com.atri.seduley.di

import com.atri.seduley.data.repository.AuthRepositoryImpl
import com.atri.seduley.data.repository.SystemConfInfoRepositoryImpl
import com.atri.seduley.domain.repository.AuthRepository
import com.atri.seduley.domain.repository.SystemConfInfoRepository
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
    abstract fun bindSystemConfInfoRepository(
        impl: SystemConfInfoRepositoryImpl
    ): SystemConfInfoRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository
}