package com.rudra.ispnetworktool.presentation.vlan_planner

data class VlanPlannerState(
    val vlans: List<Vlan> = emptyList(),
    val vlanName: String = "",
    val vlanId: String = "",
    val vlanSubnet: String = "",
    val errorMessage: String? = null
)

data class Vlan(
    val id: Int,
    val name: String,
    val subnet: String
)
