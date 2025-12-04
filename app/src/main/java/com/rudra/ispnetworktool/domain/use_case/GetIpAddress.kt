package com.rudra.ispnetworktool.domain.use_case

import com.rudra.ispnetworktool.data.models.IpAddress
import com.rudra.ispnetworktool.domain.repository.IpRepository
import java.net.InetAddress

class GetIpAddress(private val ipRepository: IpRepository) {

    suspend fun getPublicIp(): IpAddress {
        return ipRepository.getPublicIp()
    }

    fun getLocalIp(): IpAddress {
        return try {
            val localHost = InetAddress.getLocalHost()
            IpAddress(localHost.hostAddress)
        } catch (e: Exception) {
            e.printStackTrace()
            IpAddress("Unavailable")
        }
    }
}
