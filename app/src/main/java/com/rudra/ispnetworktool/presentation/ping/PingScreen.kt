package com.rudra.ispnetworktool.presentation.ping

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rudra.ispnetworktool.data.models.PingResult
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PingScreen(viewModel: PingViewModel = hiltViewModel()) {
    var commandInput by remember { mutableStateOf("") }
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    LaunchedEffect(key1 = true) {
        viewModel.errorFlow.collectLatest { error ->
            snackbarHostState.showSnackbar(message = error)
        }
    }

    // Auto-scroll to bottom when new results arrive
    LaunchedEffect(state.results.size) {
        if (state.results.isNotEmpty()) {
            listState.animateScrollToItem(state.results.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Network CLI (Ping)", fontWeight = FontWeight.Bold) },
                actions = {
                    if (state.results.isNotEmpty()) {
                        IconButton(onClick = { viewModel.saveResult() }) {
                            Icon(Icons.Default.Save, contentDescription = "Save")
                        }
                        IconButton(onClick = {
                            viewModel.shareResult { text ->
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, text)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, null)
                                context.startActivity(shareIntent)
                            }
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                        IconButton(onClick = { viewModel.clearResults() }) {
                            Icon(Icons.Default.Delete, contentDescription = "Clear")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            ChatInputBar(
                value = commandInput,
                onValueChange = { commandInput = it },
                onSend = {
                    if (commandInput.isNotBlank()) {
                        viewModel.processCommand(commandInput)
                        commandInput = ""
                    }
                },
                isLoading = state.isLoading,
                onStop = { viewModel.stopPing() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFF0C0C0C)) // Deep terminal black
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                item {
                    Text(
                        text = "ISP Network Tool [CLI Mode]",
                        color = Color(0xFF00FF00), // Terminal Green
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Type host address and press enter. Use '-t' for continuous ping.",
                        color = Color.Gray,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                items(state.results) { result ->
                    TerminalResultItem(result)
                }

                if (state.isLoading) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(10.dp),
                                strokeWidth = 1.5.dp,
                                color = Color(0xFF00FF00)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Executing...",
                                color = Color(0xFF00FF00),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    isLoading: Boolean,
    onStop: () -> Unit
) {
    Surface(
        tonalElevation = 8.dp,
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 12.dp)
                .navigationBarsPadding()
                .imePadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("google.com -t", fontSize = 14.sp) },
                shape = RoundedCornerShape(28.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSend() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                )
            )

            if (isLoading) {
                FloatingActionButton(
                    onClick = onStop,
                    containerColor = Color(0xFFCF6679), // Terminal Error Red
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.Stop, contentDescription = "Stop")
                }
            } else {
                FloatingActionButton(
                    onClick = onSend,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }
        }
    }
}

@Composable
fun TerminalResultItem(result: PingResult) {
    val text: String
    val color: Color

    when (result) {
        is PingResult.Success -> {
            text = result.fullLine
            color = if (result.rtt != null && result.rtt > 150) Color(0xFFFFB74D) else Color(0xFFE0E0E0)
        }
        is PingResult.Failure -> {
            text = "ERROR: ${result.error}"
            color = Color(0xFFCF6679)
        }
        is PingResult.Info -> {
            text = result.message
            color = Color(0xFF81D4FA) // Light Blue for info
        }
        else -> {
            text = ""
            color = Color.White
        }
    }

    if (text.isNotEmpty()) {
        Text(
            text = text,
            color = color,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            modifier = Modifier.padding(vertical = 1.dp)
        )
    }
}
