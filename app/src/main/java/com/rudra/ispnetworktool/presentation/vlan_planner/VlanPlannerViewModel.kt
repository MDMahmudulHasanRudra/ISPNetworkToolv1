package com.rudra.ispnetworktool.presentation.vlan_planner

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class VlanPlannerViewModel : ViewModel() {

    var state by mutableStateOf(VlanPlannerState())
        private set

    fun onVlanNameChange(name: String) {
        state = state.copy(vlanName = name, errorMessage = null)
    }

    fun onVlanIdChange(id: String) {
        state = state.copy(vlanId = id, errorMessage = null)
    }

    fun onVlanSubnetChange(subnet: String) {
        state = state.copy(vlanSubnet = subnet, errorMessage = null)
    }

    fun onAddVlan() {
        val id = state.vlanId.toIntOrNull()
        if (id == null || id !in 1..4094) {
            state = state.copy(errorMessage = "Invalid VLAN ID. Must be between 1 and 4094.")
            return
        }

        if (state.vlans.any { it.id == id }) {
            state = state.copy(errorMessage = "VLAN ID already exists.")
            return
        }

        if (state.vlanName.isBlank()) {
            state = state.copy(errorMessage = "VLAN name cannot be empty.")
            return
        }

        val newVlan = Vlan(id, state.vlanName, state.vlanSubnet)
        state = state.copy(
            vlans = state.vlans + newVlan,
            vlanName = "",
            vlanId = "",
            vlanSubnet = ""
        )
    }

    fun onDeleteVlan(vlan: Vlan) {
        state = state.copy(vlans = state.vlans - vlan)
    }
}
