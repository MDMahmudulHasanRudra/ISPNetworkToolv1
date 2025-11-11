package com.rudra.ispnetworktool.presentation.traceroute

import com.rudra.ispnetworktool.data.models.TracerouteResult

data class TracerouteScreenState(
    val results: List<TracerouteResult> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
