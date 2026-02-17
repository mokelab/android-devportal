package com.mokelab.android.devportal.logcat

import androidx.navigation3.runtime.EntryProviderScope
import com.mokelab.android.devportal.api.DevPortalFeature
import com.mokelab.android.devportal.api.DevPortalNavigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

object LogcatRoot

@Module
@InstallIn(ActivityRetainedComponent::class)
object DevPortalLogcatModule {
    @IntoSet
    @Provides
    fun provideDevPortalLogcatFeature(
        navigator: DevPortalNavigator,
    ): DevPortalFeature {
        return object : DevPortalFeature {
            override val name: String = "Logcat Viewer"
            override val installer: EntryProviderScope<Any>.() -> Unit = {
                entry<LogcatRoot> {
                    LogcatScreen(
                        back = {
                            navigator.goBack()
                        }
                    )
                }
            }
            override val root: Any get() = LogcatRoot
        }
    }
}