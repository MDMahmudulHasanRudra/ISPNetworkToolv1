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
        composable(Screen.History.route) {
            HistoryScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
        composable(Screen.IpValidator.route) {
            IpValidatorScreen()
        }
        composable(Screen.Ping.route) {
            PingScreen()
        }
        composable(Screen.Traceroute.route) {
            TracerouteScreen()
        }
        composable(Screen.DnsLookup.route) {
            DnsLookupScreen()
        }
        composable(Screen.IpInfo.route) {
            IpInfoScreen()
        }
        composable(Screen.SubnetCalculator.route) {
            SubnetCalculatorScreen()
        }
        composable(Screen.PortChecker.route) {
            PortCheckerScreen()
        }
        composable(Screen.WhoisLookup.route) {
            WhoisScreen()
        }
        composable(Screen.NetworkCalculator.route) {
            NetworkCalculatorScreen()
        }
        composable(Screen.CidrVisualizer.route) {
            CidrVisualizerScreen()
        }
        composable(Screen.IpPlanningChart.route) {
            IpPlanningChartScreen()
        }
        composable(Screen.VlanPlanner.route) {
            VlanPlannerScreen()
        }
        composable(Screen.TcpUdpPortReference.route) {
            TcpUdpPortReferenceScreen()
        }
        composable(Screen.OsiTcpIpModels.route) {
            OsiTcpIpModelsScreen()
        }
        composable(Screen.BasicRouterSetup.route) {
            BasicRouterSetupScreen()
        }
        composable(Screen.PppoeServerSetup.route) {
            PppoeServerSetupScreen()
        }
        composable(Screen.HotspotSetup.route) {
            HotspotSetupScreen()
        }
        composable(Screen.LoadBalancingPresets.route) {
            LoadBalancingPresetsScreen()
        }
        composable(Screen.FirewallRuleGenerator.route) {
            FirewallRuleGeneratorScreen()
        }
        composable(Screen.NatRuleTemplates.route) {
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
