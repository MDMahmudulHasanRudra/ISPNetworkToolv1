package com.rudra.ispnetworktool.presentation.dns

import com.rudra.ispnetworktool.data.models.DnsResult

data class DnsLookupScreenState(
    val result: DnsResult? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
