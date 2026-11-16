package com.mokelab.android.devportal.quickmenu.core

import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.mokelab.android.devportal.quickmenu.api.QuickMenuItem

/**
 * Attaches the menu to the Activity's decor view instead of starting another Activity,
 * so the Activity under the menu is never paused.
 *
 * A broadcast notification action does not collapse the notification shade, so the sheet opens
 * behind the shade and is visible once the user closes it. It is shown immediately (rather than
 * waiting for window focus) so it also works while the host shows a dialog.
 */
internal object QuickMenuOverlay {
    fun show(activity: ComponentActivity, items: List<QuickMenuItem>) {
        val decorView = activity.window.decorView as ViewGroup
        if (decorView.findViewById<View>(R.id.quickmenu_core_overlay) != null) return

        val view = ComposeView(activity).apply {
            id = R.id.quickmenu_core_overlay
            setViewTreeLifecycleOwner(activity)
            setViewTreeViewModelStoreOwner(activity)
            setViewTreeSavedStateRegistryOwner(activity)
        }
        // Removing the view disposes the composition; post it so it never happens mid-composition.
        val remove = { decorView.post { decorView.removeView(view) } }
        view.setContent {
            QuickMenuSheet(activity = activity, items = items, onDismissed = { remove() })
        }
        // ModalBottomSheet draws in its own window, so the host view can be zero-sized.
        decorView.addView(view, ViewGroup.LayoutParams(0, 0))
    }
}
