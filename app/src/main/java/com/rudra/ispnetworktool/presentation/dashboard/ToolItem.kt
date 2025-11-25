package com.rudra.ispnetworktool.presentation.dashboard

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class ToolItem(
    val name: String,
    val icon: ImageVector,
    val route: String,
    val color: Color
)
