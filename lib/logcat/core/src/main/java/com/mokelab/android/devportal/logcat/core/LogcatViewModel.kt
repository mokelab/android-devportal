package com.mokelab.android.devportal.logcat.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mokelab.android.devportal.logcat.api.LogcatFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogcatViewModel @Inject constructor() : ViewModel() {

    sealed interface UiState {
        object Setting : UiState
        data class Logcat(
            val loading: Boolean,
            val logs: List<String>,
            val format: LogcatFormat // 追加: 現在のフォーマット
        ) : UiState
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Setting)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun readLogcat(format: LogcatFormat) {
        _uiState.value = UiState.Logcat(
            loading = true,
            logs = emptyList(),
            format = format // 追加
        )

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val process =
                    Runtime.getRuntime().exec(arrayOf("logcat", "-v", format.formatArg, "-d"))
                val lines = process.inputStream.reader().buffered().readLines()
                _uiState.value = UiState.Logcat(
                    loading = false,
                    logs = lines.takeLast(200), // Display the last 200 lines of logcat
                    format = format // 追加
                )
            } catch (e: Exception) {
                _uiState.value = UiState.Logcat(
                    loading = false,
                    logs = listOf("Failed to read logcat: ${e.message}"),
                    format = format // 追加
                )
            }
        }
    }

    fun toSetting() {
        _uiState.value = UiState.Setting
    }
}
