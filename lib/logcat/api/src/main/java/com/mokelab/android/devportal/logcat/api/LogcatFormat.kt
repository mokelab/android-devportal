package com.mokelab.android.devportal.logcat.api

/**
 * Represents the logcat output format.
 * This is used to specify the format of the logcat output when reading logs.
 *
 * https://developer.android.com/tools/logcat#format-specifiers
 */
enum class LogcatFormat(val formatArg: String) {
    BRIEF("brief"),
    PROCESS("process"),
    TAG("tag"),
    RAW("raw"),
    TIME("time"),
    THREADTIME("threadtime"),
    LONG("long");
}
