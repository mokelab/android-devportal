package com.mokelab.android.devportal.quickmenu.core

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.mokelab.android.devportal.quickmenu.api.QuickMenuItem
import dagger.hilt.android.EntryPointAccessors
import java.lang.ref.WeakReference

internal const val TAG = "QuickMenu"

internal object QuickMenu {
    private lateinit var application: Application
    private var resumedActivity: WeakReference<Activity>? = null

    fun install(application: Application) {
        this.application = application
        QuickMenuNotification.createChannel(application)
        // A notification left over from a process that died in the foreground (onStop never ran).
        QuickMenuNotification.cancel(application)
        application.registerActivityLifecycleCallbacks(ActivityTracker())
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStop(owner: LifecycleOwner) = QuickMenuNotification.cancel(application)
        })
    }

    fun open() {
        val activity = resumedActivity?.get()
        if (activity == null) {
            Log.w(TAG, "No resumed Activity. QuickMenu is not shown.")
            return
        }
        if (activity !is ComponentActivity) {
            Log.w(TAG, "${activity.javaClass.name} is not a ComponentActivity. QuickMenu is not shown.")
            return
        }
        val items = loadItems() ?: return
        QuickMenuOverlay.show(activity, items)
    }

    private fun loadItems(): List<QuickMenuItem>? = try {
        EntryPointAccessors.fromApplication(application, QuickMenuEntryPoint::class.java)
            .quickMenuItems()
            .sortedBy { it.priority }
    } catch (e: IllegalStateException) {
        Log.w(TAG, "Hilt is not set up (@HiltAndroidApp is required). QuickMenu is not shown.", e)
        null
    }

    private class ActivityTracker : Application.ActivityLifecycleCallbacks {
        override fun onActivityResumed(activity: Activity) {
            resumedActivity = WeakReference(activity)
            // Post (again) on every resume: covers the first start, a permission granted while in
            // the foreground and a notification swiped away (ongoing ones are dismissible on 14+).
            QuickMenuNotification.show(activity.applicationContext)
        }

        override fun onActivityPaused(activity: Activity) {
            if (resumedActivity?.get() === activity) resumedActivity = null
        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
        override fun onActivityStarted(activity: Activity) {}
        override fun onActivityStopped(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        override fun onActivityDestroyed(activity: Activity) {}
    }
}
