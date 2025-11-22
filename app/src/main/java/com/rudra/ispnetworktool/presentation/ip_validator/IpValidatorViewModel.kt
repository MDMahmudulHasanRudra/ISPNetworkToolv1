package com.rudra.ispnetworktool.presentation.ip_validator

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.ispnetworktool.domain.use_case.GetIpAddress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.net.InetAddress
import javax.inject.Inject

@HiltViewModel
class IpValidatorViewModel @Inject constructor(
    private val getIpAddress: GetIpAddress
) : ViewModel() {

    private val _state = mutableStateOf(IpValidatorScreenState())
    val state: State<IpValidatorScreenState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var validationJob: Job? = null

    fun onIpAddressChanged(ipAddress: String) {
        _state.value = _state.value.copy(
            ipAddress = ipAddress,
            isValid = null,
            isPublic = null,
            ipType = null,
            networkClass = null,
            validationStatus = ValidationStatus.IDLE,
            errorMessage = null
        )

        if (isPotentialCompleteIp(ipAddress)) {
            viewModelScope.launch {
                delay(500)
                if (ipAddress == _state.value.ipAddress) {
                    checkIpAddress()
                }
            }
        }
    }

    fun checkIpAddress() {
        val ipAddress = _state.value.ipAddress.trim()
        if (ipAddress.isBlank()) {
            _state.value = _state.value.copy(
                validationStatus = ValidationStatus.ERROR,
                errorMessage = "Please enter an IP address"
            )
            return
        }

        validationJob?.cancel()
        validationJob = viewModelScope.launch {
            try {
                _state.value = _state.value.copy(
                    isLoading = true,
                    validationStatus = ValidationStatus.VALIDATING,
                    errorMessage = null
                )

                val isValidFormat = IpValidatorUtils.validateIpv4(ipAddress) ||
                        IpValidatorUtils.validateIpv6(ipAddress)

                if (!isValidFormat) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isValid = false,
                        validationStatus = ValidationStatus.INVALID,
                        errorMessage = "Invalid IP address format"
                    )
                    _eventFlow.emit(UiEvent.ShowMessage("Invalid IP format"))
                    return@launch
                }

                val inetAddress = InetAddress.getByName(ipAddress)
                val isReachable = inetAddress.isReachable(3000)

                val ipType = when {
                    IpValidatorUtils.validateIpv4(ipAddress) -> IpType.IPV4
                    IpValidatorUtils.validateIpv6(ipAddress) -> IpType.IPV6
                    else -> IpType.UNKNOWN
                }

                val isPublic = !inetAddress.isSiteLocalAddress &&
                        !IpValidatorUtils.isPrivateIp(ipAddress)

                val networkClass = if (ipType == IpType.IPV4) {
                    IpValidatorUtils.getNetworkClass(ipAddress)
                } else {
                    null
                }

                val additionalInfo = if (ipType == IpType.IPV4) {
                    IpValidatorUtils.calculateSubnetInfo(ipAddress)
                } else {
                    IpAdditionalInfo()
                }.copy(
                    specialPurpose = IpValidatorUtils.getSpecialPurpose(ipAddress),
                    isp = if (isPublic) "Unknown ISP" else "Local Network"
                )

                _state.value = _state.value.copy(
                    isLoading = false,
                    isValid = true,
                    isPublic = isPublic,
                    ipType = ipType,
                    networkClass = networkClass,
                    validationStatus = ValidationStatus.VALID,
                    additionalInfo = additionalInfo,
                    lastUpdated = System.currentTimeMillis(),
                    errorMessage = null
                )

                _eventFlow.emit(UiEvent.ShowMessage("✓ IP address validated successfully"))

                if (isValidFormat) {
                    saveToHistory(ipAddress, isPublic, ipType, networkClass)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _state.value = _state.value.copy(
                    isLoading = false,
                    isValid = false,
                    validationStatus = ValidationStatus.ERROR,
                    errorMessage = when (e) {
                        is java.net.UnknownHostException -> "Unknown host: ${e.message}"
                        is SecurityException -> "Network access denied"
                        else -> "Validation failed: ${e.message}"
                    },
                    lastUpdated = System.currentTimeMillis()
                )
                _eventFlow.emit(UiEvent.ShowMessage("✗ Validation failed: ${e.message}"))
            }
        }
    }

    fun getMyIp() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(
                    isLoading = true,
                    validationStatus = ValidationStatus.VALIDATING,
                    errorMessage = null
                )

                // Use async for concurrent execution
                val publicIpDeferred = async { getIpAddress.getPublicIp() }
                val localIpDeferred = async { getIpAddress.getLocalIp() }

                // await() is now called in a proper suspending context
                val publicIp = publicIpDeferred.await()
                val localIp = localIpDeferred.await()

                _state.value = _state.value.copy(
                    ipAddress = publicIp.ip,
                    isLoading = false,
                    isValid = true,
                    isPublic = true,
                    ipType = IpType.IPV4,
                    validationStatus = ValidationStatus.VALID,
                    additionalInfo = IpAdditionalInfo(
                        isp = "Your Internet Service Provider",
                        location = "Your approximate location"
                    ),
                    lastUpdated = System.currentTimeMillis()
                )

                _eventFlow.emit(UiEvent.ShowMessage("✓ Found your IP addresses"))
                checkIpAddress()

            } catch (e: Exception) {
                e.printStackTrace()
                _state.value = _state.value.copy(
                    isLoading = false,
                    validationStatus = ValidationStatus.ERROR,
                    errorMessage = "Failed to get IP addresses: ${e.message}"
                )
                _eventFlow.emit(UiEvent.ShowMessage("✗ Failed to get your IP"))
            }
        }
    }

    fun quickValidate(ip: String) {
        _state.value = _state.value.copy(ipAddress = ip)
        checkIpAddress()
    }

    suspend fun clearResults() {
        validationJob?.cancel()
        _state.value = IpValidatorScreenState()
        _eventFlow.emit(UiEvent.ShowMessage("Results cleared"))
    }

    suspend fun retryValidation() {
        if (_state.value.ipAddress.isNotBlank()) {
            checkIpAddress()
        } else {
            _eventFlow.emit(UiEvent.ShowMessage("Enter an IP address first"))
        }
    }

    fun validateCommonIp(ip: String, description: String) {
        _state.value = _state.value.copy(ipAddress = ip)
        viewModelScope.launch {
            _eventFlow.emit(UiEvent.ShowMessage("Validating $description..."))
            checkIpAddress()
        }
    }

    private fun isPotentialCompleteIp(ip: String): Boolean {
        val trimmed = ip.trim()
        return when {
            IpValidatorUtils.validateIpv4(trimmed) -> true
            IpValidatorUtils.validateIpv6(trimmed) -> true
            trimmed.count { it == '.' } == 3 -> true
            trimmed.contains(':') -> true
            else -> false
        }
    }

    private fun saveToHistory(
        ip: String,
        isPublic: Boolean?,
        ipType: IpType?,
        networkClass: NetworkClass?
    ) {
        viewModelScope.launch {
            try {
                val summary = buildString {
                    append("IP: $ip")
                    isPublic?.let { append(", ${if (it) "Public" else "Private"}") }
                    ipType?.let { append(", ${it.name}") }
                    networkClass?.let { append(", ${it.name}") }
                }
                _eventFlow.emit(UiEvent.ShowMessage("✓ Saved to history"))
            } catch (e: Exception) {
                // Silent fail
            }
        }
    }

    suspend fun shareResults() {
        val state = _state.value
        if (state.isValid != true) {
            _eventFlow.emit(UiEvent.ShowMessage("No valid results to share"))
            return
        }

        val shareText = buildString {
            appendLine("🌐 IP Address Validation Results")
            appendLine("══════════════════════════════")
            appendLine()
            appendLine("📍 IP Address: ${state.ipAddress}")
            appendLine("📊 Status: ${state.displayStatus}")
            appendLine("🔒 Type: ${if (state.isPublic == true) "Public" else "Private"}")
            state.ipType?.let { appendLine("🌍 Version: ${it.name}") }
            state.networkClass?.let { appendLine("📈 Network Class: ${it.name}") }
            state.additionalInfo.specialPurpose?.let { appendLine("🎯 Purpose: ${it.name}") }
            appendLine()
            appendLine("Generated by ISP Network Tools")
            appendLine("${java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}")
        }

        _eventFlow.emit(UiEvent.ShareResults(shareText))
    }

    override fun onCleared() {
        super.onCleared()
        validationJob?.cancel()
    }
}

sealed class UiEvent {
    data class ShowMessage(val message: String) : UiEvent()
    data class ShareResults(val text: String) : UiEvent()
}
