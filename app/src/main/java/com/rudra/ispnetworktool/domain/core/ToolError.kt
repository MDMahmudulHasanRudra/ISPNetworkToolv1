package com.rudra.ispnetworktool.domain.core

sealed class ToolError {
    object NetworkError : ToolError()
    object TimeoutError : ToolError()
    data class UnknownHostError(val host: String) : ToolError()
    data class ParsingError(val rawData: String) : ToolError()
    data class SecurityError(val message: String) : ToolError()
    data class UnknownError(val message: String) : ToolError()
}
