package com.rudra.ispnetworktool.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rudra.ispnetworktool.presentation.basic_router_setup.BasicRouterSetupScreen
import com.rudra.ispnetworktool.presentation.cidr_visualizer.CidrVisualizerScreen
import com.rudra.ispnetworktool.presentation.dashboard.DashboardScreen
import com.rudra.ispnetworktool.presentation.dns.DnsLookupScreen
import com.rudra.ispnetworktool.presentation.firewall_rule_generator.FirewallRuleGeneratorScreen
import com.rudra.ispnetworktool.presentation.history.HistoryScreen
import com.rudra.ispnetworktool.presentation.hotspot_setup.HotspotSetupScreen
import com.rudra.ispnetworktool.presentation.ipinfo.IpInfoScreen
import com.rudra.ispnetworktool.presentation.ip_planning_chart.IpPlanningChartScreen
import com.rudra.ispnetworktool.presentation.ip_validator.IpValidatorScreen
import com.rudra.ispnetworktool.presentation.load_balancing_presets.LoadBalancingPresetsScreen
import com.rudra.ispnetworktool.presentation.nat_rule_templates.NatRuleTemplatesScreen
import com.rudra.ispnetworktool.presentation.osi_tcp_ip_models.OsiTcpIpModelsScreen
import com.rudra.ispnetworktool.presentation.ping.PingScreen
import com.rudra.ispnetworktool.presentation.pppoe_server_setup.PppoeServerSetupScreen
import com.rudra.ispnetworktool.presentation.port_reference.TcpUdpPortReferenceScreen
import com.rudra.ispnetworktool.presentation.portchecker.PortCheckerScreen
import com.rudra.ispnetworktool.presentation.settings.SettingsScreen
import com.rudra.ispnetworktool.presentation.subnet.SubnetCalculatorScreen
import com.rudra.ispnetworktool.presentation.traceroute.TracerouteScreen
import com.rudra.ispnetworktool.presentation.vlan_planner.VlanPlannerScreen
import com.rudra.ispnetworktool.presentation.whois.WhoisScreen
import com.rudra.ispnetworktools.ui.NetworkCalculatorScreen

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController, startDestination = Screen.Dashboard.route) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController = navController)
        }
        composable("ipvalidator") {  // Add this route
            IpValidatorScreen()
        }
        composable(Screen.Tools.route) {
            ToolsScreen(navController = navController)
        }
        composable(Screen.History.route) {
            HistoryScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
        composable(Screen.IpValidator.route) {
            IpValidatorScreen()
        }
        composable("ping") {
            PingScreen()
        }
        composable("traceroute") {
            TracerouteScreen()
        }
        composable("dnslookup") {
            DnsLookupScreen()
        }
        composable("ipinfo") {
            IpInfoScreen()
        }
        composable("subnetcalculator") {
            SubnetCalculatorScreen()
        }
        composable("portchecker") {
            PortCheckerScreen()
        }
        composable("whoislookup") {
            WhoisScreen()
        }
        composable("networkcalculator") {
            NetworkCalculatorScreen()
        }
        composable("cidrvosualizer") {
            CidrVisualizerScreen()
        }
        composable("ipplanningchart") {
            IpPlanningChartScreen()
        }
        composable("vlanplanner") {
            VlanPlannerScreen()
        }
        composable("tcpudpportreference") {
            TcpUdpPortReferenceScreen()
        }
        composable("osi&tcpipmodels") {
            OsiTcpIpModelsScreen()
        }
        composable("basicroutersetup") {
            BasicRouterSetupScreen()
        }
        composable("pppoeserversetup") {
            PppoeServerSetupScreen()
        }
        composable("hotspotsetup") {
            HotspotSetupScreen()
        }
        composable("loadbalancingpresets") {
            LoadBalancingPresetsScreen()
        }
        composable("firewallrulegenerator") {
            FirewallRuleGeneratorScreen()
        }
        composable("natruletemplates") {
            NatRuleTemplatesScreen()
        }
    }
}

@Composable
fun ComingSoonScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Coming Soon!")
    }
}

@Composable
fun ToolsScreen(navController: NavHostController) {
    // This screen is no longer needed as the tools are on the dashboard
}
