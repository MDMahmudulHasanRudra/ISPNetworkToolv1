package com.rudra.ispnetworktool.domain.repository

import com.rudra.ispnetworktool.data.models.IpAddress

interface IpRepository {
    suspend fun getPublicIp(): IpAddress
}
