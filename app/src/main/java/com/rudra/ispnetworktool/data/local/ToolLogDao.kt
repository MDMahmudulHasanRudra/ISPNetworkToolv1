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

    @Query("DELETE FROM tool_logs WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM tool_logs")
    suspend fun clearAll()

    @Query("SELECT * FROM tool_logs WHERE id = :id")
    suspend fun getLogById(id: Int): ToolLogEntity?
}
