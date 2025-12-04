package com.rudra.ispnetworktool.presentation.pppoe_server_setup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PppoeServerSetupScreen(
    viewModel: PppoeServerSetupViewModel = hiltViewModel()
) {
    val state = viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PPPoE Server Setup") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LinearProgressIndicator(
                progress = (state.currentStep + 1) / state.steps.size.toFloat(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            StepContent(step = state.steps[state.currentStep])
            Spacer(modifier = Modifier.height(24.dp))
            StepNavigation(viewModel, state)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepContent(step: PppoeStep) {
    val clipboardManager = LocalClipboardManager.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = step.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = step.description,
                style = MaterialTheme.typography.bodyLarge
            )
            step.command?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(16.dp)
                ) {
                    Text(
                        text = it,
                        fontFamily = FontFamily.Monospace
                    )
                    IconButton(
                        onClick = { clipboardManager.setText(AnnotatedString(it)) },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy Command")
                    }
                }
            }
        }
    }
}

@Composable
fun StepNavigation(viewModel: PppoeServerSetupViewModel, state: PppoeServerSetupState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = viewModel::onPreviousStep,
            enabled = state.currentStep > 0
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Previous")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Previous")
        }
        Button(
            onClick = viewModel::onNextStep,
            enabled = state.currentStep < state.steps.size - 1
        ) {
            Text("Next")
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.ArrowForward, contentDescription = "Next")
        }
    }
}
