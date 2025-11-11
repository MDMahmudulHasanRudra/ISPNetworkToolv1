package com.rudra.ispnetworktool.presentation.ipinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.ispnetworktool.data.local.ToolLogEntity
import com.rudra.ispnetworktool.domain.repository.HistoryRepository
import com.rudra.ispnetworktool.domain.repository.IpInfoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IpInfoViewModel @Inject constructor(
    private val ipInfoRepository: IpInfoRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IpInfoScreenState())
    val uiState = _uiState.asStateFlow()

    private val _errorFlow = MutableSharedFlow<String>()
    val errorFlow = _errorFlow.asSharedFlow()

    init {
        fetchIpInfo()
    }

    fun fetchIpInfo() {
        _uiState.value = IpInfoScreenState(isLoading = true)
        ipInfoRepository.getIpInfo()
            .onEach { ipInfo ->
                _uiState.value = IpInfoScreenState(ipInfo = ipInfo)
            }
            .catch { e ->
                _errorFlow.emit(e.message ?: "An unknown error occurred")
            }
            .launchIn(viewModelScope)
    }

    fun saveResult() {
        viewModelScope.launch {
            val ipInfo = _uiState.value.ipInfo
            if (ipInfo != null) {
                val log = ToolLogEntity(
                    toolType = "IP Info",
                    target = ipInfo.publicIp,
                    timestamp = System.currentTimeMillis(),
                    summary = "ISP: ${ipInfo.isp}",
                    resultJson = ""
                )
                historyRepository.saveLog(log)
                _errorFlow.emit("Saved to history")
            }
        }
    }

    fun shareResult(shareText: (String) -> Unit) {
        val ipInfo = _uiState.value.ipInfo
        if (ipInfo != null) {
            val text = "Public IP: ${ipInfo.publicIp}\n" +
                    "ISP: ${ipInfo.isp}\n" +
                    "Location: ${ipInfo.city}, ${ipInfo.country}\n" +
                    "Local IP: ${ipInfo.localIp}\n" +
                    "Gateway: ${ipInfo.gateway}\n" +
                    "DNS Servers: ${ipInfo.dnsServers.joinToString()}"
            shareText(text)
        }
    }
}
