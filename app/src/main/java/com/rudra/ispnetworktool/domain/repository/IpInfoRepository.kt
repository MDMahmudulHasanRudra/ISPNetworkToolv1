package com.rudra.ispnetworktool.domain.repository

import com.rudra.ispnetworktool.data.models.IpInfo
import kotlinx.coroutines.flow.Flow

interface IpInfoRepository {
    fun getIpInfo(): Flow<IpInfo>
}
