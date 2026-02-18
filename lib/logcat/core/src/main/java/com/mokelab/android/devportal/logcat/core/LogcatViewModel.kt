package com.mokelab.android.devportal.logcat.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mokelab.android.devportal.logcat.api.LogcatExtension
import com.mokelab.android.devportal.logcat.api.LogcatFilter
import com.mokelab.android.devportal.logcat.api.LogcatFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogcatViewModel @Inject constructor(
    extensions: Set<@JvmSuppressWildcards LogcatExtension>
) : ViewModel() {

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

    val extensions = extensions.sortedBy { it.priority }

    fun readLogcat(
        format: LogcatFormat,
        filters: List<LogcatFilter>,
    ) {
        _uiState.value = UiState.Logcat(
            loading = true,
            logs = emptyList(),
            format = format // 追加
        )

        extensions.forEach { it.onStartReadLog(format) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val args = buildList {
                    add("logcat")
                    add("-v")
                    add(format.formatArg)
                    filters.forEach { filter ->
                        add(filter.toString())
                    }
                    add("-d") // add -d option to dump the log and exit
                }
                val process = Runtime.getRuntime().exec(args.toTypedArray())
                val lines = process.inputStream.reader().buffered().readLines()

                val logs = lines.takeLast(200) // Display the last 200 lines of logcat
                extensions.forEach { it.onFinishReadLog(format, logs) }

                _uiState.value = UiState.Logcat(
                    loading = false,
                    logs = logs,
                    format = format,
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
