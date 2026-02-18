package com.mokelab.android.devportal.logcat.basic

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mokelab.android.devportal.logcat.api.LogcatExtension
import com.mokelab.android.devportal.logcat.api.LogcatFilter
import com.mokelab.android.devportal.logcat.api.LogcatFormat
import com.mokelab.android.devportal.logcat.api.LogcatPriority
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

private val basicExtension = object : LogcatExtension {
    override val priority: Int
        get() = 0

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun SettingContent(
        start: (format: LogcatFormat, filters: List<LogcatFilter>) -> Unit,
    ) {
        val formats = LogcatFormat.entries.toList()
        var expanded by remember { mutableStateOf(false) }
        var selectedFormat by remember { mutableStateOf(formats[0]) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
        ) {
            OutlinedTextField(
                value = selectedFormat.formatArg,
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
                        text = { Text(format.formatArg) },
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
                start(selectedFormat, listOf(LogcatFilter("*", LogcatPriority.VERBOSE)))
            },
            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
        ) {
            Text("Start")
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
object BasicLogcatExtensionModule {
    @Provides
    @IntoSet
    fun provideLogcatExtension(): LogcatExtension = basicExtension
}