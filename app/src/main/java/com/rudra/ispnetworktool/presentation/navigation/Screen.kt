package com.rudra.ispnetworktool.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Difference
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Filled.Dashboard)
    object Tools : Screen("tools", "Tools", Icons.Filled.Difference) // Using Home icon for Tools as a placeholder
    object History : Screen("history", "History", Icons.Filled.History)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings)
}
