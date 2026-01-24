package com.rudra.ispnetworktool.presentation.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rudra.ispnetworktool.presentation.dashboard.SearchBar
import com.rudra.ispnetworktool.presentation.dashboard.ToolItem
import com.rudra.ispnetworktool.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }

    val allTools = listOf(
        ToolItem("Bandwidth Calculator", Icons.Outlined.Speed, Screen.BandwidthCalculator.route, Color(0xFFE91E63)),
        ToolItem("BDIX Monitor", Icons.Outlined.Monitor, Screen.BdixMonitor.route, Color(0xFF4CAF50)),
        ToolItem("Ping", Icons.Outlined.Security, Screen.Ping.route, Color(0xFF4CAF50)),
        ToolItem("Traceroute", Icons.Outlined.Route, Screen.Traceroute.route, Color(0xFF2196F3)),
        ToolItem("DNS Lookup", Icons.Outlined.Dns, Screen.DnsLookup.route, Color(0xFF9C27B0)),
        ToolItem("IP Info", Icons.Outlined.Info, Screen.IpInfo.route, Color(0xFFFF9800)),
        ToolItem("Subnet Calculator", Icons.Outlined.Calculate, Screen.SubnetCalculator.route, Color(0xFF607D8B)),
        ToolItem("Port Checker", Icons.Outlined.Visibility, Screen.PortChecker.route, Color(0xFFE91E63)),
        ToolItem("WHOIS Lookup", Icons.Outlined.Public, Screen.WhoisLookup.route, Color(0xFF2196F3)),
        ToolItem("Network Calculator", Icons.Outlined.Route, Screen.NetworkCalculator.route, Color(0xFF2196F3)),
        ToolItem("IP Validator", Icons.Outlined.CheckCircle, Screen.IpValidator.route, Color(0xFF795548)),
        ToolItem("CIDR Visualizer", Icons.Outlined.Insights, Screen.CidrVisualizer.route, Color(0xFF009688)),
        ToolItem("IP Planning Chart", Icons.Outlined.BarChart, Screen.IpPlanningChart.route, Color(0xFFCDDC39)),
        ToolItem("VLAN Planner", Icons.Outlined.Layers, Screen.VlanPlanner.route, Color(0xFF673AB7)),
        ToolItem("TCP/UDP Port Reference", Icons.Outlined.Code, Screen.TcpUdpPortReference.route, Color(0xFF3F51B5)),
        ToolItem("OSI & TCP/IP Models", Icons.Outlined.DataObject, Screen.OsiTcpIpModels.route, Color(0xFF03A9F4)),
        ToolItem("Basic Router Setup", Icons.Outlined.Settings, Screen.BasicRouterSetup.route, Color(0xFF4CAF50)),
        ToolItem("PPPoE Server Setup", Icons.Outlined.Build, Screen.PppoeServerSetup.route, Color(0xFF8BC34A)),
        ToolItem("Hotspot Setup", Icons.Outlined.Info, Screen.HotspotSetup.route, Color(0xFFFFC107)),
        ToolItem("Load-Balancing Presets", Icons.Outlined.SwapHoriz, Screen.LoadBalancingPresets.route, Color(0xFF00BCD4)),
        ToolItem("Firewall Rule Generator", Icons.Outlined.Lock, Screen.FirewallRuleGenerator.route, Color(0xFFF44336)),
        ToolItem("NAT Rule Templates", Icons.Outlined.Dns, Screen.NatRuleTemplates.route, Color(0xFFE91E63))
    )

    val filteredTools = allTools.filter {
        it.name.contains(searchQuery, ignoreCase = true) || searchQuery.isEmpty()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Explore Tools",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onClearQuery = { searchQuery = "" },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filteredTools) { tool ->
                    ExploreToolTile(
                        tool = tool,
                        onClick = { navController.navigate(tool.route) }
                    )
                }
            }
        }
    }
}

@Composable
fun ExploreToolTile(tool: ToolItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            tool.color.copy(alpha = 0.12f),
                            tool.color.copy(alpha = 0.04f)
                        )
                    )
                )
                .padding(12.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = tool.icon,
                    contentDescription = tool.name,
                    modifier = Modifier.size(32.dp),
                    tint = tool.color
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = tool.name,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
