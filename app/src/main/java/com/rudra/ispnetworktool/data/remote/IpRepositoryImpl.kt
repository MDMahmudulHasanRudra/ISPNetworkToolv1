package com.rudra.ispnetworktool.data.remote

import com.rudra.ispnetworktool.data.models.IpAddress
import com.rudra.ispnetworktool.domain.repository.IpRepository
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

class IpRepositoryImpl : IpRepository {
    override suspend fun getPublicIp(): IpAddress {
        return try {
            val url = URL("https://api.ipify.org")
            val connection = url.openConnection()
            val reader = BufferedReader(InputStreamReader(connection.getInputStream()))
            val ip = reader.readLine()
            reader.close()
            IpAddress(ip)
        } catch (e: Exception) {
            e.printStackTrace()
            IpAddress("Unavailable")
        }
    }
}
