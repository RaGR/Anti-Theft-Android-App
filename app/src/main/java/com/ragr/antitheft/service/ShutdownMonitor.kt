package com.ragr.antitheft.service

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.ragr.antitheft.EnrollmentActivity

object ShutdownMonitor {
    fun onPowerMenuInvoked(ctx: Context) { ConfirmOwnerActivity.start(ctx) }
}

class ConfirmOwnerActivity : Activity() {
    override fun onStart() {
        super.onStart()
        startActivity(Intent(this, EnrollmentActivity::class.java))
        finish()
    }
    companion object {
        fun start(ctx: Context) {
            val i = Intent(ctx, ConfirmOwnerActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ContextCompat.startActivity(ctx, i, null)
        }
    }
}
