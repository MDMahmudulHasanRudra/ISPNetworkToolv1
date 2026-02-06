package com.rudra.ispnetworktool.data.models

sealed class PingResult {
    data class Success(val fullLine: String, val rtt: Float?) : PingResult()
    data class Failure(val error: String) : PingResult()
    data class Info(val message: String) : PingResult()
    object InProgress : PingResult()
    object Finished : PingResult()
}
