package com.rudra.ispnetworktool.domain.repository

import com.rudra.ispnetworktool.data.models.PortResult
import kotlinx.coroutines.flow.Flow

interface PortCheckerRepository {
    fun checkPort(host: String, port: Int): Flow<PortResult>
}
