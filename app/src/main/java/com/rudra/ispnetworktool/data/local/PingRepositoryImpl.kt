package com.rudra.ispnetworktool.data.local

import com.rudra.ispnetworktool.data.models.PingResult
import com.rudra.ispnetworktool.domain.repository.PingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.IOException

class PingRepositoryImpl : PingRepository {

    private var process: Process? = null

    // Test: Should emit InProgress, then one or more Success, and finally Finished.
    // Test: Should handle unknown host correctly, emitting Failure.
    // Test: Should handle IOExceptions and emit Failure.
    override fun ping(host: String, count: Int): Flow<PingResult> = flow {
        emit(PingResult.InProgress)
        try {
            val command = listOf("ping", "-c", count.toString(), host)
            val processBuilder = ProcessBuilder(command)
            process = processBuilder.start()

            val reader = BufferedReader(InputStreamReader(process?.inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val rtt = parsePingOutput(line)
                if (rtt != null) {
                    emit(PingResult.Success(rtt))
                } else if (line!!.contains("unknown host") || line!!.contains("not found")) {
                    emit(PingResult.Failure("Unknown host: $host"))
                    break
                }
            }
            val exitCode = process?.waitFor()
            if (exitCode != 0) {
                // Handle other errors based on exit code if needed
            }
        } catch (e: IOException) {
            emit(PingResult.Failure(e.message ?: "An error occurred"))
        } finally {
            emit(PingResult.Finished)
            stopPing()
        }
    }.flowOn(Dispatchers.IO)

    // Test: Should destroy the process.
    override fun stopPing() {
        process?.destroy()
        process = null
    }

    // Test: Should parse valid ping output correctly.
    // Test: Should return null for invalid or null input.
    // Test: Should handle different formats of ping output from various Android versions.
    private fun parsePingOutput(line: String?): Float? {
        if (line == null || !line.contains("time=")) return null
        return try {
            val timeIndex = line.indexOf("time=")
            val timeSubstring = line.substring(timeIndex + 5)
            val timeValue = timeSubstring.split(" ").first()
            timeValue.toFloatOrNull()
        } catch (e: Exception) {
            null
        }
    }
}
