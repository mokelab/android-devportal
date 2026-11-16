package com.mokelab.android.devportal.quickmenu.api

import androidx.activity.ComponentActivity

/**
 * Context passed to [QuickMenuItem.Content].
 */
interface QuickMenuScope {
    /**
     * The Activity the menu is shown on. It stays RESUMED while the menu is open.
     */
    val activity: ComponentActivity

    /**
     * Closes the menu.
     */
    fun dismiss()
}
