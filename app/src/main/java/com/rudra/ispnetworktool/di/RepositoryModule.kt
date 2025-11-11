package com.rudra.ispnetworktool.di

import android.content.Context
import com.rudra.ispnetworktool.data.local.*
import com.rudra.ispnetworktool.data.remote.DnsRepositoryImpl
import com.rudra.ispnetworktool.data.remote.IpInfoRepositoryImpl
import com.rudra.ispnetworktool.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun providePingRepository(): PingRepository {
        return PingRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideTracerouteRepository(): TracerouteRepository {
        return TracerouteRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideDnsRepository(client: HttpClient): DnsRepository {
        return DnsRepositoryImpl(client)
    }

    @Provides
    @Singleton
    fun provideIpInfoRepository(client: HttpClient, @ApplicationContext context: Context): IpInfoRepository {
        return IpInfoRepositoryImpl(client, context)
    }

    @Provides
    @Singleton
    fun providePortCheckerRepository(): PortCheckerRepository {
        return PortCheckerRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideWhoisRepository(): WhoisRepository {
        return WhoisRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideHistoryRepository(dao: ToolLogDao): HistoryRepository {
        return HistoryRepositoryImpl(dao)
    }
}
