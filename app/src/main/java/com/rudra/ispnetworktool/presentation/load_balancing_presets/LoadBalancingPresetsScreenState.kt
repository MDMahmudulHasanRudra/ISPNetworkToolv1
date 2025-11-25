package com.rudra.ispnetworktool.presentation.load_balancing_presets

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class LoadBalancingPresetsState(
    val selectedPreset: LoadBalancingPreset? = null,
    val customConfiguration: LoadBalancingConfiguration? = null,
    val customConfigurations: List<LoadBalancingConfiguration> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class LoadBalancingPreset(
    val displayName: String,
    val description: String,
    val icon: ImageVector,
    val tags: List<String>,
    val complexity: ComplexityLevel,
    val performance: PerformanceLevel,
    val reliability: ReliabilityLevel,
    val configuration: LoadBalancingConfiguration
) {
    ROUND_ROBIN(
        displayName = "Round Robin",
        description = "Distributes requests equally to each server in sequence",
        icon = Icons.Default.Repeat,
        tags = listOf("Simple", "Equal", "Basic"),
        complexity = ComplexityLevel.LOW,
        performance = PerformanceLevel.MEDIUM,
        reliability = ReliabilityLevel.MEDIUM,
        configuration = LoadBalancingConfiguration(
            name = "Round Robin",
            description = "Simple equal distribution algorithm",
            algorithm = LoadBalancingAlgorithm.ROUND_ROBIN,
            healthCheckConfig = HealthCheckConfig(HealthCheckType.TCP, 5000, 30000),
            sessionPersistence = SessionPersistence.NONE,
            backupServers = 1,
            timeoutMs = 30000,
            retryAttempts = 3,
            commands = listOf(
                LoadBalancerCommand(
                    platform = LoadBalancerPlatform.NGINX,
                    command = "upstream backend { server 192.168.1.10; server 192.168.1.11; }"
                )
            )
        )
    ),
    LEAST_CONNECTIONS(
        displayName = "Least Connections",
        description = "Sends requests to the server with the fewest active connections",
        icon = Icons.Default.TrendingDown,
        tags = listOf("Dynamic", "Efficient", "Adaptive"),
        complexity = ComplexityLevel.MEDIUM,
        performance = PerformanceLevel.HIGH,
        reliability = ReliabilityLevel.HIGH,
        configuration = LoadBalancingConfiguration(
            name = "Least Connections",
            description = "Dynamic distribution based on current server load",
            algorithm = LoadBalancingAlgorithm.LEAST_CONNECTIONS,
            healthCheckConfig = HealthCheckConfig(HealthCheckType.HTTP, 3000, 15000),
            sessionPersistence = SessionPersistence.COOKIE,
            backupServers = 2,
            timeoutMs = 45000,
            retryAttempts = 2,
            commands = emptyList()
        )
    ),
    IP_HASH(
        displayName = "IP Hash",
        description = "Distributes requests based on client IP address for session consistency",
        icon = Icons.Default.Fingerprint,
        tags = listOf("Sticky", "Consistent", "Session"),
        complexity = ComplexityLevel.MEDIUM,
        performance = PerformanceLevel.MEDIUM,
        reliability = ReliabilityLevel.HIGH,
        configuration = LoadBalancingConfiguration(
            name = "IP Hash",
            description = "Session persistence through client IP hashing",
            algorithm = LoadBalancingAlgorithm.IP_HASH,
            healthCheckConfig = HealthCheckConfig(HealthCheckType.TCP, 5000, 30000),
            sessionPersistence = SessionPersistence.IP_BASED,
            backupServers = 1,
            timeoutMs = 60000,
            retryAttempts = 1,
            commands = emptyList()
        )
    ),
    WEIGHTED_ROUND_ROBIN(
        displayName = "Weighted Round Robin",
        description = "Distributes requests based on server capacity weights",
        icon = Icons.Default.AutoAwesome,
        tags = listOf("Weighted", "Capacity", "Advanced"),
        complexity = ComplexityLevel.HIGH,
        performance = PerformanceLevel.HIGH,
        reliability = ReliabilityLevel.HIGH,
        configuration = LoadBalancingConfiguration(
            name = "Weighted Round Robin",
            description = "Capacity-aware distribution with server weighting",
            algorithm = LoadBalancingAlgorithm.WEIGHTED_ROUND_ROBIN,
            healthCheckConfig = HealthCheckConfig(HealthCheckType.HTTP, 2000, 10000),
            sessionPersistence = SessionPersistence.SSL,
            backupServers = 3,
            timeoutMs = 30000,
            retryAttempts = 3,
            commands = emptyList()
        )
    )
}

data class LoadBalancingConfiguration(
    val name: String,
    val description: String,
    val algorithm: LoadBalancingAlgorithm,
    val healthCheckConfig: HealthCheckConfig,
    val sessionPersistence: SessionPersistence,
    val backupServers: Int,
    val timeoutMs: Int,
    val retryAttempts: Int,
    val commands: List<LoadBalancerCommand>
)

data class HealthCheckConfig(
    val type: HealthCheckType,
    val intervalMs: Int,
    val timeoutMs: Int
)

data class LoadBalancerCommand(
    val platform: LoadBalancerPlatform,
    val command: String
)

enum class LoadBalancingAlgorithm(
    val displayName: String,
    val description: String
) {
    ROUND_ROBIN("Round Robin", "Equal distribution in circular order"),
    LEAST_CONNECTIONS("Least Connections", "Server with fewest active connections"),
    IP_HASH("IP Hash", "Client IP-based distribution"),
    WEIGHTED_ROUND_ROBIN("Weighted Round Robin", "Capacity-weighted distribution"),
    LEAST_RESPONSE_TIME("Least Response Time", "Server with fastest response time"),
    RANDOM("Random", "Random server selection")
}

enum class HealthCheckType(val displayName: String) {
    TCP("TCP Check"),
    HTTP("HTTP Check"),
    HTTPS("HTTPS Check"),
    ICMP("Ping Check")
}

enum class SessionPersistence(val displayName: String) {
    NONE("No Persistence"),
    COOKIE("Cookie-Based"),
    IP_BASED("IP-Based"),
    SSL("SSL Session ID")
}

enum class LoadBalancerPlatform(val displayName: String) {
    NGINX("Nginx"),
    APACHE("Apache"),
    HA_PROXY("HAProxy"),
    AWS_ELB("AWS ELB"),
    AZURE_LB("Azure Load Balancer")
}

enum class ComplexityLevel(val displayName: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High")
}

enum class PerformanceLevel(val displayName: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High")
}

enum class ReliabilityLevel(val displayName: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High")
}