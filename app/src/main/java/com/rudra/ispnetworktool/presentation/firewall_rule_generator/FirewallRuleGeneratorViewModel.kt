package com.rudra.ispnetworktool.presentation.firewall_rule_generator

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
class FirewallRuleGeneratorViewModel @Inject constructor() : ViewModel() {

    var state by mutableStateOf(FirewallRuleGeneratorState())
        private set

    fun updateRuleType(ruleType: RuleType) {
        state = state.copy(ruleType = ruleType)
    }

    fun updateSourceIp(ip: String) {
        state = state.copy(sourceIp = ip)
    }

    fun updateDestinationIp(ip: String) {
        state = state.copy(destinationIp = ip)
    }

    fun updatePorts(ports: String) {
        state = state.copy(ports = ports)
    }

    fun updateProtocol(protocol: Protocol) {
        state = state.copy(protocol = protocol)
    }

    fun updateAction(action: RuleAction) {
        state = state.copy(action = action)
    }

    fun updateInterface(interfaceName: String) {
        state = state.copy(interfaceName = interfaceName)
    }

    fun updateDescription(description: String) {
        state = state.copy(description = description)
    }

    fun toggleLogging() {
        state = state.copy(enableLogging = !state.enableLogging)
    }

    fun toggleEnabled() {
        state = state.copy(enabled = !state.enabled)
    }

    fun updateFirewallType(firewallType: FirewallType) {
        state = state.copy(selectedFirewall = firewallType)
    }

    fun generateRule() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)

            try {
                delay(500) // Simulate processing

                val newRule = createFirewallRule()
                state = state.copy(
                    generatedRules = state.generatedRules + newRule,
                    isLoading = false
                )
            } catch (e: Exception) {
                state = state.copy(
                    isLoading = false,
                    error = "Failed to generate rule: ${e.message}"
                )
            }
        }
    }

    fun deleteRule(index: Int) {
        val newRules = state.generatedRules.toMutableList().apply {
            removeAt(index)
        }
        state = state.copy(generatedRules = newRules)
    }

    fun clearAllRules() {
        state = state.copy(generatedRules = emptyList())
    }

    fun copyRuleToClipboard(rule: FirewallRule) {
        // Implementation for copying to clipboard
    }

    fun exportRules() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            delay(1000) // Simulate export
            state = state.copy(isLoading = false)
            // Handle export logic
        }
    }

    private fun createFirewallRule(): FirewallRule {
        // Generate the actual firewall command based on configuration
        val command = generateFirewallCommand()

        return FirewallRule(
            ruleType = state.ruleType,
            sourceIp = state.sourceIp,
            destinationIp = state.destinationIp,
            ports = state.ports,
            protocol = state.protocol,
            action = state.action,
            interfaceName = state.interfaceName,
            description = state.description,
            enableLogging = state.enableLogging,
            enabled = state.enabled,
            command = command,
            firewallType = state.selectedFirewall
        )
    }

    private fun generateFirewallCommand(): String {
        // This would generate actual firewall commands based on the selected firewall type
        return when (state.selectedFirewall) {
            FirewallType.IPTABLES -> generateIptablesCommand()
            FirewallType.FIREWALLD -> generateFirewalldCommand()
            FirewallType.UFW -> generateUfwCommand()
            FirewallType.WINDOWS -> generateWindowsFirewallCommand()
            FirewallType.CISCO -> generateCiscoCommand()
        }
    }

    private fun generateIptablesCommand(): String {
        val sb = StringBuilder("iptables ")

        when (state.ruleType) {
            RuleType.INPUT -> sb.append("-A INPUT ")
            RuleType.OUTPUT -> sb.append("-A OUTPUT ")
            RuleType.FORWARD -> sb.append("-A FORWARD ")
        }

        if (state.sourceIp.isNotBlank()) {
            sb.append("-s ${state.sourceIp} ")
        }

        if (state.destinationIp.isNotBlank()) {
            sb.append("-d ${state.destinationIp} ")
        }

        if (state.protocol != Protocol.ALL) {
            sb.append("-p ${state.protocol.displayName.lowercase()} ")
        }

        if (state.ports.isNotBlank()) {
            sb.append("--dport ${state.ports} ")
        }

        if (state.interfaceName.isNotBlank()) {
            sb.append("-i ${state.interfaceName} ")
        }

        sb.append("-j ${state.action.displayName.uppercase()}")

        return sb.toString()
    }

    private fun generateFirewalldCommand(): String = "firewall-cmd --add-rich-rule='...'"
    private fun generateUfwCommand(): String = "ufw ${state.action.displayName.lowercase()} ..."
    private fun generateWindowsFirewallCommand(): String = "New-NetFirewallRule ..."
    private fun generateCiscoCommand(): String = "access-list ..."
}