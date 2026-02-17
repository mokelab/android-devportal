package com.mokelab.android.devportal.logcat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LogcatScreen(
    viewModel: LogcatViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LogcatScreen(
        uiState = uiState,
        start = {
            viewModel.readLogcat()
        },
    )
}

@Composable
private fun LogcatScreen(
    uiState: LogcatViewModel.UiState,
    start: () -> Unit,
) {
    Scaffold { innerPadding ->
        when (uiState) {
            LogcatViewModel.UiState.Setting -> {
                SettingForm(
                    contentPadding = innerPadding,
                    start = start,
                )
            }

            is LogcatViewModel.UiState.Logcat -> {
                LogList(
                    logs = uiState.logs,
                    contentPadding = innerPadding,
                )
            }
        }
    }
}

@Composable
private fun SettingForm(
    contentPadding: PaddingValues,
    start: () -> Unit,
) {
    Column(modifier = Modifier.padding(contentPadding)) {
        Button(onClick = {
            start()
        }) {
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
