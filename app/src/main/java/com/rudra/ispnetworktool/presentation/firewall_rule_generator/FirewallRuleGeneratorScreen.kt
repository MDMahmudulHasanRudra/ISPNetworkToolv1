package com.rudra.ispnetworktool.presentation.firewall_rule_generator

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
fun FirewallRuleGeneratorScreen(
    viewModel: FirewallRuleGeneratorViewModel = hiltViewModel()
) {
    val state = viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Firewall Rule Generator") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(
                        onClick = viewModel::clearAllRules,
                        enabled = state.generatedRules.isNotEmpty()
                    ) {
                        Icon(Icons.Default.ClearAll, contentDescription = "Clear All")
                    }
                }
            )
        },
        floatingActionButton = {
            if (state.generatedRules.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = viewModel::exportRules,
                    icon = { Icon(Icons.Default.Download, contentDescription = "Export") },
                    text = { Text("Export Rules") },
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Rule Configuration Section
            RuleConfigurationSection(state, viewModel)

            Spacer(modifier = Modifier.height(16.dp))

            // Generated Rules Section
            GeneratedRulesSection(state, viewModel)

            // Empty State
            if (state.generatedRules.isEmpty() && !state.isLoading) {
                EmptyState()
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
                        Text("Generating firewall rules...")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RuleConfigurationSection(
    state: FirewallRuleGeneratorState,
    viewModel: FirewallRuleGeneratorViewModel
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
                text = "Rule Configuration",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Rule Type Selection
            Text(
                text = "Rule Type",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RuleType.entries.forEach { ruleType ->
                    FilterChip(
                        selected = state.ruleType == ruleType,
                        onClick = { viewModel.updateRuleType(ruleType) },
                        label = { Text(ruleType.displayName) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Source and Destination
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = state.sourceIp,
                    onValueChange = viewModel::updateSourceIp,
                    label = { Text("Source IP/CIDR") },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("192.168.1.0/24") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = state.destinationIp,
                    onValueChange = viewModel::updateDestinationIp,
                    label = { Text("Destination IP/CIDR") },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("10.0.0.5") },
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Ports and Protocol
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = state.ports,
                    onValueChange = viewModel::updatePorts,
                    label = { Text("Port(s)") },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("80,443,8080") },
                    singleLine = true
                )

                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = state.protocol.displayName,
                        onValueChange = { },
                        label = { Text("Protocol") },
                        modifier = Modifier.menuAnchor(),
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        singleLine = true
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        Protocol.entries.forEach { protocol ->
                            DropdownMenuItem(
                                text = { Text(protocol.displayName) },
                                onClick = {
                                    viewModel.updateProtocol(protocol)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action and Options
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = state.action.displayName,
                        onValueChange = { },
                        label = { Text("Action") },
                        modifier = Modifier.menuAnchor(),
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        singleLine = true
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        RuleAction.entries.forEach { action ->
                            DropdownMenuItem(
                                text = { Text(action.displayName) },
                                onClick = {
                                    viewModel.updateAction(action)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = state.interfaceName,
                    onValueChange = viewModel::updateInterface,
                    label = { Text("Interface") },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("eth0, wan, lan") },
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            OutlinedTextField(
                value = state.description,
                onValueChange = viewModel::updateDescription,
                label = { Text("Rule Description") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Allow web traffic from local network") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Options
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AssistChip(
                    onClick = { viewModel.toggleLogging() },
                    label = { Text("Log Packets") },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (state.enableLogging) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    ),
                    leadingIcon = {
                        Icon(
                            Icons.Default.Security,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )

                AssistChip(
                    onClick = { viewModel.toggleEnabled() },
                    label = { Text(if (state.enabled) "Enabled" else "Disabled") },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (state.enabled) {
                            MaterialTheme.colorScheme.tertiaryContainer
                        } else {
                            MaterialTheme.colorScheme.errorContainer
                        }
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Generate Button
            Button(
                onClick = { viewModel.generateRule() },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.isValidConfiguration && !state.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate Firewall Rule")
            }

            // Error message
            if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun GeneratedRulesSection(
    state: FirewallRuleGeneratorState,
    viewModel: FirewallRuleGeneratorViewModel
) {
    if (state.generatedRules.isNotEmpty()) {
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
                        text = "Generated Rules (${state.generatedRules.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Firewall: ${state.selectedFirewall.displayName}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Firewall Type Selection
                Text(
                    text = "Target Firewall",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FirewallType.entries.forEach { firewall ->
                        FilterChip(
                            selected = state.selectedFirewall == firewall,
                            onClick = { viewModel.updateFirewallType(firewall) },
                            label = { Text(firewall.displayName) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Generated Rules List
                state.generatedRules.forEachIndexed { index, rule ->
                    GeneratedRuleItem(
                        rule = rule,
                        index = index,
                        onDelete = { viewModel.deleteRule(index) },
                        onCopy = { viewModel.copyRuleToClipboard(rule) }
                    )
                    if (index < state.generatedRules.size - 1) {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun GeneratedRuleItem(
    rule: FirewallRule,
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
                    text = "Rule #${index + 1}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row {
                    IconButton(onClick = onCopy) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy Rule")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Rule")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = rule.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = rule.command,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.ListAlt,
                contentDescription = null,
                modifier = Modifier.size(128.dp),
                tint = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "No firewall rules generated yet",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                "Configure a rule and click 'Generate' to see it here.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}