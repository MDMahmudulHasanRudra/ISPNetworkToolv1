package com.rudra.ispnetworktool.presentation.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rudra.ispnetworktool.presentation.dashboard.DashboardScreen
import com.rudra.ispnetworktool.presentation.dns.DnsLookupScreen
import com.rudra.ispnetworktool.presentation.history.HistoryScreen
import com.rudra.ispnetworktool.presentation.ipinfo.IpInfoScreen
import com.rudra.ispnetworktool.presentation.ping.PingScreen
import com.rudra.ispnetworktool.presentation.portchecker.PortCheckerScreen
import com.rudra.ispnetworktool.presentation.settings.SettingsScreen
import com.rudra.ispnetworktool.presentation.subnet.SubnetCalculatorScreen
import com.rudra.ispnetworktool.presentation.traceroute.TracerouteScreen
import com.rudra.ispnetworktool.presentation.whois.WhoisScreen
import com.rudra.ispnetworktools.ui.NetworkCalculatorScreen

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController, startDestination = Screen.Dashboard.route) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController = navController)
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
        composable("NetworkCalculator") {
            NetworkCalculatorScreen()
        }
      //  composable("SpeedTest") {
     //       SpeedTestScreen()
     //   }
    }
}

@Composable
fun ToolsScreen(navController: NavHostController) {
    val tools = listOf("Ping", "Traceroute", "DNS Lookup", "IP Info", "Subnet Calculator","NetworkCalculator" ,"Port Checker", "WHOIS Lookup")
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(tools) { tool ->
            Text(
                text = tool,
                modifier = Modifier.clickable { navController.navigate(tool.replace(" ", "").lowercase()) }
            )
        }
    }
}
