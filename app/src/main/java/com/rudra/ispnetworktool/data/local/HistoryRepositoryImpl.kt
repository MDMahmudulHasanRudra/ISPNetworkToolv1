package com.rudra.ispnetworktool.data.local

import com.rudra.ispnetworktool.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val toolLogDao: ToolLogDao
) : HistoryRepository {

    override suspend fun saveLog(log: ToolLogEntity) {
        toolLogDao.insertLog(log)
    }

    override fun getAllLogs(): Flow<List<ToolLogEntity>> {
        return toolLogDao.getAllLogs()
    }
}
