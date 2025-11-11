package com.rudra.ispnetworktool.domain.repository

import com.rudra.ispnetworktool.data.local.ToolLogEntity
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    suspend fun saveLog(log: ToolLogEntity)
    fun getAllLogs(): Flow<List<ToolLogEntity>>
}
