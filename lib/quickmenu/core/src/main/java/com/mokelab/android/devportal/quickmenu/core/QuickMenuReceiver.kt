package com.mokelab.android.devportal.quickmenu.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Receives the notification action and opens the menu on the resumed Activity.
 */
class QuickMenuReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        QuickMenu.open()
    }
}
