package com.rudra.ispnetworktool.domain.repository

import com.rudra.ispnetworktool.data.models.PingResult
import kotlinx.coroutines.flow.Flow

interface PingRepository {
    fun ping(host: String, count: Int): Flow<PingResult>
    fun stopPing()
}
