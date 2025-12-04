package com.rudra.ispnetworktool.presentation.port_reference

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class TcpUdpPortReferenceViewModel : ViewModel() {

    var state by mutableStateOf(TcpUdpPortReferenceState())
        private set

    init {
        loadPorts()
    }

    fun onSearchQueryChange(query: String) {
        state = state.copy(searchQuery = query)
    }

    private fun loadPorts() {
        val ports = listOf(
            PortInfo(20, "FTP", "TCP", "File Transfer Protocol (Data)"),
            PortInfo(21, "FTP", "TCP", "File Transfer Protocol (Control)"),
            PortInfo(22, "SSH", "TCP", "Secure Shell"),
            PortInfo(23, "Telnet", "TCP", "Telnet remote login service"),
            PortInfo(25, "SMTP", "TCP", "Simple Mail Transfer Protocol"),
            PortInfo(53, "DNS", "UDP/TCP", "Domain Name System"),
            PortInfo(80, "HTTP", "TCP", "Hypertext Transfer Protocol"),
            PortInfo(110, "POP3", "TCP", "Post Office Protocol v3"),
            PortInfo(143, "IMAP", "TCP", "Internet Message Access Protocol"),
            PortInfo(443, "HTTPS", "TCP", "HTTP Secure"),
            PortInfo(3389, "RDP", "TCP", "Remote Desktop Protocol")
        )
        state = state.copy(ports = ports)
    }
}
