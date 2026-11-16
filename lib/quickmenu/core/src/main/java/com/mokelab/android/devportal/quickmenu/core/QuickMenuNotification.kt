package com.mokelab.android.devportal.quickmenu.core

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

internal object QuickMenuNotification {
    private const val CHANNEL_ID = "com.mokelab.android.devportal.quickmenu"
    private const val NOTIFICATION_ID = 0x514d454e // "QMEN"

    fun createChannel(context: Context) {
        val channel = NotificationChannelCompat.Builder(
            CHANNEL_ID,
            NotificationManagerCompat.IMPORTANCE_LOW,
        ).setName(context.getString(R.string.quickmenu_core_channel_name)).build()
        NotificationManagerCompat.from(context).createNotificationChannel(channel)
    }

    @SuppressLint("MissingPermission") // checked by areNotificationsEnabled()
    fun show(context: Context) {
        val manager = NotificationManagerCompat.from(context)
        if (!manager.areNotificationsEnabled()) {
            Log.w(TAG, "Notifications are disabled. Grant POST_NOTIFICATIONS to use QuickMenu.")
            return
        }
        val openIntent = PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, QuickMenuReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.quickmenu_core_ic_notification)
            .setContentTitle(context.getString(R.string.quickmenu_core_notification_title))
            .setContentText(context.getString(R.string.quickmenu_core_notification_text))
            .addAction(0, context.getString(R.string.quickmenu_core_action_open), openIntent)
            .setOngoing(true)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        manager.notify(NOTIFICATION_ID, notification)
    }

    fun cancel(context: Context) {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
    }
}
