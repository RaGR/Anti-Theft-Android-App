package com.ragr.antitheft

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ragr.antitheft.crypto.CryptoStore
import org.json.JSONArray

@Composable
fun SettingsScreen() {
    val ctx = LocalContext.current
    val prefs = remember { ctx.getSharedPreferences("settings", Context.MODE_PRIVATE) }
    var contactsText by remember { mutableStateOf(loadContacts(prefs)) }
    var callEnabled by remember { mutableStateOf(prefs.getBoolean("call_enabled", false)) }
    var policeNumber by remember { mutableStateOf(prefs.getString("police_number", "110") ?: "110") }

    Column(Modifier.padding(16.dp)) {
        Text("Settings", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Text("Fixed Contacts (one per line, E.164):")
        OutlinedTextField(
            value = contactsText,
            onValueChange = { contactsText = it },
            modifier = Modifier.fillMaxWidth().height(120.dp)
        )
        Spacer(Modifier.height(8.dp))
        Row {
            Checkbox(checked = callEnabled, onCheckedChange = {
                callEnabled = it; prefs.edit().putBoolean("call_enabled", it).apply()
            })
            Spacer(Modifier.width(8.dp))
            Text("Enable auto-call after final escalation")
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = policeNumber,
            onValueChange = { policeNumber = it; prefs.edit().putString("police_number", it).apply() },
            label = { Text("Emergency number") }
        )
        Spacer(Modifier.height(12.dp))
        Button(onClick = {
            saveContacts(prefs, contactsText)
            Toast.makeText(ctx, "Saved", Toast.LENGTH_SHORT).show()
        }) { Text("Save") }
    }
}

private fun loadContacts(prefs: SharedPreferences): String {
    val enc = prefs.getString("contacts_enc", null) ?: return ""
    val dec = CryptoStore.decryptStringOrNull(enc) ?: return ""
    val arr = JSONArray(dec)
    return (0 until arr.length()).joinToString("\n") { arr.getString(it) }
}

private fun saveContacts(prefs: SharedPreferences, text: String) {
    val list = text.lines().mapNotNull { val t = it.trim(); if (t.isEmpty()) null else t }
    val json = JSONArray(list).toString()
    val enc = CryptoStore.encryptString(json)
    prefs.edit().putString("contacts_enc", enc).apply()
}
