package com.rudra.ispnetworktool.data.repository

import com.rudra.ispnetworktool.domain.model.BDIXNode
import com.rudra.ispnetworktool.domain.model.BDIXPingResult
import com.rudra.ispnetworktool.domain.model.HealthMetrics
import com.rudra.ispnetworktool.domain.model.ISPInfo
import com.rudra.ispnetworktool.domain.model.NodeStatus
import com.rudra.ispnetworktool.domain.repository.PingRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import com.rudra.ispnetworktool.data.models.PingResult as DataPingResult

class BDIXRepositoryImpl @Inject constructor(
    private val pingRepository: PingRepository
) {
    val bdixNodes = listOf(
        BDIXNode("1", "BDIX Main (BTCL)", "103.6.156.1", "Kawran Bazar, Dhaka", 23.7545, 90.3845, 250, "200Gbps"),
        BDIXNode("2", "BDIX Mirpur", "103.6.156.2", "Mirpur, Dhaka", 23.8067, 90.3683, 180, "100Gbps"),
        BDIXNode("3", "BDIX Chittagong", "103.204.81.1", "Agrabad, CTG", 22.3242, 91.8123, 120, "50Gbps"),
        BDIXNode("4", "BDIX Sylhet", "103.204.82.1", "Zindabazar, Sylhet", 24.8949, 91.8687, 60, "20Gbps")
    )

    suspend fun pingNode(nodeId: String): BDIXPingResult {
        val node = bdixNodes.find { it.id == nodeId } ?: return BDIXPingResult(nodeId, 0, 0.0, 100.0, 0.0, false)
        
        // Using existing ping repository. We take the first 4 pings to get an average.
        var totalRtt = 0f
        var count = 0
        var success = false
        
        // This is a simplified way to get a result from a flow for a single check
        try {
            pingRepository.ping(node.ipAddress, 4).collect { result ->
                if (result is DataPingResult.Success) {
                    totalRtt += result.rtt
                    count++
                    success = true
                }
            }
        } catch (e: Exception) {
            success = false
        }

        return BDIXPingResult(
            nodeId = nodeId,
            timestamp = System.currentTimeMillis(),
            latencyMs = if (count > 0) (totalRtt / count).toDouble() else 0.0,
            packetLoss = if (count > 0) 0.0 else 100.0,
            jitter = 0.0, // Existing ping repo doesn't provide jitter easily
            isSuccess = success
        )
    }

    fun getHealthMetrics(nodeId: String): HealthMetrics {
        return HealthMetrics(nodeId, 0.5, 5.0, 99.9, 95)
    }

    fun getISPConnectivity(ispName: String): ISPInfo? {
        // Mock data
        return null
    }
}
