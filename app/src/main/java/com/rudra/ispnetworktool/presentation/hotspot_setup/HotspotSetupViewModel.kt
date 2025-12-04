package com.rudra.ispnetworktool.presentation.hotspot_setup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class HotspotSetupViewModel : ViewModel() {

    var state by mutableStateOf(HotspotSetupState())
        private set

    fun updateSsid(ssid: String) {
        val error = if (ssid.isBlank()) "SSID cannot be empty"
        else if (ssid.length < 2) "SSID too short"
        else if (ssid.length > 32) "SSID too long"
        else null

        state = state.copy(
            ssid = ssid,
            ssidError = error
        )
    }

    fun updatePassword(password: String) {
        val strength = calculatePasswordStrength(password)
        val error = if (state.securityType != SecurityType.OPEN && password.length < 8) {
            "Password must be at least 8 characters"
        } else null

        state = state.copy(
            password = password,
            passwordError = error,
            passwordStrength = strength
        )
    }

    fun updateSecurityType(securityType: SecurityType) {
        state = state.copy(securityType = securityType)
        // Re-validate password when security type changes
        if (securityType != SecurityType.OPEN) {
            updatePassword(state.password)
        }
    }

    fun updateBand(band: WifiBand) {
        val channels = when (band) {
            WifiBand.TWO_POINT_FOUR_GHZ -> listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14)
            WifiBand.FIVE_GHZ -> listOf(36, 40, 44, 48, 52, 56, 60, 64, 100, 104, 108, 112, 116, 120, 124, 128, 132, 136, 140, 144, 149, 153, 157, 161, 165)
        }
        state = state.copy(
            band = band,
            availableChannels = channels,
            channel = channels.firstOrNull() ?: 1
        )
    }

    fun updateChannel(channel: Int) {
        state = state.copy(channel = channel)
    }

    fun updateMaxClients(maxClients: Int) {
        state = state.copy(maxClients = maxClients)
    }

    fun updateHidden(hidden: Boolean) {
        state = state.copy(hidden = hidden)
    }

    fun updateAutoShutdown(autoShutdown: Boolean) {
        state = state.copy(autoShutdown = autoShutdown)
    }

    fun startHotspot() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)

            try {
                // Simulate hotspot startup
                delay(2000)

                // For demo, randomly determine if startup succeeds
                val success = Math.random() > 0.1

                if (success) {
                    state = state.copy(
                        hotspotEnabled = true,
                        isLoading = false,
                        connectedClients = emptyList(),
                        uptime = "00:00:00"
                    )

                    // Start simulating connected clients
                    simulateClientConnections()
                } else {
                    state = state.copy(
                        isLoading = false,
                        error = "Failed to start hotspot. Check device permissions."
                    )
                }
            } catch (e: Exception) {
                state = state.copy(
                    isLoading = false,
                    error = "Error starting hotspot: ${e.message}"
                )
            }
        }
    }

    fun stopHotspot() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            delay(1000) // Simulate shutdown
            state = state.copy(
                hotspotEnabled = false,
                isLoading = false,
                connectedClients = emptyList()
            )
        }
    }

    fun refreshStatus() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            delay(500)
            state = state.copy(isLoading = false)
        }
    }

    fun applyTemplate(template: HotspotTemplate) {
        when (template) {
            HotspotTemplate.HOME -> {
                updateSsid("HomeNetwork")
                updateSecurityType(SecurityType.WPA2)
                updatePassword("SecureHome123")
                updateBand(WifiBand.TWO_POINT_FOUR_GHZ)
                updateHidden(false)
            }
            HotspotTemplate.GUEST -> {
                updateSsid("GuestWiFi")
                updateSecurityType(SecurityType.OPEN)
                updatePassword("")
                updateBand(WifiBand.TWO_POINT_FOUR_GHZ)
                updateMaxClients(3)
            }
            HotspotTemplate.SECURE -> {
                updateSsid("SecureNet")
                updateSecurityType(SecurityType.WPA3)
                updatePassword("VeryStrongPassword123!")
                updateBand(WifiBand.FIVE_GHZ)
                updateHidden(true)
            }
        }
    }

    private fun calculatePasswordStrength(password: String): PasswordStrength {
        if (password.isEmpty()) return PasswordStrength.NONE

        var strength = 0
        if (password.length >= 8) strength++
        if (password.any { it.isUpperCase() }) strength++
        if (password.any { it.isLowerCase() }) strength++
        if (password.any { it.isDigit() }) strength++
        if (password.any { !it.isLetterOrDigit() }) strength++

        return when (strength) {
            1 -> PasswordStrength.WEAK
            2, 3 -> PasswordStrength.MEDIUM
            4 -> PasswordStrength.STRONG
            5 -> PasswordStrength.VERY_STRONG
            else -> PasswordStrength.WEAK
        }
    }

    private fun simulateClientConnections() {
        viewModelScope.launch {
            while (state.hotspotEnabled) {
                delay(10000) // Check every 10 seconds

                // Randomly add or remove clients for simulation
                val currentClients = state.connectedClients.toMutableList()

                if (currentClients.size < state.maxClients && Math.random() > 0.7) {
                    // Add a new client
                    val newClient = ConnectedClient(
                        deviceName = "Device_${(1000..9999).random()}",
                        ipAddress = "192.168.43.${(100..199).random()}",
                        macAddress = "AA:BB:CC:${(10..99).random()}:${(10..99).random()}:${(10..99).random()}",
                        connectionTime = "${(0..23).random()}h ${(0..59).random()}m"
                    )
                    currentClients.add(newClient)
                } else if (currentClients.isNotEmpty() && Math.random() > 0.8) {
                    // Remove a random client
                    currentClients.removeAt((0 until currentClients.size).random())
                }

                state = state.copy(connectedClients = currentClients)
            }
        }
    }
}