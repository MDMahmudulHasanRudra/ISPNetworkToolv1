package com.rudra.ispnetworktool.data.models

sealed class PortResult {
    object Open : PortResult()
    object Closed : PortResult()
    data class Error(val message: String) : PortResult()
}
