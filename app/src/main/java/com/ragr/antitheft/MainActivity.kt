package com.ragr.antitheft

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ragr.antitheft.service.ForegroundInterceptService

class MainActivity : ComponentActivity() {

    private val permissions = arrayOf(
        Manifest.permission.POST_NOTIFICATIONS,
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.SEND_SMS,
        Manifest.permission.CALL_PHONE
    )

    private val permLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) {
                    HomeScreen(
                        onGrantPerms = { permLauncher.launch(permissions) },
                        onOpenEnrollment = { startActivity(Intent(this, EnrollmentActivity::class.java)) },
                        onOpenSettings = { startActivity(Intent(this, SettingsScreenActivity::class.java)) },
                        onStartProtection = { ForegroundInterceptService.start(this) },
                        onStopProtection = { ForegroundInterceptService.stop(this) }
                    )
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    onGrantPerms: () -> Unit,
    onOpenEnrollment: () -> Unit,
    onOpenSettings: () -> Unit,
    onStartProtection: () -> Unit,
    onStopProtection: () -> Unit
) {
    Column(Modifier.padding(16.dp)) {
        Text("Anti-Theft MVP", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Row {
            Button(onClick = onGrantPerms) { Text("Grant Permissions") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = onOpenEnrollment) { Text("Enrollment") }
        }
        Spacer(Modifier.height(8.dp))
        Row { Button(onClick = onOpenSettings) { Text("Settings") } }
        Spacer(Modifier.height(16.dp))
        Row {
            Button(onClick = onStartProtection) { Text("Start Protection") }
            Spacer(Modifier.width(8.dp))
            OutlinedButton(onClick = onStopProtection) { Text("Stop") }
        }
    }
}

class SettingsScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { SettingsScreen() } }
    }
}
