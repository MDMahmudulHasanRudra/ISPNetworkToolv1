package com.rudra.ispnetworktool.data.remote

import com.rudra.ispnetworktool.data.models.WhoisResult
import com.rudra.ispnetworktool.domain.repository.WhoisRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class WhoisRepositoryImpl(private val client: HttpClient) : WhoisRepository {

    override fun lookup(domain: String): Flow<WhoisResult> = flow {
        emit(WhoisResult.InProgress)
        try {
            val response: String = client.get("https://www.whoisxmlapi.com/whoisserver/WhoisService?domainName=$domain&outputFormat=JSON").body()
            // In a real app, you would parse the JSON response. For this MVP, we will show the raw string.
            emit(WhoisResult.Success(response))
        } catch (e: Exception) {
            emit(WhoisResult.Failure(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)
}
