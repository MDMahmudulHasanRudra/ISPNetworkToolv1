package com.rudra.ispnetworktool.domain.model

data class BDIXNode(
    val id: String,
    val name: String,
    val ipAddress: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val connectedISPs: Int,
    val capacity: String,
    val category: String,
    val status: NodeStatus = NodeStatus.UNKNOWN
)

enum class NodeStatus {
    HEALTHY, DEGRADED, DOWN, UNKNOWN
}

data class BDIXPingResult(
    val nodeId: String,
    val timestamp: Long,
    val latencyMs: Double,
    val packetLoss: Double,
    val jitter: Double,
    val isSuccess: Boolean
)

data class BDIXRoute(
    val target: String,
    val hops: List<BDIXRouteHop>,
    val totalLatency: Double,
    val containsBDIX: Boolean
)

data class BDIXRouteHop(
    val ip: String,
    val hostname: String?,
    val latency: Double,
    val isBDIX: Boolean = false,
    val bdixNodeName: String? = null
)

data class ISPInfo(
    val name: String,
    val tier: Int,
    val asn: String,
    val bdixConnections: List<ISPConnection>,
    val bandwidth: Map<String, String> // BDIX Node -> Bandwidth
)

data class ISPConnection(
    val nodeId: String,
    val capacity: String,
    val isPrimary: Boolean
)

data class HealthMetrics(
    val nodeId: String,
    val packetLoss24h: Double,
    val averageLatency24h: Double,
    val uptime24h: Double,
    val healthScore: Int
)
