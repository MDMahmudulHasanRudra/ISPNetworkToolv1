package com.rudra.ispnetworktool.domain.model

data class PackageInput(
    val speedMbps: Int,
    val users: Int
)

data class ServiceSelection(
    val ipt: Boolean = true,
    val cdn: Boolean = true,
    val baishan: Boolean = false,
    val ggc: Boolean = true,
    val fna: Boolean = true,
    val bdix: Boolean = true
)

data class BandwidthResult(
    val ipt: Double = 0.0,
    val cdn: Double = 0.0,
    val baishan: Double = 0.0,
    val ggc: Double = 0.0,
    val fna: Double = 0.0,
    val bdix: Double = 0.0,
    val total: Double = 0.0,
    val totalSubscribers: Int = 0
)
