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
    val filteredNodes: List<BDIXNode> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String = "All",
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

    private var allNodes: List<BDIXNode> = emptyList()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        allNodes = repository.bdixNodes
        _uiState.update { 
            it.copy(
                nodes = allNodes, 
                filteredNodes = allNodes,
                isLoading = false 
            ) 
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun onCategorySelect(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        applyFilters()
    }

    private fun applyFilters() {
        val query = _uiState.value.searchQuery.lowercase()
        val category = _uiState.value.selectedCategory

        val filtered = allNodes.filter { node ->
            val matchesQuery = node.name.lowercase().contains(query) || 
                             node.ipAddress.contains(query) || 
                             node.location.lowercase().contains(query)
            
            val matchesCategory = category == "All" || node.category == category
            
            matchesQuery && matchesCategory
        }

        _uiState.update { it.copy(filteredNodes = filtered) }
    }

    fun pingAllNodes() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        val currentNodes = _uiState.value.filteredNodes
        val results = _uiState.value.pingResults.toMutableMap()
        
        currentNodes.forEach { node ->
            val result = repository.pingNode(node.id)
            results[node.id] = result
            
            // Update node status in allNodes too to keep it consistent
            allNodes = allNodes.map { n ->
                if (n.id == node.id) n.copy(status = determineNodeStatus(result)) else n
            }
            
            _uiState.update { it.copy(pingResults = results.toMap()) }
            applyFilters() // Refresh filtered list with new statuses
        }
        _uiState.update { it.copy(isLoading = false) }
    }

    fun pingNode(nodeId: String) = viewModelScope.launch {
        val result = repository.pingNode(nodeId)
        val results = _uiState.value.pingResults.toMutableMap()
        results[nodeId] = result
        
        allNodes = allNodes.map { node ->
            if (node.id == nodeId) {
                node.copy(status = determineNodeStatus(result))
            } else node
        }
        
        _uiState.update { it.copy(pingResults = results) }
        applyFilters()
    }

    private fun determineNodeStatus(pingResult: BDIXPingResult): NodeStatus {
        return when {
            !pingResult.isSuccess || pingResult.packetLoss > 20 -> NodeStatus.DOWN
            pingResult.packetLoss > 5 || pingResult.latencyMs > 100 -> NodeStatus.DEGRADED
            else -> NodeStatus.HEALTHY
        }
    }
    
    fun getCategories(): List<String> {
        return listOf("All") + allNodes.map { it.category }.distinct().sorted()
    }
}
