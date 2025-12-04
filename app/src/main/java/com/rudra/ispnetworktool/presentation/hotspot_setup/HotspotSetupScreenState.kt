package com.rudra.ispnetworktool.presentation.hotspot_setup

data class HotspotSetupState(
    val hotspotEnabled: Boolean = false,
    val ssid: String = "MyHotspot",
    val password: String = "",
    val securityType: SecurityType = SecurityType.WPA2,
    val band: WifiBand = WifiBand.TWO_POINT_FOUR_GHZ,
    val channel: Int = 6,
    val availableChannels: List<Int> = listOf(1, 6, 11),
    val maxClients: Int = 5,
    val hidden: Boolean = false,
    val autoShutdown: Boolean = false,
    val connectedClients: List<ConnectedClient> = emptyList(),
    val uptime: String = "00:00:00",
    val dataUsage: String = "0 MB",
    val isLoading: Boolean = false,
    val error: String? = null,
    val ssidError: String? = null,
    val passwordError: String? = null,
    val passwordStrength: PasswordStrength = PasswordStrength.NONE
) {
    val isConfigurationValid: Boolean
        get() = ssid.isNotBlank() &&
                ssidError == null &&
                passwordError == null &&
                (securityType == SecurityType.OPEN || password.length >= 8)
}

enum class SecurityType(val displayName: String) {
    OPEN("Open"),
    WPA("WPA"),
    WPA2("WPA2"),
    WPA3("WPA3")
}

enum class WifiBand(val displayName: String) {
    TWO_POINT_FOUR_GHZ("2.4 GHz"),
    FIVE_GHZ("5 GHz")
}

enum class PasswordStrength(val strength: Float) {
    NONE(0f),
    WEAK(0.25f),
    MEDIUM(0.5f),
    STRONG(0.75f),
    VERY_STRONG(1f)
}

enum class HotspotTemplate {
    HOME, GUEST, SECURE
}

data class ConnectedClient(
    val deviceName: String,
    val ipAddress: String,
    val macAddress: String,
    val connectionTime: String,
    val dataUsed: String = "0 MB"
)