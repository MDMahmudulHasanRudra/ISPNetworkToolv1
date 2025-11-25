package com.rudra.ispnetworktool.presentation.cidr_visualizer

data class CidrVisualizerScreenState(
    val cidrAddress: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val cidrInfo: CidrInfo? = null
)

data class CidrInfo(
    val cidrNotation: String,
    val networkAddress: String,
    val broadcastAddress: String,
    val firstUsableHost: String,
    val lastUsableHost: String,
    val subnetMask: String,
    val wildcardMask: String,
    val prefixLength: Int,
    val totalHosts: Long,
    val usableHosts: Long,
    val isIpv6: Boolean = false,
    val networkClass: String
)
