package com.rudra.ispnetworktool.data.models

data class SubnetInfo(
    val networkAddress: String = "",
    val broadcastAddress: String = "",
    val netmask: String = "",
    val hostRange: String = "",
    val totalHosts: Int = 0
)
