package com.mokelab.android.devportal.logcat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LogcatScreen(
    back: () -> Unit,
    viewModel: LogcatViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LogcatScreen(
        uiState = uiState,
        start = { format ->
            viewModel.readLogcat(format)
        },
        back = back,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LogcatScreen(
    uiState: LogcatViewModel.UiState,
    start: (format: String) -> Unit,
    back: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Logcat")
                },
                navigationIcon = {
                    IconButton(onClick = back) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when (uiState) {
            LogcatViewModel.UiState.Setting -> {
                SettingForm(
                    contentPadding = innerPadding,
                    start = start,
                )
            }

            is LogcatViewModel.UiState.Logcat -> {
                if (uiState.loading) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                    ) {
                        CircularProgressIndicator()
                    }
                    return@Scaffold
                }
                LogList(
                    logs = uiState.logs,
                    contentPadding = innerPadding,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingForm(
    contentPadding: PaddingValues,
    start: (format: String) -> Unit,
) {
    val formats = listOf("brief", "process", "tag", "raw", "time", "threadtime", "long")
    var expanded by remember { mutableStateOf(false) }
    var selectedFormat by remember { mutableStateOf(formats[0]) }

    Column(modifier = Modifier.padding(contentPadding)) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
        ) {
            OutlinedTextField(
                value = selectedFormat,
                onValueChange = {},
                readOnly = true,
                label = { Text("Format") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                formats.forEach { format ->
                    DropdownMenuItem(
                        text = { Text(format) },
                        onClick = {
                            selectedFormat = format
                            expanded = false
                        }
                    )
                }
            }
        }

        Button(
            onClick = {
                start(selectedFormat)
            },
            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
        ) {
            Text("Start")
        }
    }
}

@Composable
private fun LogList(
    logs: List<String>,
    contentPadding: PaddingValues,
) {
    if (logs.isEmpty()) {
        Column(modifier = Modifier.padding(contentPadding)) {
            Text("No logs")
        }
        return
    }
    LazyColumn(
        contentPadding = contentPadding,
    ) {
        items(logs) { log ->
            ListItem(
                headlineContent = {
                    Text(log)
                }
            )
            HorizontalDivider()
        }
    }
}

@Preview
@Composable
private fun SettingFormPreview() {
    Scaffold { innerPadding ->
        SettingForm(
            contentPadding = innerPadding,
            start = {},
        )
    }
}
