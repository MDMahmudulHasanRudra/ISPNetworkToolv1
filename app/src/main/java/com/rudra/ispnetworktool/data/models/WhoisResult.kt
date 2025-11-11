package com.rudra.ispnetworktool.data.models

sealed class WhoisResult {
    data class Success(val rawData: String) : WhoisResult()
    data class Failure(val error: String) : WhoisResult()
    object InProgress : WhoisResult()
}
