package com.mokelab.android.devportal.quickmenu.core

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mokelab.android.devportal.quickmenu.api.QuickMenuItem
import com.mokelab.android.devportal.quickmenu.api.QuickMenuScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun QuickMenuSheet(
    activity: ComponentActivity,
    items: List<QuickMenuItem>,
    onDismissed: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    val scope = remember(activity) {
        QuickMenuScopeImpl(activity) {
            coroutineScope.launch { sheetState.hide() }.invokeOnCompletion { onDismissed() }
        }
    }
    QuickMenuTheme {
        ModalBottomSheet(onDismissRequest = onDismissed, sheetState = sheetState) {
            QuickMenuContent(items = items, scope = scope)
        }
    }
}

private class QuickMenuScopeImpl(
    override val activity: ComponentActivity,
    private val onDismiss: () -> Unit,
) : QuickMenuScope {
    override fun dismiss() = onDismiss()
}

@Composable
private fun QuickMenuContent(items: List<QuickMenuItem>, scope: QuickMenuScope) {
    Column {
        Text(
            text = stringResource(R.string.quickmenu_core_sheet_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
        if (items.isEmpty()) {
            Text(
                text = stringResource(R.string.quickmenu_core_empty),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp),
            )
        } else {
            LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
                items(items) { item -> item.Content(scope) }
            }
        }
    }
}

@Composable
private fun QuickMenuTheme(content: @Composable () -> Unit) {
    val dark = isSystemInDarkTheme()
    val context = LocalContext.current
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        dark -> darkColorScheme()
        else -> lightColorScheme()
    }
    MaterialTheme(colorScheme = colorScheme, content = content)
}

private class PreviewItem(private val label: String) : QuickMenuItem {
    override val priority = 0

    @Composable
    override fun Content(scope: QuickMenuScope) {
        Text(label, modifier = Modifier.padding(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun QuickMenuContentPreview() {
    QuickMenuTheme {
        Surface {
            QuickMenuContent(
                items = listOf(PreviewItem("Item 1"), PreviewItem("Item 2")),
                scope = PreviewScope,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QuickMenuContentEmptyPreview() {
    QuickMenuTheme {
        Surface {
            QuickMenuContent(items = emptyList(), scope = PreviewScope)
        }
    }
}

/** Preview items must not touch [QuickMenuScope.activity]. */
private object PreviewScope : QuickMenuScope {
    override val activity: ComponentActivity get() = error("No Activity in preview")
    override fun dismiss() {}
}
