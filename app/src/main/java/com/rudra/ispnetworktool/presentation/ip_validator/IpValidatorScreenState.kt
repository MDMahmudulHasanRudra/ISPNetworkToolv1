package com.rudra.ispnetworktool.presentation.ip_validator

import androidx.compose.ui.graphics.Color

data class IpValidatorScreenState(
    val ipAddress: String = "",
    val isPublic: Boolean? = null,
    val isValid: Boolean? = null,
    val ipType: IpType? = null,
    val networkClass: NetworkClass? = null,
    val validationStatus: ValidationStatus = ValidationStatus.IDLE,
    val additionalInfo: IpAdditionalInfo = IpAdditionalInfo(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val lastUpdated: Long = 0L
) {
    val displayStatus: String
        get() = when {
            isLoading -> "Validating..."
            isValid == true -> "✓ Valid IP Address"
            isValid == false -> "✗ Invalid IP Address"
            else -> "Enter IP to validate"
        }

    val statusColor: Color
        get() = when {
            isLoading -> Color(0xFFFFA000) // Amber for loading
            isValid == true -> Color(0xFF00C853) // Green for valid
            isValid == false -> Color(0xFFDD2C00) // Red for invalid
            else -> Color(0xFF666666) // Gray for idle
        }

    val isComplete: Boolean
        get() = isValid != null && !isLoading && errorMessage == null
}

enum class IpType {
    IPV4,
    IPV6,
    UNKNOWN
}

enum class NetworkClass {
    CLASS_A,    // 1.0.0.0 to 126.255.255.255
    CLASS_B,    // 128.0.0.0 to 191.255.255.255
    CLASS_C,    // 192.0.0.0 to 223.255.255.255
    CLASS_D,    // 224.0.0.0 to 239.255.255.255 (Multicast)
    CLASS_E,    // 240.0.0.0 to 255.255.255.255 (Reserved)
    LOCALHOST,  // 127.0.0.0 to 127.255.255.255
    LINK_LOCAL, // 169.254.0.0 to 169.254.255.255
    PRIVATE,    // Various private ranges
    UNKNOWN
}

enum class ValidationStatus {
    IDLE,
    VALIDATING,
    VALID,
    INVALID,
    ERROR
}

data class IpAdditionalInfo(
    val countryCode: String? = null,
    val isp: String? = null,
    val location: String? = null,
    val reverseDns: String? = null,
    val subnetMask: String? = null,
    val cidrNotation: String? = null,
    val broadcastAddress: String? = null,
    val networkAddress: String? = null,
    val firstUsable: String? = null,
    val lastUsable: String? = null,
    val totalHosts: Long = 0,
    val specialPurpose: SpecialPurpose? = null
)

enum class SpecialPurpose {
    LOOPBACK,           // 127.0.0.0/8
    LINK_LOCAL,         // 169.254.0.0/16
    MULTICAST,          // 224.0.0.0/4
    BROADCAST,          // 255.255.255.255
    DOCUMENTATION,      // 192.0.2.0/24, 198.51.100.0/24, 203.0.113.0/24
    BENCHMARK_TESTING,  // 198.18.0.0/15
    RESERVED,           // Various reserved ranges
    PUBLIC,
    PRIVATE
}

// Extension functions for enhanced functionality
fun IpValidatorScreenState.getConnectionQuality(): ConnectionQuality {
    return when {
        isValid != true -> ConnectionQuality.UNKNOWN
        isPublic == true -> ConnectionQuality.EXCELLENT
        ipType == IpType.IPV6 -> ConnectionQuality.GOOD
        networkClass in listOf(NetworkClass.CLASS_A, NetworkClass.CLASS_B) -> ConnectionQuality.GOOD
        else -> ConnectionQuality.FAIR
    }
}

fun IpValidatorScreenState.getSecurityLevel(): SecurityLevel {
    return when {
        isValid == true -> SecurityLevel.UNKNOWN
        isPublic == false -> SecurityLevel.HIGH
        additionalInfo.specialPurpose == SpecialPurpose.PRIVATE -> SecurityLevel.HIGH
        additionalInfo.specialPurpose == SpecialPurpose.LOOPBACK -> SecurityLevel.VERY_HIGH
        else -> SecurityLevel.MEDIUM
    }
}

fun IpValidatorScreenState.formatDetailedSummary(): String {
    return buildString {
        append("IP Address: $ipAddress\n")
        append("Status: ${if (isValid == true) "Valid" else "Invalid"}\n")
        isPublic?.let { append("Type: ${if (it) "Public" else "Private"}\n") }
        ipType?.let { append("Version: ${it.name}\n") }
        networkClass?.let { append("Network Class: ${it.name}\n") }
        additionalInfo.specialPurpose?.let { append("Purpose: ${it.name}\n") }
        if (additionalInfo.totalHosts > 0) {
            append("Total Hosts: ${additionalInfo.totalHosts}\n")
        }
        additionalInfo.subnetMask?.let { append("Subnet Mask: $it\n") }
    }
}

enum class ConnectionQuality(val displayName: String, val color: Long) {
    EXCELLENT("Excellent", 0xFF00C853),
    GOOD("Good", 0xFF64DD17),
    FAIR("Fair", 0xFFFFD600),
    POOR("Poor", 0xFFFF6D00),
    UNKNOWN("Unknown", 0xFF666666)
}

enum class SecurityLevel(val displayName: String, val color: Long) {
    VERY_HIGH("Very High", 0xFF00C853),
    HIGH("High", 0xFF64DD17),
    MEDIUM("Medium", 0xFFFFD600),
    LOW("Low", 0xFFFF6D00),
    UNKNOWN("Unknown", 0xFF666666)
}

// Helper functions for IP validation and analysis
object IpValidatorUtils {

    fun validateIpv4(ip: String): Boolean {
        val ipv4Regex = """^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$""".toRegex()
        return ipv4Regex.matches(ip)
    }

    fun validateIpv6(ip: String): Boolean {
        val ipv6Regex = """^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$""".toRegex()
        val compressedIpv6Regex = """^(([0-9a-fA-F]{1,4}:){1,7}:|::)([0-9a-fA-F]{1,4}:){0,6}[0-9a-fA-F]{1,4}$""".toRegex()
        return ipv6Regex.matches(ip) || compressedIpv6Regex.matches(ip)
    }

    fun getNetworkClass(ip: String): NetworkClass {
        if (!validateIpv4(ip)) return NetworkClass.UNKNOWN

        val firstOctet = ip.substringBefore('.').toIntOrNull() ?: return NetworkClass.UNKNOWN

        return when (firstOctet) {
            in 1..126 -> NetworkClass.CLASS_A
            in 128..191 -> NetworkClass.CLASS_B
            in 192..223 -> NetworkClass.CLASS_C
            in 224..239 -> NetworkClass.CLASS_D
            in 240..255 -> NetworkClass.CLASS_E
            127 -> NetworkClass.LOCALHOST
            else -> NetworkClass.UNKNOWN
        }
    }

    fun isPrivateIp(ip: String): Boolean {
        if (!validateIpv4(ip)) return false

        return when {
            ip.startsWith("10.") -> true
            ip.startsWith("192.168.") -> true
            ip.startsWith("172.") -> {
                val secondOctet = ip.split('.')[1].toIntOrNull() ?: return false
                secondOctet in 16..31
            }
            ip == "127.0.0.1" -> true
            ip.startsWith("169.254.") -> true // Link-local
            else -> false
        }
    }

    fun getSpecialPurpose(ip: String): SpecialPurpose {
        if (!validateIpv4(ip)) return SpecialPurpose.PUBLIC

        return when {
            ip.startsWith("127.") -> SpecialPurpose.LOOPBACK
            ip.startsWith("169.254.") -> SpecialPurpose.LINK_LOCAL
            ip.startsWith("224.") -> SpecialPurpose.MULTICAST
            ip == "255.255.255.255" -> SpecialPurpose.BROADCAST
            ip.startsWith("192.0.2.") || ip.startsWith("198.51.100.") || ip.startsWith("203.0.113.") ->
                SpecialPurpose.DOCUMENTATION
            ip.startsWith("198.18.") -> SpecialPurpose.BENCHMARK_TESTING
            isPrivateIp(ip) -> SpecialPurpose.PRIVATE
            else -> SpecialPurpose.PUBLIC
        }
    }

    fun calculateSubnetInfo(ip: String, cidr: Int = 24): IpAdditionalInfo {
        if (!validateIpv4(ip) || cidr !in 0..32) return IpAdditionalInfo()

        // Simplified subnet calculation (for /24 by default)
        val networkPart = ip.substringBeforeLast('.') + ".0"
        val broadcast = ip.substringBeforeLast('.') + ".255"
        val firstUsable = ip.substringBeforeLast('.') + ".1"
        val lastUsable = ip.substringBeforeLast('.') + ".254"
        val totalHosts = (1L shl (32 - cidr)) - 2

        return IpAdditionalInfo(
            subnetMask = when (cidr) {
                24 -> "255.255.255.0"
                16 -> "255.255.0.0"
                8 -> "255.0.0.0"
                else -> "Custom"
            },
            cidrNotation = "$ip/$cidr",
            broadcastAddress = broadcast,
            networkAddress = networkPart,
            firstUsable = firstUsable,
            lastUsable = lastUsable,
            totalHosts = totalHosts
        )
    }
}

// Sample data for preview and testing
val sampleIpValidatorStates = listOf(
    IpValidatorScreenState(
        ipAddress = "192.168.1.1",
        isPublic = false,
        isValid = true,
        ipType = IpType.IPV4,
        networkClass = NetworkClass.CLASS_C,
        validationStatus = ValidationStatus.VALID,
        additionalInfo = IpAdditionalInfo(
            specialPurpose = SpecialPurpose.PRIVATE,
            subnetMask = "255.255.255.0",
            totalHosts = 254
        )
    ),
    IpValidatorScreenState(
        ipAddress = "8.8.8.8",
        isPublic = true,
        isValid = true,
        ipType = IpType.IPV4,
        networkClass = NetworkClass.CLASS_A,
        validationStatus = ValidationStatus.VALID,
        additionalInfo = IpAdditionalInfo(
            isp = "Google LLC",
            location = "United States",
            specialPurpose = SpecialPurpose.PUBLIC
        )
    ),
    IpValidatorScreenState(
        ipAddress = "invalid.ip.address",
        isValid = false,
        validationStatus = ValidationStatus.INVALID,
        errorMessage = "Invalid IP address format"
    )
)