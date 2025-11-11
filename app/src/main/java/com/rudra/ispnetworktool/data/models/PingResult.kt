package com.rudra.ispnetworktool.data.models

sealed class PingResult {
    data class Success(val rtt: Float) : PingResult()
    data class Failure(val error: String) : PingResult()
    object InProgress : PingResult()
    object Finished : PingResult()
}
