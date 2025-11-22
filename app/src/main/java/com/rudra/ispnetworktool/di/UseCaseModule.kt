package com.rudra.ispnetworktool.di

import com.rudra.ispnetworktool.domain.repository.IpRepository
import com.rudra.ispnetworktool.domain.use_case.GetIpAddress
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetIpAddress(ipRepository: IpRepository): GetIpAddress {
        return GetIpAddress(ipRepository)
    }
}
