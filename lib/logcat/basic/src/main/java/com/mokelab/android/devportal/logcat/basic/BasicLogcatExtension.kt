package com.mokelab.android.devportal.logcat.basic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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

        // LogcatFilterフォーム用の状態
        var filterTag by remember { mutableStateOf("") }
        val priorities = LogcatPriority.entries.toList()
        var priorityExpanded by remember { mutableStateOf(false) }
        var selectedPriority by remember { mutableStateOf(LogcatPriority.VERBOSE) }
        val filters = remember { mutableStateListOf<LogcatFilter>() }

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

        // LogcatFilter form
        Text("Filter", modifier = Modifier.padding(start = 16.dp, top = 16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
        ) {
            OutlinedTextField(
                value = filterTag,
                onValueChange = { filterTag = it },
                label = { Text("Tag") },
                modifier = Modifier.weight(1f)
            )
            ExposedDropdownMenuBox(
                expanded = priorityExpanded,
                onExpandedChange = { priorityExpanded = !priorityExpanded },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                OutlinedTextField(
                    value = selectedPriority.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Priority") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded)
                    },
                    modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(
                    expanded = priorityExpanded,
                    onDismissRequest = { priorityExpanded = false },
                ) {
                    priorities.forEach { priority ->
                        DropdownMenuItem(
                            text = { Text(priority.name) },
                            onClick = {
                                selectedPriority = priority
                                priorityExpanded = false
                            }
                        )
                    }
                }
            }
            Button(
                onClick = {
                    if (filterTag.isNotBlank()) {
                        filters.add(LogcatFilter(filterTag, selectedPriority))
                        filterTag = ""
                        selectedPriority = LogcatPriority.VERBOSE
                    }
                },
                modifier = Modifier.padding(start = 8.dp, end = 16.dp)
            ) {
                Text(stringResource(R.string.add))
            }
        }

        // Added filter list
        Column(
            modifier = Modifier.padding(start = 16.dp, top = 8.dp)
        ) {
            filters.forEachIndexed { index, filter ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("${filter.tag} [${filter.priority.name}]", modifier = Modifier.weight(1f))
                    IconButton(onClick = { filters.removeAt(index) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.remove_filter),
                        )
                    }
                }
            }
        }

        Button(
            onClick = {
                start(selectedFormat, filters.toList())
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