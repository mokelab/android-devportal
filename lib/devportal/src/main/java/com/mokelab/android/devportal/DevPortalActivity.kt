package com.mokelab.android.devportal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import dagger.hilt.android.AndroidEntryPoint

/**
 * Entry point of DevPortal.
 */
@AndroidEntryPoint
class DevPortalActivity: ComponentActivity() {
    /*
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NavDisplay(
                backStack = navigator.backStack,
                onBack = { navigator.goBack() },
                entryProvider = entryProvider {
                    entry<DebugMenu> {
                        DebugMenuScreen(
                            navigator = navigator,
                            features = devFeatures.toList(),
                        )
                    }
                    devFeatures.forEach { feature -> feature.installer.invoke(this) }
                }
            )
        }
    }
    */
}
