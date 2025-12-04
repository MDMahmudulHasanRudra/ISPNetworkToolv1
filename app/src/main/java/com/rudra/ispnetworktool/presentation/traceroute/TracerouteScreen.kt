package com.rudra.ispnetworktool.presentation.traceroute

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rudra.ispnetworktool.data.models.TracerouteResult
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TracerouteScreen(viewModel: TracerouteViewModel = hiltViewModel()) {

    var host by remember { mutableStateOf("google.com") }
    val state by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll when new hop appears
    LaunchedEffect(state.results.size) {
        if (state.results.isNotEmpty()) {
            coroutineScope.launch {
                lazyListState.animateScrollToItem(state.results.size - 1)
            }
        }
    }

    // Show errors
    LaunchedEffect(Unit) {
        viewModel.errorFlow.collectLatest { error ->
            snackbarHostState.showSnackbar(error)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Traceroute", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            // Host input + buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                OutlinedTextField(
                    value = host,
                    onValueChange = { host = it },
                    label = { Text("Host") },
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = { viewModel.startTraceroute(host) },
                    enabled = !state.isLoading
                ) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = "Start")
                }

                IconButton(
                    onClick = { viewModel.stopTraceroute() },
                    enabled = state.isLoading
                ) {
                    Icon(Icons.Filled.Stop, contentDescription = "Stop")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Save / Share / Copy
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                OutlinedButton(
                    onClick = { viewModel.saveResult() }
                ) {
                    Icon(Icons.Filled.Save, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Save")
                }

                OutlinedButton(
                    onClick = {
                        viewModel.shareResult { text ->
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, text)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share via"))
                        }
                    }
                ) {
                    Icon(Icons.Filled.Share, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Share")
                }

                OutlinedButton(
                    onClick = {
                        viewModel.shareResult { text ->
                            clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(text))
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Copied to clipboard")
                            }
                        }
                    }
                ) {
                    Icon(Icons.Filled.ContentCopy, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Copy")
                }
            }

            // Loading Indicator
            if (state.isLoading) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // Traceroute results with animation
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 12.dp),
                state = lazyListState
            ) {

                itemsIndexed(state.results) { index, result ->

                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn()
                    ) {

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .shadow(2.dp, RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(Modifier.padding(16.dp)) {

                                when (result) {
                                    is TracerouteResult.Hop -> {
                                        Text(
                                            text = "${result.hop}. ${result.ip}  —  ${result.rtt} ms",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }

                                    is TracerouteResult.Failure -> {
                                        Text(
                                            text = "Error: ${result.error}",
                                            color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }

                                    else -> Unit
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
