package com.rudra.ispnetworktool.domain.core

import kotlinx.coroutines.flow.Flow

/**
 * A standardized interface for running a network tool operation.
 * It defines how to start and stop a potentially long-running operation that emits results over time.
 *
 * @param T The type of data emitted by the tool's execution flow.
 */
interface ToolOperation<T> {
    /**
     * Starts the tool's operation with a given set of parameters.
     *
     * @param parameters A map of key-value pairs representing the configuration for the tool run
     * (e.g., "host" to "google.com", "packetCount" to 10).
     * @return A [Flow] that emits results of type [T] as the operation progresses.
     */
    fun start(parameters: Map<String, Any>): Flow<T>

    /**
     * Stops or cancels the ongoing tool operation.
     * This should release any resources and stop any background processes tied to the operation.
     */
    fun stop()
}
