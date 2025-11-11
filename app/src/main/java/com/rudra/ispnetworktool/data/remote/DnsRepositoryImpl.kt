package com.rudra.ispnetworktool.data.remote

import com.rudra.ispnetworktool.data.models.DnsResult
import com.rudra.ispnetworktool.domain.repository.DnsRecordType
import com.rudra.ispnetworktool.domain.repository.DnsRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.net.InetAddress

class DnsRepositoryImpl(private val client: HttpClient) : DnsRepository {

    // Test: Should emit InProgress, then Success with a list of records.
    // Test: Should handle unknown host correctly.
    // Test: Should handle exceptions and emit Failure.
    override fun lookup(host: String, recordType: DnsRecordType): Flow<DnsResult> = flow {
        emit(DnsResult.InProgress)
        try {
            val records = when (recordType) {
                DnsRecordType.A, DnsRecordType.AAAA -> {
                    val addresses = InetAddress.getAllByName(host)
                    addresses.map { it.hostAddress ?: "" }.filter { it.isNotEmpty() }
                }
                else -> fetchFromDohApi(host, recordType)
            }
            emit(DnsResult.Success(records))
        } catch (e: Exception) {
            emit(DnsResult.Failure(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    // Test: Should return a list of records for a valid host and record type.
    // Test: Should handle empty or null Answer from the API.
    // Test: Should handle network errors and exceptions.
    private suspend fun fetchFromDohApi(host: String, recordType: DnsRecordType): List<String> {
        val response: DohResponse = client.get("https://cloudflare-dns.com/dns-query") {
            parameter("name", host)
            parameter("type", recordType.name)
            headers.append("accept", "application/dns-json")
        }.body()

        return response.Answer?.map { it.data } ?: emptyList()
    }
}

@kotlinx.serialization.Serializable
data class DohResponse(
    val Answer: List<DohAnswer>? = null
)

@kotlinx.serialization.Serializable
data class DohAnswer(
    val data: String
)
