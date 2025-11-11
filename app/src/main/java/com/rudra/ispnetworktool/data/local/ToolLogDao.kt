package com.rudra.ispnetworktool.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ToolLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ToolLogEntity)

    @Query("SELECT * FROM tool_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<ToolLogEntity>>
}
