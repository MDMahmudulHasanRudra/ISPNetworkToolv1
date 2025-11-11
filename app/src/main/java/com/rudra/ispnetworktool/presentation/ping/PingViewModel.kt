package com.rudra.ispnetworktool.presentation.ping

import android.content.Intent
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

    private var host: String? = null

    fun startPing(host: String, count: Int) {
        this.host = host
        _uiState.value = PingScreenState(isLoading = true)
        pingRepository.ping(host, count)
            .onEach { result ->
                val currentResults = _uiState.value.results.toMutableList()
                when (result) {
                    is PingResult.Success -> currentResults.add(result)
                    is PingResult.Failure -> _errorFlow.emit(result.error)
                    is PingResult.InProgress -> {}
                    is PingResult.Finished -> _uiState.value = _uiState.value.copy(isLoading = false)
                }
                _uiState.value = _uiState.value.copy(results = currentResults)
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
                target = host ?: "",
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
        val text = "Ping results for $host:\n" +
                results.joinToString("\n") { result ->
                    when (result) {
                        is PingResult.Success -> "Reply from $host: time=${result.rtt}ms"
                        is PingResult.Failure -> "Request timed out."
                        else -> ""
                    }
                }
        shareText(text)
    }

    fun clearResults() {
        _uiState.value = PingScreenState()
    }
}
