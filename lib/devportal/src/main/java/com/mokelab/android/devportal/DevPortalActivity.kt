package com.mokelab.android.devportal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.mokelab.android.devportal.api.DevPortalFeature
import com.mokelab.android.devportal.api.DevPortalNavigator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Entry point of DevPortal.
 */
@AndroidEntryPoint
class DevPortalActivity : ComponentActivity() {
    @Inject
    lateinit var navigator: DevPortalNavigator

    @Inject
    lateinit var devPortalFeatures: Set<@JvmSuppressWildcards DevPortalFeature>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NavDisplay(
                backStack = navigator.backStack,
                onBack = { navigator.goBack() },
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator(),
                ),
                entryProvider = entryProvider {
                    entry<DevPortal> {
                        DevPortalScreen(
                            navigator = navigator,
                            features = devPortalFeatures.toList(),
                        )
                    }
                    devPortalFeatures.forEach { feature -> feature.installer.invoke(this) }
                }
            )
        }
    }
}
