package com.rudra.ispnetworktool.data.local

import com.rudra.ispnetworktool.data.models.TracerouteResult
import com.rudra.ispnetworktool.domain.repository.TracerouteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketTimeoutException

class TracerouteRepositoryImpl : TracerouteRepository {

    private var socket: DatagramSocket? = null
    private var isStopped = false

    // Test: Should emit InProgress, then a series of Hop results, and finally Finished.
    // Test: Should handle destination reached correctly.
    // Test: Should handle timeouts correctly, emitting Hop with "*" and 0f rtt.
    // Test: Should handle IOExceptions and emit Failure.
    override fun traceroute(host: String): Flow<TracerouteResult> = flow {
        emit(TracerouteResult.InProgress)
        isStopped = false
        try {
            val destAddress = InetAddress.getByName(host)
            for (ttl in 1..30) {
                if (isStopped) break

                socket = DatagramSocket()
                // The TTL is set on the socket, but this is not a standard Java API feature.
                // For a true implementation, this would require JNI or a different library.
                // Here, we simulate the logic, but the underlying mechanism is missing.
                // This is a known limitation of this non-root approach in pure JVM.
                socket?.soTimeout = 3000 // 3 seconds timeout

                val sendData = ByteArray(0)
                val sendPacket = DatagramPacket(sendData, 0, destAddress, 33434 + ttl)

                val startTime = System.nanoTime()
                socket?.send(sendPacket)

                val receiveData = ByteArray(512)
                val receivePacket = DatagramPacket(receiveData, receiveData.size)

                try {
                    socket?.receive(receivePacket)
                    val rtt = (System.nanoTime() - startTime) / 1_000_000f
                    val hopIp = receivePacket.address.hostAddress
                    emit(TracerouteResult.Hop(ttl, hopIp ?: "*", rtt))

                    if (hopIp == destAddress.hostAddress) {
                        break // Destination reached
                    }
                } catch (e: SocketTimeoutException) {
                    emit(TracerouteResult.Hop(ttl, "*", 0f))
                } finally {
                    socket?.close()
                }
            }
        } catch (e: IOException) {
            emit(TracerouteResult.Failure(e.message ?: "An error occurred"))
        } finally {
            emit(TracerouteResult.Finished)
            stopTraceroute()
        }
    }.flowOn(Dispatchers.IO)

    // Test: Should set isStopped to true and close the socket.
    override fun stopTraceroute() {
        isStopped = true
        socket?.close()
        socket = null
    }
}
