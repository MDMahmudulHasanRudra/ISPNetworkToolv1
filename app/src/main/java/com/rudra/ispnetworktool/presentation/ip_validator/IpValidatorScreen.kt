package com.rudra.ispnetworktool.presentation.ip_validator

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun IpValidatorScreen(
    viewModel: IpValidatorViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val scaffoldState = remember { SnackbarHostState() }

    // Collect events from ViewModel
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowMessage -> {
                    scaffoldState.showSnackbar(event.message)
                }
                is UiEvent.ShareResults -> {
                    // Handle share intent
                }
            }
        }
    }

    Scaffold(
//        topBar = {
//            PremiumTopAppBar(
//                onClearClick = viewModel::clearResults,
//                onShareClick = viewModel::shareResults,
//                hasResults = state.isValid == true
//            )
//        },
        snackbarHost = { SnackbarHost(scaffoldState) },
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
            AnimatedBackgroundElements()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                IpValidatorHeader()

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    InputCard(
                        state = state,
                        onIpChanged = viewModel::onIpAddressChanged,
                        modifier = Modifier.fillMaxWidth()
                    )

                    ActionButtons(
                        onValidateClick = viewModel::checkIpAddress,
                        onMyIpClick = viewModel::getMyIp,
                        state = state,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (state.validationStatus == ValidationStatus.IDLE) {
                        QuickActionsRow(
                            onCommonIpClick = viewModel::validateCommonIp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

//                    if (state.isValid != null || state.validationStatus == ValidationStatus.ERROR) {
//                        ValidationResults(
//                            state = state,
//                            onRetryClick = viewModel::retryValidation,
//                            modifier = Modifier.fillMaxWidth()
//                        )
//                    }

                    if (state.isComplete) {
                        AdditionalInfoSection(
                            state = state,
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
fun PremiumTopAppBar(
    onClearClick: () -> Unit,
    onShareClick: () -> Unit,
    hasResults: Boolean
) {
    Surface(
        tonalElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        modifier = Modifier.shadow(8.dp)
    ) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = "Network Tools",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "IP Validator",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = MaterialTheme.colorScheme.onSurface
            ),
            actions = {
                if (hasResults) {
                    IconButton(onClick = onShareClick) {
                        Icon(Icons.Default.Share, contentDescription = "Share Results")
                    }
                }
                IconButton(onClick = onClearClick) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear Results")
                }
            }
        )
    }
}

@Composable
fun IpValidatorHeader() {
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
                imageVector = Icons.Default.Security,
                contentDescription = "IP Validator",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "IP Address Validator",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Validate and analyze IP addresses with detailed network information",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun InputCard(
    state: IpValidatorScreenState,
    onIpChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "IP Address Validator",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            PremiumTextField(
                value = state.ipAddress,
                onValueChange = onIpChanged,
                placeholder = "192.168.1.1 or 2001:db8::1",
                label = "Enter IP Address",
                modifier = Modifier.fillMaxWidth(),
                isError = state.isValid == false
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = state.displayStatus,
                    style = MaterialTheme.typography.bodyMedium,
                    color = state.statusColor,
                    fontWeight = FontWeight.Medium
                )

                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun ActionButtons(
    onValidateClick: () -> Unit,
    onMyIpClick: () -> Unit,
    state: IpValidatorScreenState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PremiumActionButton(
            onClick = onValidateClick,
            text = "Validate",
            icon = Icons.Default.Search,
            enabled = state.ipAddress.isNotBlank() && !state.isLoading,
            modifier = Modifier.weight(1f)
        )

        PremiumActionButton(
            onClick = onMyIpClick,
            text = "My IP",
            icon = Icons.Default.Wifi,
            backgroundColor = MaterialTheme.colorScheme.secondary,
            enabled = !state.isLoading,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun QuickActionsRow(
    onCommonIpClick: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val commonIps = listOf(
        "192.168.1.1" to "Router",
        "8.8.8.8" to "Google DNS",
        "1.1.1.1" to "Cloudflare",
        "127.0.0.1" to "Localhost",
        "10.0.0.1" to "Private Net",
        "172.16.0.1" to "Corporate"
    )

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Quick Tests",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(commonIps.chunked(2).size) { chunkIndex ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    commonIps.chunked(2)[chunkIndex].forEach { (ip, description) ->
                        QuickIpChip(
                            ip = ip,
                            description = description,
                            onClick = { onCommonIpClick(ip, description) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ValidationResults(
    state: IpValidatorScreenState,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Validation Results",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                if (state.validationStatus == ValidationStatus.ERROR) {
                    TextButton(onClick = onRetryClick) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Retry")
                    }
                }
            }

            if (state.validationStatus == ValidationStatus.ERROR) {
                ErrorResultItem(
                    message = state.errorMessage ?: "Validation failed",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (state.isValid == true) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ResultStatItem(
                            value = if (state.isPublic == true) "Public" else "Private",
                            label = "Visibility",
                            color = if (state.isPublic == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.weight(1f)
                        )
                        ResultStatItem(
                            value = state.ipType?.name ?: "Unknown",
                            label = "IP Version",
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    val connectionQuality = state.getConnectionQuality()
                    ResultRow(
                        label = "Connection Quality",
                        value = connectionQuality.displayName,
                        valueColor = Color(connectionQuality.color),
                        icon = Icons.Default.Speed
                    )

                    val securityLevel = state.getSecurityLevel()
                    ResultRow(
                        label = "Security Level",
                        value = securityLevel.displayName,
                        valueColor = Color(securityLevel.color),
                        icon = Icons.Default.Security
                    )

                    state.networkClass?.let { networkClass ->
                        ResultRow(
                            label = "Network Class",
                            value = networkClass.name.replace("_", " "),
                            valueColor = MaterialTheme.colorScheme.primary,
                            icon = Icons.Default.Lan
                        )
                    }

                    state.additionalInfo.specialPurpose?.let { purpose ->
                        if (purpose != SpecialPurpose.PUBLIC) {
                            ResultRow(
                                label = "Special Purpose",
                                value = purpose.name.replace("_", " "),
                                valueColor = MaterialTheme.colorScheme.secondary,
                                icon = Icons.Default.Info
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdditionalInfoSection(
    state: IpValidatorScreenState,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Network Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            state.additionalInfo.apply {
                subnetMask?.let { mask ->
                    ResultRow(
                        label = "Subnet Mask",
                        value = mask,
                        valueColor = MaterialTheme.colorScheme.onSurface
                    )
                }

                cidrNotation?.let { cidr ->
                    ResultRow(
                        label = "CIDR Notation",
                        value = cidr,
                        valueColor = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (totalHosts > 0) {
                    ResultRow(
                        label = "Total Hosts",
                        value = totalHosts.toString(),
                        valueColor = MaterialTheme.colorScheme.onSurface
                    )
                }

                networkAddress?.let { network ->
                    ResultRow(
                        label = "Network Address",
                        value = network,
                        valueColor = MaterialTheme.colorScheme.onSurface
                    )
                }

                broadcastAddress?.let { broadcast ->
                    ResultRow(
                        label = "Broadcast Address",
                        value = broadcast,
                        valueColor = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            if (state.lastUpdated > 0) {
                ResultRow(
                    label = "Last Updated",
                    value = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
                        .format(java.util.Date(state.lastUpdated)),
                    valueColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    icon = Icons.Default.Schedule
                )
            }
        }
    }
}

@Composable
fun QuickIpChip(
    ip: String,
    description: String,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = SpringSpec(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow),
        label = ""
    )

    Card(
        onClick = onClick,
        modifier = Modifier
            .width(120.dp)
            .scale(scale),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            tryAwaitRelease()
                            isPressed = false
                        }
                    )
                }
        ) {
            Text(
                text = ip,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ErrorResultItem(
    message: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ResultStatItem(
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
            color = color,
            textAlign = TextAlign.Center
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
fun ResultRow(
    label: String,
    value: String,
    valueColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = valueColor
        )
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
    modifier: Modifier = Modifier,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        modifier = modifier,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
            focusedLabelColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            errorBorderColor = MaterialTheme.colorScheme.error,
            errorLabelColor = MaterialTheme.colorScheme.error
        ),
        isError = isError,
        singleLine = true
    )
}

@Composable
fun PremiumActionButton(
    onClick: () -> Unit,
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    enabled: Boolean = true,
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
        enabled = enabled,
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
fun AnimatedBackgroundElements() {
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        drawCircle(
            color = Color(0xFFE3F2FD), // Light Blue
            radius = 150f,
            center = Offset(size.width * 0.8f, size.height * 0.2f)
        )
        drawCircle(
            color = Color(0xFFFFF9C4), // Light Yellow
            radius = 100f,
            center = Offset(size.width * 0.2f, size.height * 0.7f)
        )
    }
}
