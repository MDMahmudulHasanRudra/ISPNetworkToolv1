package com.rudra.ispnetworktool.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ToolLogEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun toolLogDao(): ToolLogDao
}
