package com.ragr.antitheft.comms

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.telephony.SmsManager
import androidx.core.app.ActivityCompat
import com.ragr.antitheft.capture.LocationProvider
import com.ragr.antitheft.crypto.CryptoStore
import org.json.JSONArray

object EmergencyModule {
    suspend fun escalate(ctx: Context) {
        val settings = ctx.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val contacts = loadContacts(settings)
        val loc = LocationProvider.getCurrent(ctx)
        val link = if (loc != null) "https://maps.google.com/?q=${loc.latitude},${loc.longitude}" else "Location unavailable"
        val message = "EMERGENCY: Device may be stolen. " + link + " Call 110."

        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            val sms = SmsManager.getDefault()
            contacts.forEach { num -> sms.sendTextMessage(num, null, message, null, null) }
        }
        val callEnabled = settings.getBoolean("call_enabled", false)
        val num = settings.getString("police_number", "110") ?: "110"
        if (callEnabled && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            val i = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + num))
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ctx.startActivity(i)
        }
    }
    private fun loadContacts(prefs: SharedPreferences): List<String> {
        val enc = prefs.getString("contacts_enc", null) ?: return emptyList()
        val json = CryptoStore.decryptStringOrNull(enc) ?: return emptyList()
        val arr = JSONArray(json)
        return (0 until arr.length()).map { arr.getString(it) }
    }
}
