package com.rudra.ispnetworktool.domain.repository

import com.rudra.ispnetworktool.data.models.WhoisResult
import kotlinx.coroutines.flow.Flow

interface WhoisRepository {
    fun lookup(domain: String): Flow<WhoisResult>
}
