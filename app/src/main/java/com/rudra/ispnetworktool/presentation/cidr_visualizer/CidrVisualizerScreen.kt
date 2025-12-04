package com.rudra.ispnetworktool.presentation.cidr_visualizer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CidrVisualizerScreen(
    viewModel: CidrVisualizerViewModel = hiltViewModel()
) {
    val state by viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CIDR Visualizer") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Input Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Enter CIDR Notation",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = state.cidrAddress,
                        onValueChange = viewModel::onCidrAddressChanged,
                        label = { Text("CIDR Address (e.g., 192.168.1.0/24)") },
                        placeholder = { Text("192.168.1.0/24") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = state.error != null,
                        singleLine = true,
                        trailingIcon = {
                            if (state.error != null) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = "Error",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )

                    if (state.error != null) {
                        Text(
                            text = state.error!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = viewModel::visualizeCidr,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = state.cidrAddress.isNotBlank() && !state.isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Processing...")
                        } else {
                            Text("Visualize CIDR")
                        }
                    }

                    // Examples
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Examples:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "• 192.168.1.0/24\n• 10.0.0.0/8\n• 172.16.0.0/12",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Results Section
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Analyzing CIDR notation...")
                    }
                }
            } else if (state.cidrInfo != null) {
                CidrInfoCard(info = state.cidrInfo!!)

                // Additional visualization
                Spacer(modifier = Modifier.height(16.dp))
                NetworkRangeVisualizer(info = state.cidrInfo!!)
            } else {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "Info",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Enter a CIDR notation to visualize",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CidrInfoCard(info: CidrInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Network Information",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Basic Information
            InfoRow("CIDR Notation", info.cidrNotation)
            InfoRow("Network Address", info.networkAddress)
            InfoRow("Broadcast Address", info.broadcastAddress)
            InfoRow("Subnet Mask", info.subnetMask)
            InfoRow("Wildcard Mask", info.wildcardMask)
            InfoRow("CIDR Prefix", "/${info.prefixLength}")

            Spacer(modifier = Modifier.height(8.dp))

            // Host Information
            Text(
                text = "Host Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            InfoRow("First Usable Host", info.firstUsableHost)
            InfoRow("Last Usable Host", info.lastUsableHost)
            InfoRow("Total Hosts", formatNumber(info.totalHosts))
            InfoRow("Usable Hosts", formatNumber(info.usableHosts))

            Spacer(modifier = Modifier.height(8.dp))

            // IP Address Information
            Text(
                text = "IP Address Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            InfoRow("IP Version", if (info.isIpv6) "IPv6" else "IPv4")
            InfoRow("Address Range", "${info.networkAddress} - ${info.broadcastAddress}")
            InfoRow("Network Class", info.networkClass)
        }
    }
}

@Composable
fun NetworkRangeVisualizer(info: CidrInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Network Range",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Simple visual representation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text("Start", style = MaterialTheme.typography.labelSmall)
                    Text(info.networkAddress, style = MaterialTheme.typography.bodySmall)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Range", style = MaterialTheme.typography.labelSmall)
                    Text("${info.usableHosts} hosts", style = MaterialTheme.typography.bodySmall)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("End", style = MaterialTheme.typography.labelSmall)
                    Text(info.broadcastAddress, style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Visual bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        shape = MaterialTheme.shapes.small
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.small
                        )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Network", style = MaterialTheme.typography.labelSmall)
                Text("Usable Range", style = MaterialTheme.typography.labelSmall)
                Text("Broadcast", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun formatNumber(number: Long): String {
    return when {
        number >= 1_000_000 -> "${number / 1_000_000}M"
        number >= 1_000 -> "${number / 1_000}K"
        else -> number.toString()
    }
}
