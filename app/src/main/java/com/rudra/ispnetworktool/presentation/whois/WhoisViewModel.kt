package com.rudra.ispnetworktool.presentation.whois

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.ispnetworktool.data.local.ToolLogEntity
import com.rudra.ispnetworktool.data.models.WhoisResult
import com.rudra.ispnetworktool.domain.repository.HistoryRepository
import com.rudra.ispnetworktool.domain.repository.WhoisRepository
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
class WhoisViewModel @Inject constructor(
    private val whoisRepository: WhoisRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WhoisScreenState())
    val uiState = _uiState.asStateFlow()

    private val _errorFlow = MutableSharedFlow<String>()
    val errorFlow = _errorFlow.asSharedFlow()

    private var domain: String? = null

    fun lookup(domain: String) {
        this.domain = domain
        _uiState.value = WhoisScreenState(isLoading = true)
        whoisRepository.lookup(domain)
            .onEach { result ->
                _uiState.value = WhoisScreenState(result = result)
            }
            .catch { e ->
                viewModelScope.launch {
                    _errorFlow.emit(e.message ?: "An unknown error occurred")
                }
            }
            .launchIn(viewModelScope)
    }

    fun saveResult() {
        viewModelScope.launch {
            val result = _uiState.value.result
            if (result is WhoisResult.Success) {
                val log = ToolLogEntity(
                    toolType = "WHOIS Lookup",
                    target = domain ?: "",
                    timestamp = System.currentTimeMillis(),
                    summary = "WHOIS data retrieved",
                    resultJson = result.rawData
                )
                historyRepository.saveLog(log)
                _errorFlow.emit("Saved to history")
            }
        }
    }

    fun shareResult(shareText: (String) -> Unit) {
        val result = _uiState.value.result
        if (result is WhoisResult.Success) {
            shareText(result.rawData)
        }
    }

    fun clearResult() {
        _uiState.value = WhoisScreenState()
    }
}
