package com.rudra.ispnetworktool.presentation.bandwidth_calculator

import androidx.lifecycle.ViewModel
import com.rudra.ispnetworktool.domain.logic.BandwidthCalculator
import com.rudra.ispnetworktool.domain.model.BandwidthResult
import com.rudra.ispnetworktool.domain.model.PackageInput
import com.rudra.ispnetworktool.domain.model.ServiceSelection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class BandwidthCalculatorState(
    val packages: List<PackageInput> = listOf(PackageInput(5, 100)),
    val services: ServiceSelection = ServiceSelection(),
    val bufferPercent: Int = 20,
    val contentionRatio: Int = 10,
    val result: BandwidthResult = BandwidthResult()
)

class BandwidthCalculatorViewModel : ViewModel() {
    private val calculator = BandwidthCalculator()
    
    private val _state = MutableStateFlow(BandwidthCalculatorState())
    val state: StateFlow<BandwidthCalculatorState> = _state.asStateFlow()

    init {
        calculate()
    }

    fun onPackageAdded() {
        _state.update { it.copy(packages = it.packages + PackageInput(10, 0)) }
        calculate()
    }

    fun onPackageRemoved(index: Int) {
        if (_state.value.packages.size > 1) {
            _state.update { it.copy(packages = it.packages.filterIndexed { i, _ -> i != index }) }
            calculate()
        }
    }

    fun onPackageChanged(index: Int, speed: Int? = null, users: Int? = null) {
        _state.update { currentState ->
            val updatedPackages = currentState.packages.toMutableList()
            val oldPkg = updatedPackages[index]
            updatedPackages[index] = oldPkg.copy(
                speedMbps = speed ?: oldPkg.speedMbps,
                users = users ?: oldPkg.users
            )
            currentState.copy(packages = updatedPackages)
        }
        calculate()
    }

    fun onServiceToggle(service: String) {
        _state.update { currentState ->
            val newServices = when (service) {
                "ipt" -> currentState.services.copy(ipt = !currentState.services.ipt)
                "cdn" -> currentState.services.copy(cdn = !currentState.services.cdn)
                "baishan" -> currentState.services.copy(baishan = !currentState.services.baishan)
                "ggc" -> currentState.services.copy(ggc = !currentState.services.ggc)
                "fna" -> currentState.services.copy(fna = !currentState.services.fna)
                "bdix" -> currentState.services.copy(bdix = !currentState.services.bdix)
                else -> currentState.services
            }
            currentState.copy(services = newServices)
        }
        calculate()
    }

    fun onBufferChanged(buffer: Int) {
        _state.update { it.copy(bufferPercent = buffer) }
        calculate()
    }

    fun onContentionRatioChanged(ratio: Int) {
        _state.update { it.copy(contentionRatio = ratio) }
        calculate()
    }

    private fun calculate() {
        val currentState = _state.value
        val result = calculator.calculate(
            packages = currentState.packages,
            services = currentState.services,
            bufferPercent = currentState.bufferPercent,
            contentionRatio = currentState.contentionRatio
        )
        _state.update { it.copy(result = result) }
    }
}
