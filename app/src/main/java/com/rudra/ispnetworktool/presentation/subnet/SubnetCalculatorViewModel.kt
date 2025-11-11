package com.rudra.ispnetworktool.presentation.subnet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.ispnetworktool.data.local.ToolLogEntity
import com.rudra.ispnetworktool.data.models.SubnetInfo
import com.rudra.ispnetworktool.domain.repository.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubnetCalculatorViewModel @Inject constructor(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubnetCalculatorScreenState())
    val uiState = _uiState.asStateFlow()

    private val _errorFlow = MutableSharedFlow<String>()
    val errorFlow = _errorFlow.asSharedFlow()

    private var ipAddress: String? = null
    private var cidr: Int? = null

    fun calculateSubnet(ipAddress: String, cidr: Int) {
        this.ipAddress = ipAddress
        this.cidr = cidr
        try {
            val ip = ipToInt(ipAddress)
            val mask = (-1 shl (32 - cidr))
            val network = ip and mask
            val broadcast = network or mask.inv()

            val networkAddress = intToIp(network)
            val broadcastAddress = intToIp(broadcast)
            val netmask = intToIp(mask)
            val firstHost = intToIp(network + 1)
            val lastHost = intToIp(broadcast - 1)
            val totalHosts = (1 shl (32 - cidr)) - 2

            val subnetInfo = SubnetInfo(
                networkAddress = networkAddress,
                broadcastAddress = broadcastAddress,
                netmask = netmask,
                hostRange = "$firstHost - $lastHost",
                totalHosts = if (totalHosts > 0) totalHosts else 0
            )
            _uiState.value = SubnetCalculatorScreenState(subnetInfo = subnetInfo)
        } catch (e: Exception) {
            viewModelScope.launch { 
                _errorFlow.emit(e.message ?: "Invalid input")
            }
        }
    }

    fun saveResult() {
        viewModelScope.launch {
            val subnetInfo = _uiState.value.subnetInfo
            if (subnetInfo != null) {
                val log = ToolLogEntity(
                    toolType = "Subnet Calculator",
                    target = "$ipAddress/$cidr",
                    timestamp = System.currentTimeMillis(),
                    summary = "${subnetInfo.totalHosts} hosts",
                    resultJson = ""
                )
                historyRepository.saveLog(log)
                _errorFlow.emit("Saved to history")
            }
        }
    }

    fun shareResult(shareText: (String) -> Unit) {
        val subnetInfo = _uiState.value.subnetInfo
        if (subnetInfo != null) {
            val text = "Subnet details for $ipAddress/$cidr:\n" +
                    "Network Address: ${subnetInfo.networkAddress}\n" +
                    "Broadcast Address: ${subnetInfo.broadcastAddress}\n" +
                    "Netmask: ${subnetInfo.netmask}\n" +
                    "Host Range: ${subnetInfo.hostRange}\n" +
                    "Total Usable Hosts: ${subnetInfo.totalHosts}"
            shareText(text)
        }
    }

    private fun ipToInt(ip: String): Int {
        val parts = ip.split(".").map { it.toInt() }
        if (parts.size != 4) throw IllegalArgumentException("Invalid IP address")
        return (parts[0] shl 24) or (parts[1] shl 16) or (parts[2] shl 8) or parts[3]
    }

    private fun intToIp(ip: Int): String {
        return "${(ip shr 24) and 0xFF}.${(ip shr 16) and 0xFF}.${(ip shr 8) and 0xFF}.${ip and 0xFF}"
    }
}
