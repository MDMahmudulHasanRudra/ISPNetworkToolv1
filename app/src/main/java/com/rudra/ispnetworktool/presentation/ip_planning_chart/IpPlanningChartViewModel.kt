package com.rudra.ispnetworktool.presentation.ip_planning_chart

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IpPlanningChartViewModel @Inject constructor() : ViewModel() {

    var state by mutableStateOf(IpPlanningChartState())
        private set

    fun updateBaseNetwork(network: String) {
        val error = validateCidr(network)
        state = state.copy(
            baseNetwork = network,
            baseNetworkError = error
        )
    }

    fun updateSubnetPrefix(prefix: Int) {
        val error = if (prefix !in 8..30) "Prefix must be between 8 and 30" else null
        state = state.copy(
            subnetPrefix = prefix,
            subnetPrefixError = error
        )
    }

    fun addNetworkSegment() {
        val newSegment = NetworkSegment(
            name = "New Segment ${state.networkSegments.size + 1}",
            requiredHosts = 10,
            description = "New network segment"
        )
        state = state.copy(
            networkSegments = state.networkSegments + newSegment
        )
    }

    fun updateNetworkSegment(index: Int, segment: NetworkSegment) {
        val updatedSegments = state.networkSegments.toMutableList().apply {
            this[index] = segment
        }
        state = state.copy(networkSegments = updatedSegments)
    }

    fun removeNetworkSegment(index: Int) {
        val updatedSegments = state.networkSegments.toMutableList().apply {
            removeAt(index)
        }
        state = state.copy(networkSegments = updatedSegments)
    }

    fun updatePlanningStrategy(strategy: PlanningStrategy) {
        state = state.copy(planningStrategy = strategy)
    }

    fun updateReserveInfrastructureIps(reserve: Boolean) {
        state = state.copy(reserveInfrastructureIps = reserve)
    }

    fun updateGrowthBuffer(buffer: Int) {
        state = state.copy(growthBuffer = buffer)
    }

    fun updateIncludeVlans(include: Boolean) {
        state = state.copy(includeVlans = include)
    }

    fun generatePlan() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)

            try {
                delay(1000) // Simulate planning calculation

                val plan = calculateNetworkPlan()
                state = state.copy(
                    networkPlans = state.networkPlans + plan,
                    isLoading = false
                )
            } catch (e: Exception) {
                state = state.copy(
                    isLoading = false,
                    error = "Failed to generate plan: ${e.message}"
                )
            }
        }
    }

    fun deletePlan(index: Int) {
        val updatedPlans = state.networkPlans.toMutableList().apply {
            removeAt(index)
        }
        state = state.copy(networkPlans = updatedPlans)
    }

    fun clearAllPlans() {
        state = state.copy(networkPlans = emptyList())
    }

    fun copyPlanToClipboard(plan: NetworkPlan) {
        // Implementation for copying to clipboard
    }

    fun exportPlan() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            delay(500) // Simulate export
            state = state.copy(isLoading = false)
            // Handle export logic
        }
    }

    private fun validateCidr(cidr: String): String? {
        val cidrRegex = """^(\d{1,3}\.){3}\d{1,3}/\d{1,2}$""".toRegex()
        if (!cidrRegex.matches(cidr)) {
            return "Invalid CIDR format. Use: 192.168.0.0/16"
        }

        val parts = cidr.split("/")
        val prefix = parts[1].toIntOrNull() ?: return "Invalid prefix"
        if (prefix !in 8..30) return "Prefix must be between 8 and 30"

        return null
    }

    private fun calculateNetworkPlan(): NetworkPlan {
        // This is a simplified implementation
        // In a real app, you would implement proper subnet calculation logic

        val segments = state.networkSegments.mapIndexed { index, segment ->
            val assignedHosts = (segment.requiredHosts * (1 + state.growthBuffer / 100.0)).toInt()
            val subnet = calculateSubnetForHosts(assignedHosts)

            NetworkSegmentPlan(
                name = segment.name,
                requiredHosts = segment.requiredHosts,
                assignedHosts = assignedHosts,
                subnet = subnet,
                subnetMask = "255.255.255.0",
                networkAddress = "192.168.${index + 1}.0",
                broadcastAddress = "192.168.${index + 1}.255",
                firstUsable = "192.168.${index + 1}.1",
                lastUsable = "192.168.${index + 1}.254",
                utilization = segment.requiredHosts.toFloat() / assignedHosts,
                vlanId = if (state.includeVlans) index + 10 else null
            )
        }

        val totalUsable = segments.sumOf { it.assignedHosts }
        val totalAllocated = segments.sumOf { it.requiredHosts }
        val utilization = (totalAllocated.toFloat() / totalUsable * 100).toInt()

        return NetworkPlan(
            baseNetwork = state.baseNetwork,
            planningStrategy = state.planningStrategy,
            segments = segments,
            utilizationPercentage = utilization,
            totalUsableHosts = totalUsable,
            totalAllocatedHosts = totalAllocated
        )
    }

    private fun calculateSubnetForHosts(hosts: Int): String {
        // Simplified subnet calculation
        return when {
            hosts <= 30 -> "/27"    // 30 hosts
            hosts <= 62 -> "/26"    // 62 hosts
            hosts <= 126 -> "/25"   // 126 hosts
            hosts <= 254 -> "/24"   // 254 hosts
            hosts <= 510 -> "/23"   // 510 hosts
            hosts <= 1022 -> "/22"  // 1022 hosts
            hosts <= 2046 -> "/21"  // 2046 hosts
            hosts <= 4094 -> "/20"  // 4094 hosts
            else -> "/19"           // 8190 hosts
        }
    }
}