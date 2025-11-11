package com.rudra.ispnetworktool.presentation.subnet

import com.rudra.ispnetworktool.data.models.SubnetInfo

data class SubnetCalculatorScreenState(
    val subnetInfo: SubnetInfo? = null,
    val error: String? = null
)
