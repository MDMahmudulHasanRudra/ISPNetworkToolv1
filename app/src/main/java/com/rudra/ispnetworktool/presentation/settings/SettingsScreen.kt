package com.rudra.ispnetworktool.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            item {
                SectionTitle("Network")
                Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.shadow(4.dp, RoundedCornerShape(12.dp))){
                    SwitchSettingItem(
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
                    TextFieldSettingItem(
                        title = "Default Timeout (ms)",
                        value = state.defaultTimeout.toString(),
                        onValueChange = { viewModel.setDefaultTimeout(it.toIntOrNull() ?: 2000) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionTitle("Appearance")
                Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.shadow(4.dp, RoundedCornerShape(12.dp))) {
                    ThemeSettingItem(
                        selectedTheme = state.theme,
                        onThemeSelected = viewModel::setTheme
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionTitle("Privacy")
                Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.shadow(4.dp, RoundedCornerShape(12.dp))) {
                    SwitchSettingItem(
                        title = "Enable Logging",
                        subtitle = "Save tool results to history",
                        checked = state.isLoggingEnabled,
                        onCheckedChange = viewModel::setLoggingEnabled
                    )
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun SwitchSettingItem(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
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
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun ThemeSettingItem(selectedTheme: ThemeSetting, onThemeSelected: (ThemeSetting) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true }
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Theme", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text(text = "Current: ${selectedTheme.name.lowercase().replaceFirstChar { it.uppercase() }}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Theme")
    }

    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        ThemeSetting.values().forEach { theme ->
            DropdownMenuItem(text = { Text(theme.name) }, onClick = { 
                onThemeSelected(theme)
                expanded = false 
            })
        }
    }
}
