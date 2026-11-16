package com.mokelab.android.devportal.quickmenu.core

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import dagger.hilt.android.EntryPointAccessors
import java.lang.ref.WeakReference

internal const val TAG = "QuickMenu"

internal object QuickMenu {
    private lateinit var application: Application
    private var resumedActivity: WeakReference<Activity>? = null

    fun install(application: Application) {
        this.application = application
        QuickMenuNotification.createChannel(application)
        application.registerActivityLifecycleCallbacks(ActivityTracker())
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) = QuickMenuNotification.show(application)
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
        QuickMenuOverlay.show(activity, loadItems())
    }

    private fun loadItems() =
        EntryPointAccessors.fromApplication(application, QuickMenuEntryPoint::class.java)
            .quickMenuItems()
            .sortedBy { it.priority }

    private class ActivityTracker : Application.ActivityLifecycleCallbacks {
        override fun onActivityResumed(activity: Activity) {
            resumedActivity = WeakReference(activity)
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
