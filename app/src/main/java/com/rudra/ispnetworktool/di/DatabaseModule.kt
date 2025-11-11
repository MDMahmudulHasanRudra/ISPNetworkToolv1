package com.rudra.ispnetworktool.di

import android.content.Context
import androidx.room.Room
import com.rudra.ispnetworktool.data.local.AppDatabase
import com.rudra.ispnetworktool.data.local.ToolLogDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "isp_network_tool_db"
        ).build()
    }

    @Provides
    fun provideToolLogDao(database: AppDatabase): ToolLogDao {
        return database.toolLogDao()
    }
}
