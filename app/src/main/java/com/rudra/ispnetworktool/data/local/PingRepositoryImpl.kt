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

    override fun ping(host: String, count: Int): Flow<PingResult> = flow {
        emit(PingResult.InProgress)
        try {
            // Reordered command: options should generally come before the host
            val command = mutableListOf("ping")
            if (count < Int.MAX_VALUE) {
                command.add("-c")
                command.add(count.toString())
            }
            command.add(host)
            
            val processBuilder = ProcessBuilder(command)
            processBuilder.redirectErrorStream(true)
            process = processBuilder.start()

            val reader = BufferedReader(InputStreamReader(process?.inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                if (line!!.isNotBlank()) {
                    val rtt = parsePingOutput(line)
                    emit(PingResult.Success(line!!, rtt))
                }
            }
            val exitCode = process?.waitFor()
        } catch (e: IOException) {
            emit(PingResult.Failure(e.message ?: "An error occurred"))
        } finally {
            emit(PingResult.Finished)
            stopPing()
        }
    }.flowOn(Dispatchers.IO)

    override fun stopPing() {
        process?.destroy()
        process = null
    }

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
