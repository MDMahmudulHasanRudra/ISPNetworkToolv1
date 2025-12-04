package com.rudra.ispnetworktool.presentation.vlan_planner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VlanPlannerScreen(
    viewModel: VlanPlannerViewModel = hiltViewModel()
) {
    val state = viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("VLAN Planner") },
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
        ) {
            AddVlanForm(viewModel, state)
            Spacer(modifier = Modifier.height(16.dp))
            VlanList(state, viewModel::onDeleteVlan)
        }
    }
}

@Composable
fun AddVlanForm(viewModel: VlanPlannerViewModel, state: VlanPlannerState) {
    Column {
        OutlinedTextField(
            value = state.vlanName,
            onValueChange = viewModel::onVlanNameChange,
            label = { Text("VLAN Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = state.vlanId,
                onValueChange = viewModel::onVlanIdChange,
                label = { Text("VLAN ID") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = state.vlanSubnet,
                onValueChange = viewModel::onVlanSubnetChange,
                label = { Text("Subnet (Optional)") },
                modifier = Modifier.weight(1f)
            )
        }
        state.errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = viewModel::onAddVlan,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add VLAN")
        }
    }
}

@Composable
fun VlanList(state: VlanPlannerState, onDeleteVlan: (Vlan) -> Unit) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(state.vlans) { vlan ->
            VlanListItem(vlan, onDeleteVlan)
        }
    }
}

@Composable
fun VlanListItem(vlan: Vlan, onDeleteVlan: (Vlan) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "${vlan.name} (ID: ${vlan.id})")
                vlan.subnet.takeIf { it.isNotBlank() }?.let {
                    Text(text = "Subnet: $it", style = MaterialTheme.typography.bodySmall)
                }
            }
            IconButton(onClick = { onDeleteVlan(vlan) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete VLAN")
            }
        }
    }
}
