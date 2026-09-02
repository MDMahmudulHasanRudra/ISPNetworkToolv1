package com.rudra.ispnetworktool.presentation.osi_tcp_ip_models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OsiTcpIpModelsViewModel @Inject constructor() : ViewModel() {

    var state by mutableStateOf(OsiTcpIpModelsState())
        private set

    init {
        loadOsiLayers()
        loadTcpIpLayers()
    }

    fun onTabSelected(index: Int) {
        state = state.copy(selectedTabIndex = index)
    }

    private fun loadOsiLayers() {
        val layers = listOf(
            OsiAndTcpIpLayer(7, "Application", "Provides network services to applications.", "Gateways, Firewalls", "HTTP, FTP, SMTP"),
            OsiAndTcpIpLayer(6, "Presentation", "Translates, encrypts, and compresses data.", "Gateways, Firewalls", "SSL, TLS, JPEG"),
            OsiAndTcpIpLayer(5, "Session", "Establishes, manages, and terminates sessions.", "Gateways", "NetBIOS, SAP"),
            OsiAndTcpIpLayer(4, "Transport", "Provides reliable data transfer and error correction.", "Gateways, Firewalls", "TCP, UDP"),
            OsiAndTcpIpLayer(3, "Network", "Handles logical addressing and routing.", "Routers, L3 Switches", "IP, ICMP, ARP"),
            OsiAndTcpIpLayer(2, "Data Link", "Manages physical addressing and access to media.", "Switches, Bridges, NICs", "Ethernet, PPP"),
            OsiAndTcpIpLayer(1, "Physical", "Transmits raw bits over a physical medium.", "Hubs, Repeaters, Cables", "Ethernet, DSL")
        )
        state = state.copy(osiLayers = layers)
    }

    private fun loadTcpIpLayers() {
        val layers = listOf(
            OsiAndTcpIpLayer(4, "Application", "Provides network services to applications.", "Gateways, Firewalls", "HTTP, FTP, SMTP"),
            OsiAndTcpIpLayer(3, "Transport", "Provides reliable data transfer and error correction.", "Gateways, Firewalls", "TCP, UDP"),
            OsiAndTcpIpLayer(2, "Internet", "Handles logical addressing and routing.", "Routers", "IP, ICMP, ARP"),
            OsiAndTcpIpLayer(1, "Network Access", "Manages physical addressing and access to media.", "Switches, Hubs, NICs", "Ethernet, Wi-Fi")
        )
        state = state.copy(tcpIpLayers = layers)
    }
}
