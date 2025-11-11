package com.rudra.ispnetworktool.presentation.dns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.ispnetworktool.data.local.ToolLogEntity
import com.rudra.ispnetworktool.data.models.DnsResult
import com.rudra.ispnetworktool.domain.repository.DnsRecordType
import com.rudra.ispnetworktool.domain.repository.DnsRepository
import com.rudra.ispnetworktool.domain.repository.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DnsLookupViewModel @Inject constructor(
    private val dnsRepository: DnsRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DnsLookupScreenState())
    val uiState = _uiState.asStateFlow()

    private val _errorFlow = MutableSharedFlow<String>()
    val errorFlow = _errorFlow.asSharedFlow()

    private var host: String? = null
    private var recordType: DnsRecordType? = null

    fun lookup(host: String, recordType: DnsRecordType) {
        this.host = host
        this.recordType = recordType
        _uiState.value = DnsLookupScreenState(isLoading = true)
        dnsRepository.lookup(host, recordType)
            .onEach { result ->
                when (result) {
                    is DnsResult.Success -> _uiState.value = DnsLookupScreenState(result = result)
                    is DnsResult.Failure -> _errorFlow.emit(result.error)
                    is DnsResult.InProgress -> _uiState.value = DnsLookupScreenState(isLoading = true)
                }
            }
            .launchIn(viewModelScope)
    }

    fun saveResult() {
        viewModelScope.launch {
            val result = _uiState.value.result
            if (result is DnsResult.Success) {
                val log = ToolLogEntity(
                    toolType = "DNS Lookup",
                    target = "$host ($recordType)",
                    timestamp = System.currentTimeMillis(),
                    summary = "${result.records.size} records found",
                    resultJson = ""
                )
                historyRepository.saveLog(log)
                _errorFlow.emit("Saved to history")
            }
        }
    }

    fun shareResult(shareText: (String) -> Unit) {
        val result = _uiState.value.result
        if (result is DnsResult.Success) {
            val text = "DNS lookup results for $host ($recordType):\n" +
                    result.records.joinToString("\n")
            shareText(text)
        }
    }

    fun clearResult() {
        _uiState.value = DnsLookupScreenState()
    }
}
