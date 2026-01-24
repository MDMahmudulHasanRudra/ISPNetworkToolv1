package com.rudra.ispnetworktool.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Filled.Dashboard)
    object Explore : Screen("explore", "Explore", Icons.Filled.Explore)
    object Tools : Screen("tools", "Tools", Icons.Filled.Difference)
    object History : Screen("history", "History", Icons.Filled.History)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings)
    object IpValidator : Screen("ip_validator", "IP Validator", Icons.Filled.CheckCircle)
    object Ping : Screen("ping", "Ping", Icons.Filled.NetworkPing)
    object Traceroute : Screen("traceroute", "Traceroute", Icons.Filled.Timeline)
    object DnsLookup : Screen("dnslookup", "DNS Lookup", Icons.Filled.Dns)
    object IpInfo : Screen("ipinfo", "IP Info", Icons.Filled.Info)
    object SubnetCalculator : Screen("subnetcalculator", "Subnet Calculator", Icons.Filled.Calculate)
    object PortChecker : Screen("portchecker", "Port Checker", Icons.Filled.NetworkCheck)
    object WhoisLookup : Screen("whoislookup", "Whois Lookup", Icons.Filled.Public)
    object NetworkCalculator : Screen("networkcalculator", "Network Calculator", Icons.Filled.Calculate)
    object CidrVisualizer : Screen("cidrvisualizer", "CIDR Visualizer", Icons.Filled.Visibility)
    object IpPlanningChart : Screen("ipplanningchart", "IP Planning Chart", Icons.Filled.AccountTree)
    object VlanPlanner : Screen("vlanplanner", "VLAN Planner", Icons.Filled.Schema)
    object TcpUdpPortReference : Screen("tcpudpportreference", "TCP/UDP Ports", Icons.Filled.ListAlt)
    object OsiTcpIpModels : Screen("osi&tcpipmodels", "OSI & TCP/IP Models", Icons.Filled.DonutLarge)
    object BasicRouterSetup : Screen("basicroutersetup", "Basic Router Setup", Icons.Filled.Router)
    object PppoeServerSetup : Screen("pppoeserversetup", "PPPoE Server Setup", Icons.Filled.Dns)
    object HotspotSetup : Screen("hotspotsetup", "Hotspot Setup", Icons.Filled.Wifi)
    object LoadBalancingPresets : Screen("loadbalancingpresets", "Load Balancing Presets", Icons.Filled.Balance)
    object FirewallRuleGenerator : Screen("firewallrulegenerator", "Firewall Rule Generator", Icons.Filled.Security)
    object NatRuleTemplates : Screen("natruletemplates", "NAT Rule Templates", Icons.Filled.AccountTree)
    object BandwidthCalculator : Screen("bandwidth_calculator", "Bandwidth Calculator", Icons.Filled.Speed)
    object BdixMonitor : Screen("bdix_monitor", "BDIX Monitor", Icons.Filled.Monitor)
}
