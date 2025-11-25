package com.rudra.ispnetworktool.presentation.hotspot_setup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotspotSetupScreen(
    viewModel: HotspotSetupViewModel = hiltViewModel()
) {
    val state = viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hotspot Setup") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(
                        onClick = viewModel::refreshStatus,
                        enabled = !state.isLoading
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        floatingActionButton = {
            if (state.hotspotEnabled) {
                FloatingActionButton(
                    onClick = { viewModel.stopHotspot() },
                    containerColor = MaterialTheme.colorScheme.error
                ) {
                    Icon(Icons.Default.PowerOff, contentDescription = "Stop Hotspot")
                }
            } else {
                val isEnabled = state.isConfigurationValid && !state.isLoading
                FloatingActionButton(
                    onClick = { if (isEnabled) viewModel.startHotspot() },
                    containerColor = if (isEnabled) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Start Hotspot")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Status Card
            HotspotStatusCard(state)

            Spacer(modifier = Modifier.height(16.dp))

            // Configuration Section
            if (!state.hotspotEnabled) {
                HotspotConfigurationSection(state, viewModel)
            } else {
                ConnectedClientsSection(state)
            }

            // Quick Setup Templates
            Spacer(modifier = Modifier.height(16.dp))
            QuickSetupTemplates(viewModel, enabled = !state.hotspotEnabled)

            // Loading State
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (state.hotspotEnabled) "Stopping hotspot..." else "Starting hotspot...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Error Message
            state.error?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HotspotStatusCard(
    state: HotspotSetupState
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (state.hotspotEnabled) {
                MaterialTheme.colorScheme.tertiaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (state.hotspotEnabled) "Hotspot Active" else "Hotspot Ready",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (state.hotspotEnabled) "Sharing your internet connection" else "Configure and start hotspot",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector = if (state.hotspotEnabled) Icons.Default.Wifi else Icons.Default.WifiOff,
                    contentDescription = "Hotspot Status",
                    modifier = Modifier.size(32.dp),
                    tint = if (state.hotspotEnabled) MaterialTheme.colorScheme.tertiary
                    else MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (state.hotspotEnabled) {
                // Active hotspot info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    InfoItem("SSID", state.ssid)
                    InfoItem("Band", state.band.displayName)
                    InfoItem("Clients", state.connectedClients.size.toString())
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Hotspot statistics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    InfoItem("Uptime", state.uptime)
                    InfoItem("Data Used", state.dataUsage)
                    InfoItem("Security", state.securityType.displayName)
                }
            } else {
                // Configuration summary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    InfoItem("SSID", state.ssid.ifBlank { "Not set" })
                    InfoItem("Security", state.securityType.displayName)
                    InfoItem("Band", state.band.displayName)
                }
            }
        }
    }
}

@Composable
private fun HotspotConfigurationSection(
    state: HotspotSetupState,
    viewModel: HotspotSetupViewModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Hotspot Configuration",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // SSID Configuration
            OutlinedTextField(
                value = state.ssid,
                onValueChange = viewModel::updateSsid,
                label = { Text("Network Name (SSID)") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("MyHotspot") },
                singleLine = true,
                isError = state.ssidError != null
            )
            state.ssidError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Security Type
            Text(
                text = "Security Type",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SecurityType.entries.forEach { security ->
                    FilterChip(
                        selected = state.securityType == security,
                        onClick = { viewModel.updateSecurityType(security) },
                        label = { Text(security.displayName) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Password (if security is not Open)
            if (state.securityType != SecurityType.OPEN) {
                OutlinedTextField(
                    value = state.password,
                    onValueChange = viewModel::updatePassword,
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter secure password") },
                    singleLine = true,
                    isError = state.passwordError != null
                )
                state.passwordError?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                PasswordStrengthIndicator(state.passwordStrength)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Band Selection
            Text(
                text = "Wi-Fi Band",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WifiBand.entries.forEach { band ->
                    FilterChip(
                        selected = state.band == band,
                        onClick = { viewModel.updateBand(band) },
                        label = { Text(band.displayName) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Advanced Options
            var expanded by remember { mutableStateOf(false) }
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column {
                    ListItem(
                        headlineContent = { Text("Advanced Options") },
                        trailingContent = {
                            IconButton(onClick = { expanded = !expanded }) {
                                Icon(
                                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = if (expanded) "Collapse" else "Expand"
                                )
                            }
                        }
                    )

                    if (expanded) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                            // Max Clients
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Max Clients: ${state.maxClients}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                Slider(
                                    value = state.maxClients.toFloat(),
                                    onValueChange = { viewModel.updateMaxClients(it.toInt()) },
                                    valueRange = 1f..10f,
                                    steps = 8
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Hide SSID
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Hide SSID",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                Switch(
                                    checked = state.hidden,
                                    onCheckedChange = viewModel::updateHidden
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConnectedClientsSection(state: HotspotSetupState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Connected Clients (${state.connectedClients.size})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (state.connectedClients.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No clients connected",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                state.connectedClients.forEach { client ->
                    ConnectedClientItem(client = client)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}

@Composable
private fun ConnectedClientItem(client: ConnectedClient) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Devices,
            contentDescription = "Client Device",
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = client.deviceName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "IP: ${client.ipAddress} • MAC: ${client.macAddress}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = client.connectionTime,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun QuickSetupTemplates(
    viewModel: HotspotSetupViewModel,
    enabled: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Quick Setup Templates",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickTemplateButton(
                    modifier = Modifier.weight(1f),
                    title = "Home Use",
                    description = "WPA2, 2.4GHz",
                    onClick = { viewModel.applyTemplate(HotspotTemplate.HOME) },
                    enabled = enabled
                )

                QuickTemplateButton(
                    modifier = Modifier.weight(1f),
                    title = "Guest",
                    description = "Open, Limited",
                    onClick = { viewModel.applyTemplate(HotspotTemplate.GUEST) },
                    enabled = enabled
                )

                QuickTemplateButton(
                    modifier = Modifier.weight(1f),
                    title = "Secure",
                    description = "WPA3, Hidden",
                    onClick = { viewModel.applyTemplate(HotspotTemplate.SECURE) },
                    enabled = enabled
                )
            }
        }
    }
}

@Composable
private fun QuickTemplateButton(
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PasswordStrengthIndicator(strength: PasswordStrength) {
    val color = when (strength) {
        PasswordStrength.WEAK -> MaterialTheme.colorScheme.error
        PasswordStrength.MEDIUM -> MaterialTheme.colorScheme.onSurfaceVariant
        PasswordStrength.STRONG -> MaterialTheme.colorScheme.tertiary
        PasswordStrength.VERY_STRONG -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline
    }

    val text = when (strength) {
        PasswordStrength.WEAK -> "Weak password"
        PasswordStrength.MEDIUM -> "Medium strength"
        PasswordStrength.STRONG -> "Strong password"
        PasswordStrength.VERY_STRONG -> "Very strong password"
        else -> "Enter password"
    }

    Column {
        LinearProgressIndicator(
            progress = strength.strength,
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            color = color
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
private fun InfoItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
