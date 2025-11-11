package com.rudra.ispnetworktool.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tool_logs")
data class ToolLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val toolType: String,
    val target: String,
    val timestamp: Long,
    val summary: String,
    val resultJson: String
)
