package com.rudra.ispnetworktool.data.local

import com.rudra.ispnetworktool.data.models.WhoisResult
import com.rudra.ispnetworktool.domain.repository.WhoisRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class WhoisRepositoryImpl : WhoisRepository {

    override fun lookup(domain: String): Flow<WhoisResult> = flow {
        emit(WhoisResult.InProgress)
        try {
            val whoisServer = "whois.iana.org"
            val socket = Socket(whoisServer, 43)
            val writer = PrintWriter(socket.getOutputStream(), true)
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

            writer.println(domain)

            val response = reader.readText()
            socket.close()

            emit(WhoisResult.Success(response))
        } catch (e: Exception) {
            emit(WhoisResult.Failure(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)
}
