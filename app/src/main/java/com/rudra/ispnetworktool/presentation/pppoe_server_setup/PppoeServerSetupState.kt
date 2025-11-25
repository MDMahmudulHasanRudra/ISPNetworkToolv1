package com.rudra.ispnetworktool.presentation.pppoe_server_setup

data class PppoeServerSetupState(
    val steps: List<PppoeStep> = emptyList(),
    val currentStep: Int = 0
)

data class PppoeStep(
    val title: String,
    val description: String,
    val command: String? = null
)
