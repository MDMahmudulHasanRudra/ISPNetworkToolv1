package com.rudra.ispnetworktool.presentation.load_balancing_presets

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
class LoadBalancingPresetsViewModel @Inject constructor() : ViewModel() {

    var state by mutableStateOf(LoadBalancingPresetsState())
        private set

    fun selectPreset(preset: LoadBalancingPreset) {
        state = state.copy(
            selectedPreset = preset,
            customConfiguration = null
        )
    }

    fun createCustomConfiguration() {
        val customConfig = LoadBalancingConfiguration(
            name = "Custom Configuration",
            description = "Your custom load balancing setup",
            algorithm = LoadBalancingAlgorithm.ROUND_ROBIN,
            healthCheckConfig = HealthCheckConfig(HealthCheckType.TCP, 5000, 30000),
            sessionPersistence = SessionPersistence.NONE,
            backupServers = 1,
            timeoutMs = 30000,
            retryAttempts = 3,
            commands = emptyList()
        )
        state = state.copy(
            selectedPreset = null,
            customConfiguration = customConfig
        )
    }

    fun selectCustomConfiguration(config: LoadBalancingConfiguration) {
        state = state.copy(
            selectedPreset = null,
            customConfiguration = config
        )
    }

    fun saveCustomConfiguration() {
        val currentConfig = state.customConfiguration
        if (currentConfig != null && state.customConfigurations.none { it.name == currentConfig.name }) {
            state = state.copy(
                customConfigurations = state.customConfigurations + currentConfig
            )
        }
    }

    fun deleteCustomConfiguration(index: Int) {
        val configToDelete = state.customConfigurations.getOrNull(index) ?: return
        val updatedConfigs = state.customConfigurations.toMutableList().apply {
            removeAt(index)
        }
        if (state.customConfiguration == configToDelete) {
            state = state.copy(
                customConfigurations = updatedConfigs,
                customConfiguration = null
            )
        } else {
            state = state.copy(customConfigurations = updatedConfigs)
        }
    }

    fun clearSelection() {
        state = state.copy(
            selectedPreset = null,
            customConfiguration = null
        )
    }

    fun applyConfiguration() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)

            try {
                delay(2000) // Simulate configuration application

                // For demo, randomly determine if application succeeds
                val success = Math.random() > 0.1

                if (success) {
                    state = state.copy(isLoading = false)
                    // In real app, would navigate or show success message
                } else {
                    state = state.copy(
                        isLoading = false,
                        error = "Failed to apply configuration. Check system requirements."
                    )
                }
            } catch (e: Exception) {
                state = state.copy(
                    isLoading = false,
                    error = "Error applying configuration: ${e.message}"
                )
            }
        }
    }

    fun exportConfigurations() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            delay(1000) // Simulate export
            state = state.copy(isLoading = false)
            // Handle export logic
        }
    }
}