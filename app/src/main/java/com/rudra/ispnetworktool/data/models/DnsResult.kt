package com.rudra.ispnetworktool.data.models

sealed class DnsResult {
    data class Success(val records: List<String>) : DnsResult()
    data class Failure(val error: String) : DnsResult()
    object InProgress : DnsResult()
}
