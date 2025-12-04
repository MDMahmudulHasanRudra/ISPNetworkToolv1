package com.rudra.ispnetworktool.presentation.osi_tcp_ip_models

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OsiTcpIpModelsScreen(
    viewModel: OsiTcpIpModelsViewModel = hiltViewModel()
) {
    val state = viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("OSI & TCP/IP Models") },
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
        ) {
            TabRow(
                selectedTabIndex = state.selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Tab(
                    selected = state.selectedTabIndex == 0,
                    onClick = { viewModel.onTabSelected(0) },
                    text = { Text("OSI Model") }
                )
                Tab(
                    selected = state.selectedTabIndex == 1,
                    onClick = { viewModel.onTabSelected(1) },
                    text = { Text("TCP/IP Model") }
                )
            }

            when (state.selectedTabIndex) {
                0 -> ModelLayersList(layers = state.osiLayers)
                1 -> ModelLayersList(layers = state.tcpIpLayers)
            }
        }
    }
}

@Composable
fun ModelLayersList(layers: List<OsiAndTcpIpLayer>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(layers) { layer ->
            LayerCard(layer)
        }
    }
}

@Composable
fun LayerCard(layer: OsiAndTcpIpLayer) {
    val alpha by animateFloatAsState(targetValue = 1f, label = "")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Layer ${layer.layerNumber}: ${layer.name}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = layer.description,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            InfoRow(label = "Devices", value = layer.devices)
            InfoRow(label = "Protocols", value = layer.protocols)
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = "$label:",
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(80.dp)
        )
        Text(text = value)
    }
}
