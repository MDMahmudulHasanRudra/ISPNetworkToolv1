package com.rudra.ispnetworktool.presentation.explore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.DataObject
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Monitor
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rudra.ispnetworktool.presentation.components.SectionHeader
import com.rudra.ispnetworktool.presentation.components.ToolGridCard
import com.rudra.ispnetworktool.presentation.dashboard.SearchBar
import com.rudra.ispnetworktool.presentation.dashboard.ToolItem
import com.rudra.ispnetworktool.presentation.navigation.Screen

data class ToolCategory(
    val name: String,
    val tools: List<ToolItem>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val categories = listOf(
        ToolCategory(
            name = "Diagnostics",
            tools = listOf(
                ToolItem("Ping", Icons.Outlined.Security, Screen.Ping.route, Color(0xFF22C55E)),
                ToolItem("Traceroute", Icons.Outlined.Route, Screen.Traceroute.route, Color(0xFF3B82F6)),
                ToolItem("DNS Lookup", Icons.Outlined.Dns, Screen.DnsLookup.route, Color(0xFF8B5CF6)),
                ToolItem("WHOIS Lookup", Icons.Outlined.Public, Screen.WhoisLookup.route, Color(0xFF06B6D4)),
                ToolItem("Port Checker", Icons.Outlined.Visibility, Screen.PortChecker.route, Color(0xFFEC4899))
            )
        ),
        ToolCategory(
            name = "IP & Network",
            tools = listOf(
                ToolItem("IP Info", Icons.Outlined.Info, Screen.IpInfo.route, Color(0xFFF59E0B)),
                ToolItem("IP Validator", Icons.Outlined.CheckCircle, Screen.IpValidator.route, Color(0xFFD97706)),
                ToolItem("Subnet Calculator", Icons.Outlined.Calculate, Screen.SubnetCalculator.route, Color(0xFF64748B)),
                ToolItem("Network Calculator", Icons.Outlined.Route, Screen.NetworkCalculator.route, Color(0xFF14B8A6)),
                ToolItem("CIDR Visualizer", Icons.Outlined.Insights, Screen.CidrVisualizer.route, Color(0xFF10B981)),
                ToolItem("IP Planning Chart", Icons.Outlined.BarChart, Screen.IpPlanningChart.route, Color(0xFF84CC16)),
                ToolItem("Bandwidth Calculator", Icons.Outlined.Speed, Screen.BandwidthCalculator.route, Color(0xFFE11D48))
            )
        ),
        ToolCategory(
            name = "Planning & Reference",
            tools = listOf(
                ToolItem("VLAN Planner", Icons.Outlined.Layers, Screen.VlanPlanner.route, Color(0xFF7C3AED)),
                ToolItem("TCP/UDP Ports", Icons.Outlined.Code, Screen.TcpUdpPortReference.route, Color(0xFF2563EB)),
                ToolItem("OSI & TCP/IP", Icons.Outlined.DataObject, Screen.OsiTcpIpModels.route, Color(0xFF0EA5E9)),
                ToolItem("BDIX Monitor", Icons.Outlined.Monitor, Screen.BdixMonitor.route, Color(0xFF22C55E))
            )
        ),
        ToolCategory(
            name = "Router & Security",
            tools = listOf(
                ToolItem("Router Setup", Icons.Outlined.Settings, Screen.BasicRouterSetup.route, Color(0xFF22C55E)),
                ToolItem("PPPoE Setup", Icons.Outlined.Build, Screen.PppoeServerSetup.route, Color(0xFF65A30D)),
                ToolItem("Hotspot Setup", Icons.Outlined.Info, Screen.HotspotSetup.route, Color(0xFFFBBF24)),
                ToolItem("Load Balancing", Icons.Outlined.SwapHoriz, Screen.LoadBalancingPresets.route, Color(0xFF06B6D4)),
                ToolItem("Firewall Rules", Icons.Outlined.Lock, Screen.FirewallRuleGenerator.route, Color(0xFFEF4444)),
                ToolItem("NAT Rules", Icons.Outlined.Dns, Screen.NatRuleTemplates.route, Color(0xFFE11D48))
            )
        )
    )

    val allTools = categories.flatMap { it.tools }
    val filteredTools = if (searchQuery.isEmpty()) {
        allTools
    } else {
        allTools.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Explore Tools",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Browse all available tools",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(span = { GridItemSpan(2) }) {
                SearchBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    onClearQuery = { searchQuery = "" },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (searchQuery.isEmpty()) {
                categories.forEach { category ->
                    item(span = { GridItemSpan(2) }) {
                        SectionHeader(
                            title = category.name,
                            subtitle = "${category.tools.size} tools"
                        )
                    }
                    items(category.tools) { tool ->
                        ToolGridCard(
                            tool = tool,
                            onClick = { navController.navigate(tool.route) }
                        )
                    }
                }
            } else {
                items(filteredTools) { tool ->
                    ToolGridCard(
                        tool = tool,
                        onClick = { navController.navigate(tool.route) }
                    )
                }
            }
        }
    }
}
