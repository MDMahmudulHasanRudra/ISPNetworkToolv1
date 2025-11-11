package com.rudra.ispnetworktool.domain.core

sealed class ToolResult<out T> {
    data class Success<T>(val data: T) : ToolResult<T>()
    data class Error(val error: ToolError) : ToolResult<Nothing>()
    object Loading : ToolResult<Nothing>()
}
