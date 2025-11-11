package com.rudra.ispnetworktool.presentation.portchecker

import com.rudra.ispnetworktool.data.models.PortResult

data class PortCheckerScreenState(
    val result: PortResult? = null,
    val isLoading: Boolean = false
)
