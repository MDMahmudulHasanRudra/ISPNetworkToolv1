package com.rudra.ispnetworktool.presentation.bdix

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.ispnetworktool.data.repository.BDIXRepositoryImpl
import com.rudra.ispnetworktool.domain.model.BDIXNode
import com.rudra.ispnetworktool.domain.model.BDIXPingResult
import com.rudra.ispnetworktool.domain.model.HealthMetrics
import com.rudra.ispnetworktool.domain.model.ISPInfo
import com.rudra.ispnetworktool.domain.model.NodeStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BDIXUIState(
    val nodes: List<BDIXNode> = emptyList(),
    val pingResults: Map<String, BDIXPingResult> = emptyMap(),
    val healthMetrics: Map<String, HealthMetrics> = emptyMap(),
    val isLoading: Boolean = false,
    val selectedISP: ISPInfo? = null
)

@HiltViewModel
class BDIXViewModel @Inject constructor(
    private val repository: BDIXRepositoryImpl
) : ViewModel() {

    private val _uiState = MutableStateFlow(BDIXUIState())
    val uiState: StateFlow<BDIXUIState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        _uiState.update { it.copy(nodes = repository.bdixNodes, isLoading = false) }
    }

    fun pingAllNodes() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        val results = mutableMapOf<String, BDIXPingResult>()
        val updatedNodes = _uiState.value.nodes.toMutableList()

        _uiState.value.nodes.forEachIndexed { index, node ->
            val result = repository.pingNode(node.id)
            results[node.id] = result
            
            val status = determineNodeStatus(result)
            updatedNodes[index] = node.copy(status = status)
            
            // Update state incrementally for better UX
            _uiState.update { it.copy(pingResults = results.toMap(), nodes = updatedNodes.toList()) }
        }
        _uiState.update { it.copy(isLoading = false) }
    }

    fun pingNode(nodeId: String) = viewModelScope.launch {
        val result = repository.pingNode(nodeId)
        val results = _uiState.value.pingResults.toMutableMap()
        results[nodeId] = result
        
        val updatedNodes = _uiState.value.nodes.map { node ->
            if (node.id == nodeId) {
                node.copy(status = determineNodeStatus(result))
            } else node
        }
        
        _uiState.update { it.copy(pingResults = results, nodes = updatedNodes) }
    }

    private fun determineNodeStatus(pingResult: BDIXPingResult): NodeStatus {
        return when {
            !pingResult.isSuccess || pingResult.packetLoss > 20 -> NodeStatus.DOWN
            pingResult.packetLoss > 5 || pingResult.latencyMs > 100 -> NodeStatus.DEGRADED
            else -> NodeStatus.HEALTHY
        }
    }
}
