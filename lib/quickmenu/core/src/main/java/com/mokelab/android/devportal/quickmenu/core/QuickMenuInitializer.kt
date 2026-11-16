package com.mokelab.android.devportal.quickmenu.core

import android.app.Application
import android.content.Context
import androidx.lifecycle.ProcessLifecycleInitializer
import androidx.startup.Initializer

/**
 * Installs QuickMenu at process start via App Startup.
 *
 * Runs before `Application.onCreate`, so it must not touch Hilt. Menu items are resolved
 * lazily when the menu is opened.
 */
class QuickMenuInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        QuickMenu.install(context.applicationContext as Application)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> =
        listOf(ProcessLifecycleInitializer::class.java)
}
