package com.rudra.ispnetworktool.presentation.ping

import com.rudra.ispnetworktool.data.models.PingResult

data class PingScreenState(
    val results: List<PingResult> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
