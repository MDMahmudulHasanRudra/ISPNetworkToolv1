package com.rudra.ispnetworktool.presentation.firewall_rule_generator

import androidx.compose.ui.graphics.Color

data class FirewallRuleGeneratorState(
    val ruleType: RuleType = RuleType.INPUT,
    val sourceIp: String = "",
    val destinationIp: String = "",
    val ports: String = "",
    val protocol: Protocol = Protocol.TCP,
    val action: RuleAction = RuleAction.ALLOW,
    val interfaceName: String = "",
    val description: String = "",
    val enableLogging: Boolean = false,
    val enabled: Boolean = true,
    val selectedFirewall: FirewallType = FirewallType.IPTABLES,
    val generatedRules: List<FirewallRule> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val isValidConfiguration: Boolean
        get() = sourceIp.isNotBlank() || destinationIp.isNotBlank() || ports.isNotBlank()
}

enum class RuleType(val displayName: String) {
    INPUT("Input"),
    OUTPUT("Output"),
    FORWARD("Forward")
}

enum class Protocol(val displayName: String) {
    TCP("TCP"),
    UDP("UDP"),
    ICMP("ICMP"),
    ALL("All")
}

enum class RuleAction(val displayName: String, val color: Color) {
    ALLOW("Allow", Color(0xFF4CAF50)),
    DENY("Deny", Color(0xFFF44336)),
    DROP("Drop", Color(0xFF9C27B0)),
    REJECT("Reject", Color(0xFFFF9800))
}

enum class FirewallType(val displayName: String) {
    IPTABLES("iptables"),
    FIREWALLD("firewalld"),
    UFW("UFW"),
    WINDOWS("Windows Firewall"),
    CISCO("Cisco ASA")
}

data class FirewallRule(
    val ruleType: RuleType,
    val sourceIp: String,
    val destinationIp: String,
    val ports: String,
    val protocol: Protocol,
    val action: RuleAction,
    val interfaceName: String,
    val description: String,
    val enableLogging: Boolean,
    val enabled: Boolean,
    val command: String,
    val firewallType: FirewallType
)