package com.rudra.ispnetworktool.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.rudra.ispnetworktool.presentation.components.NetworkStatusCard
import com.rudra.ispnetworktool.presentation.components.SectionHeader
import com.rudra.ispnetworktool.presentation.components.ToolGridCard
import com.rudra.ispnetworktool.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController, viewModel: DashboardViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val tools = listOf(
        ToolItem("Ping", Icons.Outlined.Security, Screen.Ping.route, Color(0xFF22C55E)),
        ToolItem("Traceroute", Icons.Outlined.Route, Screen.Traceroute.route, Color(0xFF3B82F6)),
        ToolItem("DNS Lookup", Icons.Outlined.Dns, Screen.DnsLookup.route, Color(0xFF8B5CF6)),
        ToolItem("IP Info", Icons.Outlined.Info, Screen.IpInfo.route, Color(0xFFF59E0B)),
        ToolItem("Subnet Calculator", Icons.Outlined.Calculate, Screen.SubnetCalculator.route, Color(0xFF64748B)),
        ToolItem("Port Checker", Icons.Outlined.Visibility, Screen.PortChecker.route, Color(0xFFEC4899)),
        ToolItem("WHOIS Lookup", Icons.Outlined.Public, Screen.WhoisLookup.route, Color(0xFF06B6D4)),
        ToolItem("Network Calculator", Icons.Outlined.Route, Screen.NetworkCalculator.route, Color(0xFF14B8A6)),
        ToolItem("IP Validator", Icons.Outlined.CheckCircle, Screen.IpValidator.route, Color(0xFFD97706)),
        ToolItem("CIDR Visualizer", Icons.Outlined.Insights, Screen.CidrVisualizer.route, Color(0xFF10B981)),
        ToolItem("IP Planning Chart", Icons.Outlined.BarChart, Screen.IpPlanningChart.route, Color(0xFF84CC16)),
        ToolItem("VLAN Planner", Icons.Outlined.Layers, Screen.VlanPlanner.route, Color(0xFF7C3AED)),
        ToolItem("TCP/UDP Ports", Icons.Outlined.Code, Screen.TcpUdpPortReference.route, Color(0xFF2563EB)),
        ToolItem("OSI & TCP/IP", Icons.Outlined.DataObject, Screen.OsiTcpIpModels.route, Color(0xFF0EA5E9)),
        ToolItem("Router Setup", Icons.Outlined.Settings, Screen.BasicRouterSetup.route, Color(0xFF22C55E)),
        ToolItem("PPPoE Setup", Icons.Outlined.Build, Screen.PppoeServerSetup.route, Color(0xFF65A30D)),
        ToolItem("Hotspot Setup", Icons.Outlined.Info, Screen.HotspotSetup.route, Color(0xFFFBBF24)),
        ToolItem("Load Balancing", Icons.Outlined.SwapHoriz, Screen.LoadBalancingPresets.route, Color(0xFF06B6D4)),
        ToolItem("Firewall Rules", Icons.Outlined.Lock, Screen.FirewallRuleGenerator.route, Color(0xFFEF4444)),
        ToolItem("NAT Rules", Icons.Outlined.Dns, Screen.NatRuleTemplates.route, Color(0xFFE11D48))
    )

    val filteredTools = tools.filter {
        it.name.contains(searchQuery, ignoreCase = true) || searchQuery.isEmpty()
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            text = "ISP Network Tool",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Professional Network Toolkit",
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
        },
        containerColor = MaterialTheme.colorScheme.background
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
            // Search bar as first item spanning full width
            item(span = { GridItemSpan(2) }) {
                com.rudra.ispnetworktool.presentation.dashboard.SearchBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    onClearQuery = { searchQuery = "" },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Network status card spanning full width
            if (searchQuery.isEmpty()) {
                state.ipInfo?.let {
                    item(span = { GridItemSpan(2) }) {
                        NetworkStatusCard(
                            publicIp = it.publicIp,
                            localIp = it.localIp,
                            gateway = it.gateway
                        )
                    }
                }
            }

            // Section header
            if (searchQuery.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    SectionHeader(
                        title = "Network Tools",
                        subtitle = "${filteredTools.size} tools available"
                    )
                }
            }

            // Tool grid cards
            items(filteredTools) { tool ->
                ToolGridCard(
                    tool = tool,
                    onClick = { navController.navigate(tool.route) }
                )
            }
        }
    }
}
