package com.rudra.ispnetworktool.presentation.ip_planning_chart

import androidx.compose.foundation.background
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
fun IpPlanningChartScreen(
    viewModel: IpPlanningChartViewModel = hiltViewModel()
) {
    val state = viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("IP Planning Chart") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(
                        onClick = viewModel::exportPlan,
                        enabled = state.networkPlans.isNotEmpty()
                    ) {
                        Icon(Icons.Default.Download, contentDescription = "Export")
                    }
                    IconButton(onClick = viewModel::clearAllPlans) {
                        Icon(Icons.Default.ClearAll, contentDescription = "Clear All")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.generatePlan() },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
                text = { Text("Generate Plan") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
//                enabled = state.isValidConfiguration && !state.isLoading
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Configuration Section
            PlanningConfigurationSection(state, viewModel)

            Spacer(modifier = Modifier.height(16.dp))

            // Generated Plans
            if (state.networkPlans.isNotEmpty()) {
                NetworkPlansSection(state, viewModel)
            }

            // Empty State
            if (state.networkPlans.isEmpty() && !state.isLoading) {
                EmptyPlanningState()
            }

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
                        Text("Generating network plan...")
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
private fun PlanningConfigurationSection(
    state: IpPlanningChartState,
    viewModel: IpPlanningChartViewModel
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
                text = "Network Planning Configuration",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Base Network Configuration
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = state.baseNetwork,
                    onValueChange = viewModel::updateBaseNetwork,
                    label = { Text("Base Network (CIDR)") },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("192.168.0.0/16") },
                    singleLine = true,
                    isError = state.baseNetworkError != null
                )

                OutlinedTextField(
                    value = state.subnetPrefix.toString(),
                    onValueChange = { viewModel.updateSubnetPrefix(it.toIntOrNull() ?: 24) },
                    label = { Text("Subnet Prefix") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    isError = state.subnetPrefixError != null
                )
            }

            state.baseNetworkError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Network Segments
            Text(
                text = "Network Segments",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            state.networkSegments.forEachIndexed { index, segment ->
                NetworkSegmentRow(
                    segment = segment,
                    onUpdate = { updated -> viewModel.updateNetworkSegment(index, updated) },
                    onRemove = { viewModel.removeNetworkSegment(index) },
                    showRemove = state.networkSegments.size > 1
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = viewModel::addNetworkSegment,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Segment")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Network Segment")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Planning Strategy
            Text(
                text = "Planning Strategy",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PlanningStrategy.entries.forEach { strategy ->
                    FilterChip(
                        selected = state.planningStrategy == strategy,
                        onClick = { viewModel.updatePlanningStrategy(strategy) },
                        label = { Text(strategy.displayName) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Advanced Options
            var expanded by remember { mutableStateOf(false) }
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
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
                            // Reserve IPs
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Reserve IPs for infrastructure",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                Switch(
                                    checked = state.reserveInfrastructureIps,
                                    onCheckedChange = viewModel::updateReserveInfrastructureIps
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Growth Buffer
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Growth Buffer: ${state.growthBuffer}%",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                Slider(
                                    value = state.growthBuffer.toFloat(),
                                    onValueChange = { viewModel.updateGrowthBuffer(it.toInt()) },
                                    valueRange = 0f..50f,
                                    steps = 49
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // VLAN Support
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Include VLAN IDs",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                Switch(
                                    checked = state.includeVlans,
                                    onCheckedChange = viewModel::updateIncludeVlans
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Summary
            PlanningSummary(state)
        }
    }
}

@Composable
private fun NetworkSegmentRow(
    segment: NetworkSegment,
    onUpdate: (NetworkSegment) -> Unit,
    onRemove: () -> Unit,
    showRemove: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = segment.name,
                    onValueChange = { onUpdate(segment.copy(name = it)) },
                    label = { Text("Segment Name") },
                    modifier = Modifier.weight(2f),
                    placeholder = { Text("e.g., Servers, Users, IoT") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = segment.requiredHosts.toString(),
                    onValueChange = {
                        val hosts = it.toIntOrNull() ?: 0
                        onUpdate(segment.copy(requiredHosts = hosts))
                    },
                    label = { Text("Hosts") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                if (showRemove) {
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Remove")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = segment.description,
                onValueChange = { onUpdate(segment.copy(description = it)) },
                label = { Text("Description (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Purpose of this network segment") },
                singleLine = true
            )
        }
    }
}

@Composable
private fun PlanningSummary(state: IpPlanningChartState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Planning Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(12.dp))

            val totalRequiredHosts = state.networkSegments.sumOf { it.requiredHosts }
            val estimatedHosts = (totalRequiredHosts * (1 + state.growthBuffer / 100.0)).toInt()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                SummaryItem("Total Segments", state.networkSegments.size.toString())
                SummaryItem("Required Hosts", totalRequiredHosts.toString())
                SummaryItem("With Growth", estimatedHosts.toString())
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Base Network: ${state.baseNetwork}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "Strategy: ${state.planningStrategy.displayName}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun NetworkPlansSection(
    state: IpPlanningChartState,
    viewModel: IpPlanningChartViewModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Generated Network Plans",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${state.networkPlans.size} plans",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            state.networkPlans.forEachIndexed { index, plan ->
                NetworkPlanItem(
                    plan = plan,
                    index = index,
                    onDelete = { viewModel.deletePlan(index) },
                    onCopy = { viewModel.copyPlanToClipboard(plan) }
                )
                if (index < state.networkPlans.size - 1) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                }
            }
        }
    }
}

@Composable
private fun NetworkPlanItem(
    plan: NetworkPlan,
    index: Int,
    onDelete: () -> Unit,
    onCopy: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Plan #${index + 1}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row {
                    IconButton(onClick = onCopy) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Plan details
            PlanDetailRow("Base Network", plan.baseNetwork)
            PlanDetailRow("Strategy", plan.planningStrategy.displayName)
            PlanDetailRow("Total Segments", plan.segments.size.toString())
            PlanDetailRow("Utilization", "${plan.utilizationPercentage}%")

            Spacer(modifier = Modifier.height(8.dp))

            // Segment breakdown
            Text(
                text = "Network Segments:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            plan.segments.forEach { segment ->
                SegmentDetailRow(segment)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Visual utilization chart
            UtilizationChart(plan.segments)
        }
    }
}

@Composable
private fun UtilizationChart(segments: List<NetworkSegmentPlan>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = "Address Space Utilization",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            segments.forEach { segment ->
                UtilizationBar(segment)
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun UtilizationBar(segment: NetworkSegmentPlan) {
    val utilization = segment.utilization.coerceIn(0f, 1f)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = segment.name,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.width(80.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .height(12.dp)
                .background(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    shape = MaterialTheme.shapes.small
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(utilization)
                    .height(12.dp)
                    .background(
                        color = when {
                            utilization > 0.8f -> MaterialTheme.colorScheme.error
                            utilization > 0.6f -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.primary
                        },
                        shape = MaterialTheme.shapes.small
                    )
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "${(utilization * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.width(30.dp)
        )
    }
}

@Composable
private fun PlanDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(100.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SegmentDetailRow(segment: NetworkSegmentPlan) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(
            text = segment.name,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = "${segment.assignedHosts} hosts",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = segment.subnet,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
        )
    }
}

@Composable
private fun EmptyPlanningState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.HourglassEmpty,
                contentDescription = "No plans",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Network Plans Generated",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Configure your network segments and click 'Generate Plan' to create IP allocation plans.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SummaryItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}