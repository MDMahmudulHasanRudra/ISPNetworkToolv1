package com.rudra.ispnetworktool.presentation.whois

import com.rudra.ispnetworktool.data.models.WhoisResult

data class WhoisScreenState(
    val result: WhoisResult? = null,
    val isLoading: Boolean = false
)
