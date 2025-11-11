package com.rudra.ispnetworktool.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.ispnetworktool.data.models.IpInfo
import com.rudra.ispnetworktool.domain.repository.IpInfoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

data class DashboardState(
    val ipInfo: IpInfo? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val ipInfoRepository: IpInfoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardState())
    val uiState: StateFlow<DashboardState> = _uiState.asStateFlow()

    init {
        fetchIpInfo()
    }

    fun fetchIpInfo() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        ipInfoRepository.getIpInfo()
            .onEach { ipInfo ->
                _uiState.value = DashboardState(ipInfo = ipInfo)
            }
            .launchIn(viewModelScope)
    }
}
