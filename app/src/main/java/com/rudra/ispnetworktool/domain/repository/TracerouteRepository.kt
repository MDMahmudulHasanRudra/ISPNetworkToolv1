package com.rudra.ispnetworktool.domain.repository

import com.rudra.ispnetworktool.data.models.TracerouteResult
import kotlinx.coroutines.flow.Flow

interface TracerouteRepository {
    fun traceroute(host: String): Flow<TracerouteResult>
    fun stopTraceroute()
}
