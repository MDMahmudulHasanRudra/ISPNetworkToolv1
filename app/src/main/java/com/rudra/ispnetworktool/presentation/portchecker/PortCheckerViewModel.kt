package com.rudra.ispnetworktool.presentation.portchecker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.ispnetworktool.data.local.ToolLogEntity
import com.rudra.ispnetworktool.data.models.PortResult
import com.rudra.ispnetworktool.domain.repository.HistoryRepository
import com.rudra.ispnetworktool.domain.repository.PortCheckerRepository
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
class PortCheckerViewModel @Inject constructor(
    private val portCheckerRepository: PortCheckerRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PortCheckerScreenState())
    val uiState = _uiState.asStateFlow()

    private val _errorFlow = MutableSharedFlow<String>()
    val errorFlow = _errorFlow.asSharedFlow()

    private var host: String? = null
    private var port: Int? = null

    fun checkPort(host: String, port: Int) {
        this.host = host
        this.port = port
        _uiState.value = PortCheckerScreenState(isLoading = true)
        portCheckerRepository.checkPort(host, port)
            .onEach { result ->
                _uiState.value = PortCheckerScreenState(result = result)
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
            if (result != null) {
                val log = ToolLogEntity(
                    toolType = "Port Checker",
                    target = "$host:$port",
                    timestamp = System.currentTimeMillis(),
                    summary = when (result) {
                        is PortResult.Open -> "Open"
                        is PortResult.Closed -> "Closed"
                        is PortResult.Error -> "Error"
                    },
                    resultJson = ""
                )
                historyRepository.saveLog(log)
                _errorFlow.emit("Saved to history")
            }
        }
    }

    fun shareResult(shareText: (String) -> Unit) {
        val result = _uiState.value.result
        if (result != null) {
            val text = "Port check result for $host:$port: " +
                    when (result) {
                        is PortResult.Open -> "Open"
                        is PortResult.Closed -> "Closed"
                        is PortResult.Error -> "Error: ${result.message}"
                    }
            shareText(text)
        }
    }

    fun clearResult() {
        _uiState.value = PortCheckerScreenState()
    }
}
