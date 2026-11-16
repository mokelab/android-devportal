package com.mokelab.android.devportal.quickmenu.core

import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.mokelab.android.devportal.quickmenu.api.QuickMenuItem
import java.lang.ref.WeakReference

/**
 * Attaches the menu to the Activity's decor view instead of starting another Activity,
 * so the Activity under the menu is never paused.
 */
internal object QuickMenuOverlay {
    private var pendingFocusListener: ViewTreeObserver.OnWindowFocusChangeListener? = null
    private var pendingActivity: WeakReference<ComponentActivity>? = null

    /**
     * Shows the menu. A broadcast notification action does not collapse the notification shade,
     * so while the shade covers the Activity (= the window has no focus) this waits until the
     * user closes it.
     */
    fun show(activity: ComponentActivity, items: List<QuickMenuItem>) {
        val decorView = activity.window.decorView as ViewGroup
        if (decorView.hasWindowFocus()) {
            attach(activity, decorView, items)
            return
        }
        // Already waiting on this Activity. A wait on an older Activity is simply superseded.
        if (pendingFocusListener != null && pendingActivity?.get() === activity) return
        val observer = decorView.viewTreeObserver
        val listener = object : ViewTreeObserver.OnWindowFocusChangeListener {
            override fun onWindowFocusChanged(hasFocus: Boolean) {
                if (!hasFocus) return
                // Removing a listener while it is being dispatched is not allowed.
                decorView.post { if (observer.isAlive) observer.removeOnWindowFocusChangeListener(this) }
                if (pendingFocusListener !== this) return
                pendingFocusListener = null
                pendingActivity = null
                if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                    attach(activity, decorView, items)
                }
            }
        }
        pendingFocusListener = listener
        pendingActivity = WeakReference(activity)
        observer.addOnWindowFocusChangeListener(listener)
    }

    private fun attach(activity: ComponentActivity, decorView: ViewGroup, items: List<QuickMenuItem>) {
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
