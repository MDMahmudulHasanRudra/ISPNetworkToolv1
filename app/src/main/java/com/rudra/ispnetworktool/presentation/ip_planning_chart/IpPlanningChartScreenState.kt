package com.rudra.ispnetworktool.presentation.ip_planning_chart

data class IpPlanningChartState(
    val baseNetwork: String = "192.168.0.0/16",
    val subnetPrefix: Int = 24,
    val networkSegments: List<NetworkSegment> = listOf(
        NetworkSegment("Servers", 50, "Critical infrastructure servers"),
        NetworkSegment("Users", 200, "Employee workstations"),
        NetworkSegment("IoT", 30, "Smart devices and IoT")
    ),
    val planningStrategy: PlanningStrategy = PlanningStrategy.EFFICIENT,
    val reserveInfrastructureIps: Boolean = true,
    val growthBuffer: Int = 20,
    val includeVlans: Boolean = false,
    val networkPlans: List<NetworkPlan> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val baseNetworkError: String? = null,
    val subnetPrefixError: String? = null
) {
    val isValidConfiguration: Boolean
        get() = baseNetworkError == null &&
                subnetPrefixError == null &&
                networkSegments.isNotEmpty() &&
                networkSegments.all { it.requiredHosts > 0 && it.name.isNotBlank() }
}

data class NetworkSegment(
    val name: String,
    val requiredHosts: Int,
    val description: String = ""
)

data class NetworkPlan(
    val baseNetwork: String,
    val planningStrategy: PlanningStrategy,
    val segments: List<NetworkSegmentPlan>,
    val utilizationPercentage: Int,
    val totalUsableHosts: Int,
    val totalAllocatedHosts: Int
)

data class NetworkSegmentPlan(
    val name: String,
    val requiredHosts: Int,
    val assignedHosts: Int,
    val subnet: String,
    val subnetMask: String,
    val networkAddress: String,
    val broadcastAddress: String,
    val firstUsable: String,
    val lastUsable: String,
    val utilization: Float,
    val vlanId: Int? = null
)

enum class PlanningStrategy(val displayName: String) {
    EFFICIENT("Most Efficient"),
    SEQUENTIAL("Sequential"),
    HIERARCHICAL("Hierarchical"),
    CUSTOM("Custom")
}