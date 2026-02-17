package com.mokelab.android.devportal.logcat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LogcatViewModel : ViewModel() {
    sealed interface UiState {
        object Setting : UiState
        data class Logcat(
            val loading: Boolean,
            val logs: List<String>,
        ) : UiState
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Setting)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun readLogcat() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val process = Runtime.getRuntime().exec(arrayOf("logcat", "-v", "thread", "-d"))
                val lines = process.inputStream.reader().buffered().readLines()
                _uiState.value = UiState.Logcat(
                    loading = false,
                    logs = lines.takeLast(200) // Display the last 200 lines of logcat
                )
            } catch (e: Exception) {
                _uiState.value = UiState.Logcat(
                    loading = false,
                    logs = listOf("Failed to read logcat: ${e.message}")
                )
            }
        }
    }
}

