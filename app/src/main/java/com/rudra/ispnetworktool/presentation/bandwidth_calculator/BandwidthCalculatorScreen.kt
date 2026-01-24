package com.rudra.ispnetworktool.presentation.bandwidth_calculator

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BandwidthCalculatorScreen(
    viewModel: BandwidthCalculatorViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bandwidth Capacity Planning", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Subscriber Packages",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            itemsIndexed(state.packages) { index, pkg ->
                PackageInputItem(
                    speed = pkg.speedMbps,
                    users = pkg.users,
                    onChanged = { s, u -> viewModel.onPackageChanged(index, s, u) },
                    onRemove = { viewModel.onPackageRemoved(index) }
                )
            }

            item {
                Button(
                    onClick = { viewModel.onPackageAdded() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Add Package")
                }
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    "Planning Parameters",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = state.contentionRatio.toString(),
                        onValueChange = { viewModel.onContentionRatioChanged(it.toIntOrNull() ?: 0) },
                        label = { Text("Contention Ratio (1:X)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = state.bufferPercent.toString(),
                        onValueChange = { viewModel.onBufferChanged(it.toIntOrNull() ?: 0) },
                        label = { Text("Buffer %") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }

            item {
                Text(
                    "Services Enabled",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ServiceChip("IPT", state.services.ipt) { viewModel.onServiceToggle("ipt") }
                    ServiceChip("CDN", state.services.cdn) { viewModel.onServiceToggle("cdn") }
                    ServiceChip("GGC", state.services.ggc) { viewModel.onServiceToggle("ggc") }
                    ServiceChip("FNA", state.services.fna) { viewModel.onServiceToggle("fna") }
                    ServiceChip("BDIX", state.services.bdix) { viewModel.onServiceToggle("bdix") }
                    ServiceChip("Baishan", state.services.baishan) { viewModel.onServiceToggle("baishan") }
                }
            }

            item {
                ResultsCard(state.result)
            }
        }
    }
}

@Composable
fun PackageInputItem(
    speed: Int,
    users: Int,
    onChanged: (Int?, Int?) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = speed.toString(),
                onValueChange = { onChanged(it.toIntOrNull(), null) },
                label = { Text("Mbps") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = users.toString(),
                onValueChange = { onChanged(null, it.toIntOrNull()) },
                label = { Text("Users") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ServiceChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) }
    )
}

@Composable
fun ResultsCard(result: com.rudra.ispnetworktool.domain.model.BandwidthResult) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Capacity Estimation",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            ResultRow("Total Subscribers", "${result.totalSubscribers}")
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
            
            ResultRow("IPT Bandwidth", "%.2f Mbps".format(result.ipt))
            ResultRow("CDN Bandwidth", "%.2f Mbps".format(result.cdn))
            ResultRow("GGC (Google)", "%.2f Mbps".format(result.ggc))
            ResultRow("FNA (Facebook)", "%.2f Mbps".format(result.fna))
            ResultRow("BDIX Peering", "%.2f Mbps".format(result.bdix))
            ResultRow("Baishan", "%.2f Mbps".format(result.baishan))
            
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "TOTAL CAPACITY",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    "%.2f Mbps".format(result.total),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF2E7D32) // Dark Green
                )
            }
        }
    }
}

@Composable
fun ResultRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
}
