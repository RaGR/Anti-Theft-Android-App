package com.ragr.antitheft.boot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ragr.antitheft.service.ForegroundInterceptService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            ForegroundInterceptService.start(context)
        }
    }
}
