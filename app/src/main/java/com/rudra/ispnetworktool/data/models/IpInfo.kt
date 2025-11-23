package com.rudra.ispnetworktool.data.models

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
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
