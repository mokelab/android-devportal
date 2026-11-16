package com.mokelab.android.devportal.quickmenu.api

import androidx.compose.runtime.Composable

/**
 * An item displayed in the QuickMenu bottom sheet.
 *
 * Contribute implementations with `@Provides @IntoSet` in a Hilt module installed in
 * `SingletonComponent`. Items are shown in ascending order of [priority].
 */
interface QuickMenuItem {
    val priority: Int

    /**
     * Renders this item as one row of the menu.
     *
     * @param scope Gives access to the Activity under the sheet and lets the item close the sheet.
     */
    @Composable
    fun Content(scope: QuickMenuScope)
}
