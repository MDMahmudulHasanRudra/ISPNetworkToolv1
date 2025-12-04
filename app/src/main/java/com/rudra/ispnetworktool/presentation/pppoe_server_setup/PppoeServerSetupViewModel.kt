package com.rudra.ispnetworktool.presentation.pppoe_server_setup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class PppoeServerSetupViewModel : ViewModel() {

    var state by mutableStateOf(PppoeServerSetupState())
        private set

    init {
        loadSteps()
    }

    fun onNextStep() {
        if (state.currentStep < state.steps.size - 1) {
            state = state.copy(currentStep = state.currentStep + 1)
        }
    }

    fun onPreviousStep() {
        if (state.currentStep > 0) {
            state = state.copy(currentStep = state.currentStep - 1)
        }
    }

    private fun loadSteps() {
        val steps = listOf(
            PppoeStep(
                title = "Create IP Pool",
                description = "Create a pool of IP addresses to be assigned to clients.",
                command = "/ip pool add name=pppoe-pool ranges=192.168.88.100-192.168.88.200"
            ),
            PppoeStep(
                title = "Create PPPoE Profile",
                description = "Create a profile to define the settings for PPPoE clients.",
                command = "/ppp profile add name=pppoe-profile local-address=192.168.88.1 remote-address=pppoe-pool"
            ),
            PppoeStep(
                title = "Create PPPoE Server",
                description = "Create the PPPoE server on the desired interface.",
                command = "/interface pppoe-server server add interface=ether2 service-name=pppoe-service authentication=mschap2 disabled=no"
            ),
            PppoeStep(
                title = "Create User Secret",
                description = "Create a username and password for a PPPoE user.",
                command = "/ppp secret add name=user1 password=pass service=pppoe profile=pppoe-profile"
            )
        )
        state = state.copy(steps = steps)
    }
}
