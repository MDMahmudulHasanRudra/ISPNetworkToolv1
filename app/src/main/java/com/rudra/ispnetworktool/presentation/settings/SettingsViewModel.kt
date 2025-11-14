package com.rudra.ispnetworktool.presentation.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SettingsState(
    val useCustomDns: Boolean = false,
    val customDnsServer: String = "1.1.1.1",
    val defaultTimeout: Int = 2000, // in milliseconds
    val isLoggingEnabled: Boolean = true,
    val theme: ThemeSetting = ThemeSetting.SYSTEM
)

enum class ThemeSetting { LIGHT, DARK, SYSTEM }

class SettingsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsState())
    val uiState = _uiState.asStateFlow()

    fun setUseCustomDns(useCustom: Boolean) {
        _uiState.value = _uiState.value.copy(useCustomDns = useCustom)
    }

    fun setCustomDnsServer(dnsServer: String) {
        _uiState.value = _uiState.value.copy(customDnsServer = dnsServer)
    }

    fun setDefaultTimeout(timeout: Int) {
        _uiState.value = _uiState.value.copy(defaultTimeout = timeout)
    }

    fun setLoggingEnabled(isEnabled: Boolean) {
        _uiState.value = _uiState.value.copy(isLoggingEnabled = isEnabled)
    }

    fun setTheme(theme: ThemeSetting) {
        _uiState.value = _uiState.value.copy(theme = theme)
    }
}
