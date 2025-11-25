package com.rudra.ispnetworktool.presentation.port_reference

data class TcpUdpPortReferenceState(
    val ports: List<PortInfo> = emptyList(),
    val searchQuery: String = ""
)

data class PortInfo(
    val portNumber: Int,
    val portName: String,
    val protocol: String,
    val description: String
)
