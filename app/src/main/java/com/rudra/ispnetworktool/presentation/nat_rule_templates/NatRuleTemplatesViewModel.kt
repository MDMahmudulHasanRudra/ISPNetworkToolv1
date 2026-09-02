package com.rudra.ispnetworktool.presentation.nat_rule_templates

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NatRuleTemplatesViewModel @Inject constructor() : ViewModel() {

    var state by mutableStateOf(NatRuleTemplatesState())
        private set

    init {
        loadTemplates()
    }

    fun onTemplateSelected(template: NatRuleTemplate) {
        state = state.copy(selectedTemplate = template)
    }

    fun onClearSelection() {
        state = state.copy(selectedTemplate = null)
    }

    private fun loadTemplates() {
        val templates = listOf(
            NatRuleTemplate(
                name = "Port Forwarding",
                description = "Redirect traffic from a public port to a private IP and port.",
                platform = "MikroTik",
                command = "/ip firewall nat add chain=dstnat dst-address=<public_ip> protocol=tcp dst-port=<public_port> action=dst-nat to-addresses=<private_ip> to-ports=<private_port>"
            ),
            NatRuleTemplate(
                name = "Source NAT (Masquerade)",
                description = "Translate private IP addresses to a public IP address.",
                platform = "MikroTik",
                command = "/ip firewall nat add chain=srcnat out-interface=<wan_interface> action=masquerade"
            ),
            NatRuleTemplate(
                name = "Static NAT (1:1)",
                description = "Map a public IP address to a private IP address.",
                platform = "MikroTik",
                command = "/ip firewall nat add chain=dstnat dst-address=<public_ip> action=dst-nat to-addresses=<private_ip>\n/ip firewall nat add chain=srcnat src-address=<private_ip> action=src-nat to-addresses=<public_ip>"
            )
        )
        state = state.copy(templates = templates)
    }
}
