package com.ragr.antitheft

import android.Manifest
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ragr.antitheft.auth.AudioFeatureExtractor
import com.ragr.antitheft.auth.AuthEngine
import com.ragr.antitheft.auth.BiometricHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EnrollmentActivity : ComponentActivity() {

    private val micPerm = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AuthEngine.init(applicationContext)
        micPerm.launch(Manifest.permission.RECORD_AUDIO)
        setContent { MaterialTheme { EnrollmentScreen() } }
    }
}

@Composable
fun EnrollmentScreen() {
    val scope = rememberCoroutineScope()
    var status by remember { mutableStateOf("Record 5 short phrases when ready.") }
    var enrolled by remember { mutableStateOf(false) }
    var pin by remember { mutableStateOf("") }

    Column(Modifier.padding(16.dp)) {
        Text("Enrollment", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            scope.launch {
                status = "Recording..."
                val features = mutableListOf<FloatArray>()
                repeat(5) {
                    val pcm = record3Seconds()
                    val vec = AudioFeatureExtractor.extract(pcm, sampleRate = 16000)
                    features += vec
                    status = "Sample ${it + 1}/5 captured"
                }
                val ok = AuthEngine.storeVoiceTemplate(features)
                enrolled = ok
                status = if (ok) "Voice enrolled." else "Enroll failed."
            }
        }) { Text("Record 5 Phrases") }

        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = pin, onValueChange = { pin = it }, label = { Text("Set PIN (4-8 digits)") })
        Spacer(Modifier.height(8.dp))
        Button(onClick = { val ok = AuthEngine.storePin(pin); status = if (ok) "PIN saved." else "PIN invalid." }) { Text("Save PIN") }

        Spacer(Modifier.height(8.dp))
        Button(onClick = { BiometricHelper.enableBiometric(); status = "Biometric enabled (if available)." }) { Text("Enable Biometric") }

        Spacer(Modifier.height(16.dp))
        Text("Status: $status")
        if (enrolled) Text("✅ Enrollment complete")
    }
}

private suspend fun record3Seconds(sampleRate: Int = 16000): ShortArray = withContext(Dispatchers.Default) {
    val minBuf = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
    val rec = AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION, sampleRate,
        AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, minBuf * 2)
    val total = sampleRate * 3
    val buf = ShortArray(total)
    rec.startRecording()
    var read = 0
    while (read < total) { read += rec.read(buf, read, minOf(1024, total - read)) }
    rec.stop(); rec.release(); buf
}
