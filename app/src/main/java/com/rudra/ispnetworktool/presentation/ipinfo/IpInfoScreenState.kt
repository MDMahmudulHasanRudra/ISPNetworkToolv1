package com.rudra.ispnetworktool.presentation.ipinfo

import com.rudra.ispnetworktool.data.models.IpInfo

data class IpInfoScreenState(
    val ipInfo: IpInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
