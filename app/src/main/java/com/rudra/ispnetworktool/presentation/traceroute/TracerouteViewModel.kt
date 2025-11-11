package com.rudra.ispnetworktool.presentation.traceroute

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.ispnetworktool.data.local.ToolLogEntity
import com.rudra.ispnetworktool.data.models.TracerouteResult
import com.rudra.ispnetworktool.domain.repository.HistoryRepository
import com.rudra.ispnetworktool.domain.repository.TracerouteRepository
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
class TracerouteViewModel @Inject constructor(
    private val tracerouteRepository: TracerouteRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TracerouteScreenState())
    val uiState = _uiState.asStateFlow()

    private val _errorFlow = MutableSharedFlow<String>()
    val errorFlow = _errorFlow.asSharedFlow()

    private var host: String? = null

    fun startTraceroute(host: String) {
        this.host = host
        _uiState.value = TracerouteScreenState(isLoading = true)
        tracerouteRepository.traceroute(host)
            .onEach { result ->
                val currentResults = _uiState.value.results.toMutableList()
                when (result) {
                    is TracerouteResult.Hop -> currentResults.add(result)
                    is TracerouteResult.Failure -> _errorFlow.emit(result.error)
                    is TracerouteResult.InProgress -> {}
                    is TracerouteResult.Finished -> _uiState.value = _uiState.value.copy(isLoading = false)
                }
                _uiState.value = _uiState.value.copy(results = currentResults)
            }
            .launchIn(viewModelScope)
    }

    fun stopTraceroute() {
        tracerouteRepository.stopTraceroute()
        _uiState.value = _uiState.value.copy(isLoading = false)
    }

    fun saveResult() {
        viewModelScope.launch {
            val log = ToolLogEntity(
                toolType = "Traceroute",
                target = host ?: "",
                timestamp = System.currentTimeMillis(),
                summary = "${_uiState.value.results.size} hops",
                resultJson = ""
            )
            historyRepository.saveLog(log)
            _errorFlow.emit("Saved to history")
        }
    }

    fun shareResult(shareText: (String) -> Unit) {
        val results = _uiState.value.results
        val text = "Traceroute results for $host:\n" +
                results.joinToString("\n") { result ->
                    when (result) {
                        is TracerouteResult.Hop -> "${result.hop}. ${result.ip} - ${result.rtt}ms"
                        is TracerouteResult.Failure -> "Error: ${result.error}"
                        else -> ""
                    }
                }
        shareText(text)
    }

    fun clearResults() {
        _uiState.value = TracerouteScreenState()
    }
}
