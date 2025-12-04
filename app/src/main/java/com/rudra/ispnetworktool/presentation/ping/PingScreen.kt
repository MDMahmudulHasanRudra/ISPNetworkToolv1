package com.rudra.ispnetworktool.presentation.ping

import android.content.Intent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rudra.ispnetworktool.data.models.PingResult
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PingScreen(viewModel: PingViewModel = hiltViewModel()) {
    var host by remember { mutableStateOf("google.com") }
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.errorFlow.collectLatest { error ->
            snackbarHostState.showSnackbar(message = error)
        }
    }

    Scaffold(
        topBar = {
            PremiumTopAppBar()
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    )
                )
        ) {
            // Background decorative elements
            AnimatedBackgroundElements()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header Section
                PingHeader()

                // Main Content
                when {
                    state.isLoading -> {
                        PingInProgressSection(
                            host = host,
                            results = state.results,
                            onStopClick = { viewModel.stopPing() },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    else -> {
                        PingIdleSection(
                            host = host,
                            onHostChange = { host = it },
                            onPingClick = { viewModel.startPing(host, 4) },
                            results = state.results,
                            onSaveClick = { viewModel.saveResult() },
                            onShareClick = {
                                viewModel.shareResult { text ->
                                    val sendIntent: Intent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, text)
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, null)
                                    context.startActivity(shareIntent)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumTopAppBar() {
    Surface(
        tonalElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        modifier = Modifier.shadow(8.dp)
    ) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = "Network Diagnostics",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Ping Tool",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

@Composable
fun PingHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Wifi,
                contentDescription = "Network Tools",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Network Ping Tool",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Test your network connectivity with precision",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun PingIdleSection(
    host: String,
    onHostChange: (String) -> Unit,
    onPingClick: () -> Unit,
    results: List<PingResult>,
    onSaveClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Input Card
        GlassCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Enter Host",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                PremiumTextField(
                    value = host,
                    onValueChange = onHostChange,
                    placeholder = "google.com",
                    label = "Hostname or IP Address",
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Popular options: google.com, 8.8.8.8, cloudflare.com",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PremiumActionButton(
                onClick = onPingClick,
                text = "Start Ping",
                icon = Icons.Default.PlayArrow,
                modifier = Modifier.weight(1f)
            )

            if (results.isNotEmpty()) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PremiumIconButton(
                        onClick = onSaveClick,
                        icon = Icons.Default.Save,
                        contentDescription = "Save Results",
                        backgroundColor = MaterialTheme.colorScheme.secondary
                    )

                    PremiumIconButton(
                        onClick = onShareClick,
                        icon = Icons.Default.Share,
                        contentDescription = "Share Results",
                        backgroundColor = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }

        // Results Display
        if (results.isNotEmpty()) {
            PingResultsSection(
                results = results,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun PingInProgressSection(
    host: String,
    results: List<PingResult>,
    onStopClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Progress Card
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Testing Connection",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Pinging $host",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                LinearProgressIndicator(
                    progress = { calculateProgress(results.size, 4) },
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Stop Button
        PremiumActionButton(
            onClick = onStopClick,
            text = "Stop Test",
            icon = Icons.Default.Stop,
            backgroundColor = MaterialTheme.colorScheme.error,
            modifier = Modifier.fillMaxWidth(0.7f)
        )

        // Live Results
        if (results.isNotEmpty()) {
            PingResultsSection(
                results = results,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun PingResultsSection(
    results: List<PingResult>,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ping Results",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${results.size} packets",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            LazyColumn(
                modifier = Modifier.height(200.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(results) { result ->
                    PingResultItem(result = result)
                }
            }

            // Statistics Summary
            if (results.isNotEmpty()) {
                val successCount = results.count { it is PingResult.Success }
                val packetLoss = ((results.size - successCount) * 100.0 / results.size).toInt()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatItem(
                        value = "$packetLoss%",
                        label = "Packet Loss",
                        color = if (packetLoss > 20) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    StatItem(
                        value = successCount.toString(),
                        label = "Successful",
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )
                    StatItem(
                        value = results.size.toString(),
                        label = "Total",
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun PingResultItem(result: PingResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = when (result) {
                            is PingResult.Success -> getLatencyColor((result as PingResult.Success).rtt)
                            else -> MaterialTheme.colorScheme.error
                        },
                        shape = CircleShape
                    )
            )

            Column(modifier = Modifier.weight(1f)) {
                when (result) {
                    is PingResult.Success -> {
                        Text(
                            text = "Reply received",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Time: ${result.rtt}ms",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    is PingResult.Failure -> {
                        Text(
                            text = "Request timed out",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "No response from host",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    else -> {
                        Text(
                            text = "Unknown result",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            when (result) {
                is PingResult.Success -> {
                    Text(
                        text = "${result.rtt}ms",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = getLatencyColor(result.rtt)
                    )
                }
                else -> {
                    Text(
                        text = "Timeout",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                clip = false
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        content()
    }
}

@Composable
fun PremiumTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        modifier = modifier,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
        ),
        singleLine = true
    )
}

@Composable
fun PremiumActionButton(
    onClick: () -> Unit,
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = SpringSpec(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow),
        label = ""
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .height(56.dp)
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            },
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun PremiumIconButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = SpringSpec(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow),
        label = ""
    )

    Box(
        modifier = modifier
            .size(56.dp)
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun StatItem(
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AnimatedBackgroundElements() {
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        drawCircle(
            color = Color(0xFFE3F2FD), // Soft light blue
            radius = 150f,
            center = Offset(size.width * 0.8f, size.height * 0.2f)
        )
        drawCircle(
            color = Color(0xFFE8F5E9), // Soft light green
            radius = 100f,
            center = Offset(size.width * 0.2f, size.height * 0.7f)
        )
    }
}


// Helper functions
private fun calculateProgress(current: Int, total: Int): Float {
    return if (total == 0) 0f else (current.toFloat() / total.toFloat()).coerceIn(0f, 1f)
}

private fun getLatencyColor(latency: Float): Color {
    return when {
        latency < 50 -> Color(0xFF00C853) // Green - Excellent
        latency < 100 -> Color(0xFF64DD17) // Light Green - Good
        latency < 200 -> Color(0xFFFFD600) // Yellow - Average
        latency < 500 -> Color(0xFFFF6D00) // Orange - Poor
        else -> Color(0xFFDD2C00) // Red - Bad
    }
}