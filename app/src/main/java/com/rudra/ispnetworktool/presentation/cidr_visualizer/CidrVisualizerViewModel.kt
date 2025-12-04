package com.rudra.ispnetworktool.presentation.cidr_visualizer

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.InetAddress
import javax.inject.Inject

@HiltViewModel
class CidrVisualizerViewModel @Inject constructor() : ViewModel() {

    private val _state = mutableStateOf(CidrVisualizerScreenState())
    val state: State<CidrVisualizerScreenState> = _state

    fun onCidrAddressChanged(address: String) {
        _state.value = _state.value.copy(
            cidrAddress = address.trim(),
            error = if (_state.value.error != null) null else _state.value.error
        )
    }

    fun visualizeCidr() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            try {
                // Simulate processing delay
                delay(500)

                val cidr = _state.value.cidrAddress.trim()

                // Validate CIDR format
                if (!isValidCidrFormat(cidr)) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Invalid CIDR format. Use format: 192.168.1.0/24"
                    )
                    return@launch
                }

                // Parse and calculate CIDR info
                val cidrInfo = calculateCidrInfo(cidr)
                _state.value = _state.value.copy(
                    isLoading = false,
                    cidrInfo = cidrInfo
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Error processing CIDR: ${e.message}"
                )
            }
        }
    }

    private fun isValidCidrFormat(cidr: String): Boolean {
        val cidrRegex = """^(\d{1,3}\.){3}\d{1,3}/\d{1,2}$""".toRegex()
        if (!cidrRegex.matches(cidr)) return false

        val parts = cidr.split("/")
        val ip = parts[0]
        val prefix = parts[1].toInt()

        // Validate IP address components
        val ipParts = ip.split(".")
        if (ipParts.size != 4) return false

        for (part in ipParts) {
            val num = part.toIntOrNull() ?: return false
            if (num !in 0..255) return false
        }

        // Validate prefix length
        if (prefix !in 0..32) return false

        return true
    }

    private fun calculateCidrInfo(cidr: String): CidrInfo {
        val parts = cidr.split("/")
        val ip = parts[0]
        val prefixLength = parts[1].toInt()

        // Convert IP to integer
        val ipBytes = InetAddress.getByName(ip).address
        var ipInt = 0
        for (byte in ipBytes) {
            ipInt = ipInt shl 8 or (byte.toInt() and 0xFF)
        }

        // Calculate subnet mask
        val subnetMask = (0xFFFFFFFFL shl (32 - prefixLength)).toInt() // and 0xFFFFFFFF
        val subnetMaskStr = intToIp(subnetMask)

        // Calculate network address
        val networkAddressInt = ipInt and subnetMask
        val networkAddress = intToIp(networkAddressInt)

        // Calculate broadcast address
        val wildcardMask = subnetMask.inv()// and 0xFFFFFFFF
        val broadcastAddressInt = networkAddressInt or wildcardMask.toInt()
        val broadcastAddress = intToIp(broadcastAddressInt)

        // Calculate host counts
        val totalHosts = (1L shl (32 - prefixLength))
        val usableHosts = if (totalHosts > 2) totalHosts - 2 else 0

        // Calculate first and last usable hosts
        val firstUsableHost = if (totalHosts > 2) intToIp(networkAddressInt + 1) else "N/A"
        val lastUsableHost = if (totalHosts > 2) intToIp(broadcastAddressInt - 1) else "N/A"

        // Calculate wildcard mask
        val wildcardMaskStr = intToIp(wildcardMask.toInt())

        // Determine network class
        val networkClass = determineNetworkClass(ip)

        return CidrInfo(
            cidrNotation = cidr,
            networkAddress = networkAddress,
            broadcastAddress = broadcastAddress,
            firstUsableHost = firstUsableHost,
            lastUsableHost = lastUsableHost,
            subnetMask = subnetMaskStr,
            wildcardMask = wildcardMaskStr,
            prefixLength = prefixLength,
            totalHosts = totalHosts,
            usableHosts = usableHosts,
            networkClass = networkClass,
            isIpv6 = false
        )
    }

    private fun intToIp(ipInt: Int): String {
        return String.format(
            "%d.%d.%d.%d",
            (ipInt ushr 24) and 0xFF,
            (ipInt ushr 16) and 0xFF,
            (ipInt ushr 8) and 0xFF,
            ipInt and 0xFF
        )
    }

    private fun determineNetworkClass(ip: String): String {
        val firstOctet = ip.split(".")[0].toInt()
        return when (firstOctet) {
            in 0..127 -> "Class A"
            in 128..191 -> "Class B"
            in 192..223 -> "Class C"
            in 224..239 -> "Class D (Multicast)"
            in 240..255 -> "Class E (Experimental)"
            else -> "Unknown"
        }
    }
}
