package com.rudra.ispnetworktool.data.local

import com.rudra.ispnetworktool.data.models.PortResult
import com.rudra.ispnetworktool.domain.repository.PortCheckerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.net.InetSocketAddress
import java.net.Socket

class PortCheckerRepositoryImpl : PortCheckerRepository {

    override fun checkPort(host: String, port: Int): Flow<PortResult> = flow {
        try {
            Socket().use {
                it.connect(InetSocketAddress(host, port), 2000) // 2 second timeout
                if (it.isConnected) {
                    emit(PortResult.Open)
                } else {
                    emit(PortResult.Closed)
                }
            }
        } catch (e: Exception) {
            emit(PortResult.Error(e.message ?: "An unknown error occurred"))
        }
    }.flowOn(Dispatchers.IO)
}
