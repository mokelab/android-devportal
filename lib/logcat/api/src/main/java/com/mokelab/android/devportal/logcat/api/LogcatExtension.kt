package com.mokelab.android.devportal.logcat.api

import androidx.compose.runtime.Composable

/**
 * Interface for logcat extensions.
 * This allows developers to create custom logcat extensions that can be used to read logs in different formats or with different filters.
 */
interface LogcatExtension {
    val priority: Int

    /**
     * Called when the logcat reader starts reading logs.
     *
     * @param format The format of the logcat output.
     */
    fun onStartReadLog(format: LogcatFormat) {}

    /**
     * Called when the logcat reader finishes reading logs.
     *
     * @param format The format of the logcat output.
     * @param logs The list of log lines that were read from logcat.
     */
    fun onFinishReadLog(format: LogcatFormat, logs: List<String>) {}

    /**
     * Composable function to display the settings for the logcat extension.
     *
     * @param start A callback function that should be called when the user wants to start reading logs.
     */
    @Composable
    fun SettingContent(start: (format: LogcatFormat, filters: List<LogcatFilter>) -> Unit)
}