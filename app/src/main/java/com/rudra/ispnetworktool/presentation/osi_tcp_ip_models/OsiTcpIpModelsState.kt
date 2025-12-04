package com.rudra.ispnetworktool.presentation.osi_tcp_ip_models

data class OsiTcpIpModelsState(
    val selectedTabIndex: Int = 0,
    val osiLayers: List<OsiAndTcpIpLayer> = emptyList(),
    val tcpIpLayers: List<OsiAndTcpIpLayer> = emptyList()
)

data class OsiAndTcpIpLayer(
    val layerNumber: Int,
    val name: String,
    val description: String,
    val devices: String,
    val protocols: String
)
