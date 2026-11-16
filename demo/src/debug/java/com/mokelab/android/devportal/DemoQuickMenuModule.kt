package com.mokelab.android.devportal

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.ListItem
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mokelab.android.devportal.quickmenu.api.QuickMenuItem
import com.mokelab.android.devportal.quickmenu.api.QuickMenuScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

/** Shows the lifecycle state of the Activity under the sheet (should stay RESUMED). */
private val lifecycleItem = object : QuickMenuItem {
    override val priority = 0

    @Composable
    override fun Content(scope: QuickMenuScope) {
        val state by scope.activity.lifecycle.currentStateFlow.collectAsState()
        ListItem(
            headlineContent = { Text(scope.activity.javaClass.simpleName) },
            supportingContent = { Text("Lifecycle: $state") },
        )
    }
}

private var sampleFlag by mutableStateOf(false)

private val flagItem = object : QuickMenuItem {
    override val priority = 10

    @Composable
    override fun Content(scope: QuickMenuScope) {
        ListItem(
            headlineContent = { Text("Sample flag") },
            trailingContent = { Switch(checked = sampleFlag, onCheckedChange = { sampleFlag = it }) },
        )
    }
}

private val toastItem = object : QuickMenuItem {
    override val priority = 20

    @Composable
    override fun Content(scope: QuickMenuScope) {
        TextButton(
            onClick = {
                Toast.makeText(scope.activity, "Hello from QuickMenu", Toast.LENGTH_SHORT).show()
                scope.dismiss()
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        ) { Text("Show toast and close") }
    }
}

private val devPortalItem = object : QuickMenuItem {
    override val priority = 30

    @Composable
    override fun Content(scope: QuickMenuScope) {
        TextButton(
            onClick = {
                scope.activity.startActivity(
                    Intent(scope.activity, DevPortalActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
                scope.dismiss()
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        ) { Text("Open DevPortal") }
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DemoQuickMenuModule {
    @Provides @IntoSet fun lifecycle(): QuickMenuItem = lifecycleItem
    @Provides @IntoSet fun flag(): QuickMenuItem = flagItem
    @Provides @IntoSet fun toast(): QuickMenuItem = toastItem
    @Provides @IntoSet fun devPortal(): QuickMenuItem = devPortalItem
}
