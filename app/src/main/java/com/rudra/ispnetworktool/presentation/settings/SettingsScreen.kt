package com.rudra.ispnetworktool.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    var showClearHistoryDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            item {
                SettingsHeader("Network & Tools")
                SettingsGroup {
                    SwitchSettingItem(
                        icon = Icons.Default.Dns,
                        title = "Use Custom DNS",
                        subtitle = "Override system DNS with a custom server",
                        checked = state.useCustomDns,
                        onCheckedChange = viewModel::setUseCustomDns
                    )
                    if (state.useCustomDns) {
                        TextFieldSettingItem(
                            title = "Custom DNS Server",
                            value = state.customDnsServer,
                            onValueChange = viewModel::setCustomDnsServer
                        )
                    }
                    ClickableSettingItem(
                        icon = Icons.Default.Timer,
                        title = "Default Timeout",
                        subtitle = "${state.defaultTimeout} ms",
                        onClick = { /* Could show a picker dialog */ }
                    )
                }
            }

            item {
                SettingsHeader("Appearance")
                SettingsGroup {
                    ThemeSettingItem(
                        selectedTheme = state.theme,
                        onThemeSelected = viewModel::setTheme
                    )
                }
            }

            item {
                SettingsHeader("Data & Privacy")
                SettingsGroup {
                    SwitchSettingItem(
                        icon = Icons.Default.History,
                        title = "Enable Logging",
                        subtitle = "Save tool results to history automatically",
                        checked = state.isLoggingEnabled,
                        onCheckedChange = viewModel::setLoggingEnabled
                    )
                    ClickableSettingItem(
                        icon = Icons.Default.DeleteSweep,
                        title = "Clear History",
                        subtitle = "Delete all saved logs and results",
                        onClick = { showClearHistoryDialog = true },
                        contentColor = MaterialTheme.colorScheme.error
                    )
                }
            }

            item {
                SettingsHeader("About")
                SettingsGroup {
                    InfoSettingItem(
                        icon = Icons.Default.Info,
                        title = "Version",
                        subtitle = state.appVersion
                    )
                    ClickableSettingItem(
                        icon = Icons.Default.Star,
                        title = "Rate the App",
                        subtitle = "Support us by leaving a review",
                        onClick = { /* Open Play Store */ }
                    )
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text("Clear History") },
            text = { Text("Are you sure you want to delete all tool history? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearHistory()
                        showClearHistoryDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Clear All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SettingsHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsGroup(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 1.dp,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Column(content = content)
    }
}

@Composable
fun SwitchSettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun ClickableSettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    contentColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (contentColor == MaterialTheme.colorScheme.error) contentColor else MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = contentColor)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray.copy(alpha = 0.5f))
    }
}

@Composable
fun InfoSettingItem(icon: ImageVector, title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun TextFieldSettingItem(title: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(title) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun ThemeSettingItem(selectedTheme: ThemeSetting, onThemeSelected: (ThemeSetting) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = when(selectedTheme) {
                ThemeSetting.LIGHT -> Icons.Default.LightMode
                ThemeSetting.DARK -> Icons.Default.DarkMode
                ThemeSetting.SYSTEM -> Icons.Default.SettingsSuggest
            },
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Theme", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text(
                text = "Current: ${selectedTheme.name.lowercase().replaceFirstChar { it.uppercase() }}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Box {
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Theme")
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                ThemeSetting.values().forEach { theme ->
                    DropdownMenuItem(
                        text = { Text(theme.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        onClick = {
                            onThemeSelected(theme)
                            expanded = false
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = when(theme) {
                                    ThemeSetting.LIGHT -> Icons.Default.LightMode
                                    ThemeSetting.DARK -> Icons.Default.DarkMode
                                    ThemeSetting.SYSTEM -> Icons.Default.SettingsSuggest
                                },
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        }
    }
}
