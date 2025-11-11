package com.rudra.ispnetworktool.data.models

sealed class TracerouteResult {
    data class Hop(val hop: Int, val ip: String, val rtt: Float) : TracerouteResult()
    data class Failure(val error: String) : TracerouteResult()
    object InProgress : TracerouteResult()
    object Finished : TracerouteResult()
}
