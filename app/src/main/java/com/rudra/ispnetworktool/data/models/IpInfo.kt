package com.rudra.ispnetworktool.data.models

import kotlinx.serialization.Serializable

@Serializable
data class IpInfo(
    val publicIp: String = "N/A",
    val isp: String = "N/A",
    val city: String = "N/A",
    val country: String = "N/A",
    val localIp: String = "N/A",
    val gateway: String = "N/A",
    val dnsServers: List<String> = emptyList()
)
