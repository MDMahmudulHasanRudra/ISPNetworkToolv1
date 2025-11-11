package com.rudra.ispnetworktool.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.ispnetworktool.domain.core.ToolOperation
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * An abstract ViewModel that provides a standard structure for tool execution.
 *
 * This class is designed to work with a [ToolOperation] to manage the lifecycle of a network tool.
 * It handles starting, stopping, and collecting results from the operation, exposing them through a [StateFlow].
 *
 * @param T The type of data produced by the tool operation.
 * @param S The type of the UI state managed by this ViewModel.
 * @property toolOperation The [ToolOperation] implementation that this ViewModel will manage.
 * @property initialState The initial state of the ViewModel before any operation has started.
 */
abstract class BaseViewModel<T, S>(
    private val toolOperation: ToolOperation<T>,
    private val initialState: S
) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    private var toolJob: Job? = null

    /**
     * Starts the tool operation with the given parameters.
     *
     * Any existing operation will be cancelled before the new one begins.
     * Results from the [ToolOperation] are collected and passed to [handleResult] to update the UI state.
     *
     * @param params The parameters for the tool operation.
     */
    fun startOperation(params: Map<String, Any>) {
        stopOperation() // Cancel any previous job
        toolJob = toolOperation.start(params)
            .onEach { result -> _uiState.value = handleResult(result) }
            .launchIn(viewModelScope)
    }

    /**
     * Stops the currently running tool operation.
     */
    fun stopOperation() {
        toolJob?.cancel()
        toolJob = null
        toolOperation.stop()
    }

    /**
     * Abstract function to be implemented by subclasses to transform the raw tool result [T]
     * into the appropriate UI state [S].
     *
     * @param result The result from the [ToolOperation].
     * @return The new UI state.
     */
    protected abstract fun handleResult(result: T): S

    /**
     * Resets the UI to its initial state.
     */
    fun resetState() {
        _uiState.value = initialState
    }

    override fun onCleared() {
        super.onCleared()
        stopOperation()
    }
}
