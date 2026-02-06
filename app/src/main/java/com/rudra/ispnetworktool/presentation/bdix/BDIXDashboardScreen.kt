package com.rudra.ispnetworktool.presentation.bdix

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.rudra.ispnetworktool.domain.model.BDIXNode
import com.rudra.ispnetworktool.domain.model.BDIXPingResult
import com.rudra.ispnetworktool.domain.model.NodeStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BDIXDashboardScreen(
    navController: NavController,
    viewModel: BDIXViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var isSearchActive by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            if (isSearchActive) {
                SearchTopBar(
                    query = uiState.searchQuery,
                    onQueryChange = { viewModel.onSearchQueryChange(it) },
                    onClose = {
                        isSearchActive = false
                        viewModel.onSearchQueryChange("")
                    }
                )
            } else {
                TopAppBar(
                    title = { Text("🇧🇩 BDIX Monitor", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    actions = {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                        IconButton(onClick = { viewModel.pingAllNodes() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh All")
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            // Category Chips
            CategoryFilterRow(
                categories = viewModel.getCategories(),
                selectedCategory = uiState.selectedCategory,
                onCategorySelect = { viewModel.onCategorySelect(it) }
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${uiState.filteredNodes.size} Nodes Found",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        if (uiState.searchQuery.isNotEmpty() || uiState.selectedCategory != "All") {
                            TextButton(onClick = { 
                                viewModel.onSearchQueryChange("")
                                viewModel.onCategorySelect("All")
                            }) {
                                Text("Clear Filters")
                            }
                        }
                    }
                }

                items(uiState.filteredNodes, key = { it.id }) { node ->
                    BDIXNodeCard(
                        node = node,
                        pingResult = uiState.pingResults[node.id],
                        onClick = { viewModel.pingNode(node.id) }
                    )
                }
                
                if (uiState.filteredNodes.isEmpty()) {
                    item {
                        EmptySearchResults()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit
) {
    TopAppBar(
        title = {
            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Search by name, IP, or location...", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    cursorColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )
        },
        navigationIcon = {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Clear, contentDescription = "Close Search", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
    )
}

@Composable
fun CategoryFilterRow(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelect(category) },
                label = { Text(category) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}

@Composable
fun BDIXNodeCard(
    node: BDIXNode,
    pingResult: BDIXPingResult?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = node.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = node.ipAddress,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        CategoryBadge(category = node.category)
                    }
                }
                
                StatusIndicator(status = node.status)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "📍 ${node.location}",
                style = MaterialTheme.typography.bodySmall
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricColumn(value = node.capacity, label = "Capacity")
                
                pingResult?.let { result ->
                    MetricColumn(
                        value = if (result.isSuccess) "${result.latencyMs.toInt()}ms" else "N/A",
                        label = "Latency",
                        valueColor = if (result.isSuccess) Color(0xFF2E7D32) else Color.Red
                    )
                }

                MetricColumn(value = "${node.connectedISPs}", label = "ISPs")
            }
        }
    }
}

@Composable
fun StatusIndicator(status: NodeStatus) {
    Box(
        modifier = Modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(
                color = when (status) {
                    NodeStatus.HEALTHY -> Color(0xFF4CAF50)
                    NodeStatus.DEGRADED -> Color(0xFFFFC107)
                    NodeStatus.DOWN -> Color(0xFFF44336)
                    NodeStatus.UNKNOWN -> Color.Gray
                }
            )
    )
}

@Composable
fun MetricColumn(
    value: String,
    label: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun CategoryBadge(category: String) {
    Surface(
        color = when (category) {
            "Core" -> MaterialTheme.colorScheme.primaryContainer
            "PTX" -> MaterialTheme.colorScheme.secondaryContainer
            "ISP" -> MaterialTheme.colorScheme.tertiaryContainer
            "Government", "Banking" -> Color(0xFFE3F2FD)
            "Education" -> Color(0xFFF1F8E9)
            else -> MaterialTheme.colorScheme.surfaceVariant
        },
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = category,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = when (category) {
                "Government", "Banking" -> Color(0xFF1976D2)
                "Education" -> Color(0xFF388E3C)
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

@Composable
fun EmptySearchResults() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("No nodes found matching your search.", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Try adjusting your filters or search query.", style = MaterialTheme.typography.bodySmall)
    }
}
