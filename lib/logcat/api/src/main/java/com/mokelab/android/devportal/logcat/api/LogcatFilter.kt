package com.mokelab.android.devportal.logcat.api

/**
 * Represents a filter for logcat logs.
 * This is used to specify the tag and priority of the logs to be filtered when reading logs.
 *
 * @param tag The tag of the logs to be filtered. Use "*" to match all tags.
 * @param priority The priority of the logs to be filtered. Use LogcatPriority.VERBOSE to match all priorities.
 */
data class LogcatFilter(
    val tag: String,
    val priority: LogcatPriority
) {
    override fun toString(): String {
        return "$tag:${priority.priorityArg}"
    }
}

/**
 * Represents the priority of logcat logs.
 * This is used to specify the priority of the logs to be filtered when reading logs.
 *
 * https://developer.android.com/tools/logcat#filteringOutput
 */
enum class LogcatPriority(val priorityArg: String) {
    VERBOSE("V"),
    DEBUG("D"),
    INFO("I"),
    WARN("W"),
    ERROR("E"),
    FATAL("F"),
    SILENT("S")
}