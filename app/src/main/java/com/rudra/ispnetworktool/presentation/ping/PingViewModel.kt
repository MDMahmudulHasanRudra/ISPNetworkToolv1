package com.rudra.ispnetworktool.presentation.ping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.ispnetworktool.data.local.ToolLogEntity
import com.rudra.ispnetworktool.data.models.PingResult
import com.rudra.ispnetworktool.domain.repository.HistoryRepository
import com.rudra.ispnetworktool.domain.repository.PingRepository
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
class PingViewModel @Inject constructor(
    private val pingRepository: PingRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PingScreenState())
    val uiState = _uiState.asStateFlow()

    private val _errorFlow = MutableSharedFlow<String>()
    val errorFlow = _errorFlow.asSharedFlow()

    private var currentHost: String? = null

    fun processCommand(input: String) {
        val trimmedInput = input.trim()
        if (trimmedInput.isEmpty()) return

        // Command parsing: handles "-t", "/t", "-T", "/T"
        val parts = trimmedInput.split("\\s+".toRegex())
        val host = parts.find { !it.startsWith("-") && !it.startsWith("/") } ?: ""
        val isContinuous = parts.any { it.equals("-t", true) || it.equals("/t", true) }
        
        if (host.isNotEmpty()) {
            startPing(host, if (isContinuous) Int.MAX_VALUE else 4)
        } else if (trimmedInput.isNotEmpty()) {
            // If no clear host found, try the whole input if it doesn't look like just options
            startPing(trimmedInput, 4)
        }
    }

    fun startPing(host: String, count: Int) {
        this.currentHost = host
        // Add a header line for the start of the ping
        val initialResults = listOf(PingResult.Info("Pinging $host with 32 bytes of data:"))
        _uiState.value = PingScreenState(isLoading = true, results = initialResults)
        
        pingRepository.ping(host, count)
            .onEach { result ->
                val currentState = _uiState.value
                val currentResults = currentState.results.toMutableList()
                
                when (result) {
                    is PingResult.Success -> {
                        currentResults.add(result)
                        _uiState.value = currentState.copy(results = currentResults)
                    }
                    is PingResult.Info -> {
                        currentResults.add(result)
                        _uiState.value = currentState.copy(results = currentResults)
                    }
                    is PingResult.Failure -> {
                        currentResults.add(result)
                        _uiState.value = currentState.copy(results = currentResults, isLoading = false)
                        _errorFlow.emit(result.error)
                    }
                    is PingResult.InProgress -> {
                        _uiState.value = currentState.copy(isLoading = true)
                    }
                    is PingResult.Finished -> {
                        _uiState.value = currentState.copy(isLoading = false)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun stopPing() {
        pingRepository.stopPing()
        _uiState.value = _uiState.value.copy(isLoading = false)
    }

    fun saveResult() {
        viewModelScope.launch {
            val log = ToolLogEntity(
                toolType = "Ping",
                target = currentHost ?: "Unknown",
                timestamp = System.currentTimeMillis(),
                summary = "${_uiState.value.results.size} pings",
                resultJson = ""
            )
            historyRepository.saveLog(log)
            _errorFlow.emit("Saved to history")
        }
    }

    fun shareResult(shareText: (String) -> Unit) {
        val results = _uiState.value.results
        val text = "Ping results for $currentHost:\n" +
                results.joinToString("\n") { result ->
                    when (result) {
                        is PingResult.Success -> result.fullLine
                        is PingResult.Failure -> "Request timed out: ${result.error}"
                        is PingResult.Info -> result.message
                        else -> ""
                    }
                }
        shareText(text)
    }

    fun clearResults() {
        _uiState.value = PingScreenState()
    }
}
