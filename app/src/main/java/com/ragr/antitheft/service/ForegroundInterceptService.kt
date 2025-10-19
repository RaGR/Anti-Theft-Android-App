package com.ragr.antitheft.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.ragr.antitheft.MainActivity
import com.ragr.antitheft.R
import com.ragr.antitheft.auth.AuthEngine
import com.ragr.antitheft.capture.EvidenceCapture

class ForegroundInterceptService : android.app.Service() {

    private val closeDialogsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_CLOSE_SYSTEM_DIALOGS) {
                val reason = intent.getStringExtra("reason")
                if (reason == "globalactions") {
                    ShutdownMonitor.onPowerMenuInvoked(this@ForegroundInterceptService)
                }
            }
        }
    }

    private val shutdownReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            EvidenceCapture.quickCaptureAndPersist(applicationContext)
        }
    }

    override fun onCreate() {
        super.onCreate()
        AuthEngine.init(applicationContext)
        registerReceiver(closeDialogsReceiver, IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
        registerReceiver(shutdownReceiver, IntentFilter(Intent.ACTION_SHUTDOWN))
        startForeground(NOTIF_ID, buildNotification())
    }

    override fun onDestroy() {
        unregisterReceiver(closeDialogsReceiver)
        unregisterReceiver(shutdownReceiver)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(): Notification {
        val mgr = getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= 26) {
            val ch = NotificationChannel(CH_ID, getString(R.string.notif_channel_name),
                NotificationManager.IMPORTANCE_LOW)
            ch.description = getString(R.string.notif_channel_desc)
            mgr.createNotificationChannel(ch)
        }
        val pi = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Builder(this, CH_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Security monitoring active")
            .setContentText("Tap to open")
            .setContentIntent(pi)
            .setOngoing(true)
            .build()
    }

    companion object {
        private const val CH_ID = "protect"
        private const val NOTIF_ID = 1101
        fun start(ctx: Context) { ctx.startForegroundService(Intent(ctx, ForegroundInterceptService::class.java)) }
        fun stop(ctx: Context) { ctx.stopService(Intent(ctx, ForegroundInterceptService::class.java)) }
    }
}
