package com.rudra.ispnetworktool.domain.repository

import com.rudra.ispnetworktool.data.models.DnsResult
import kotlinx.coroutines.flow.Flow

enum class DnsRecordType {
    A, AAAA, CNAME, MX, TXT, SRV
}

interface DnsRepository {
    fun lookup(host: String, recordType: DnsRecordType): Flow<DnsResult>
}
