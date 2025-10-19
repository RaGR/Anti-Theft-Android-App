package com.ragr.antitheft.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.ragr.antitheft.crypto.CryptoStore
import java.security.MessageDigest
import java.nio.ByteBuffer

object AuthEngine {
    private lateinit var prefs: SharedPreferences
    fun init(ctx: Context) { prefs = ctx.getSharedPreferences("auth", Context.MODE_PRIVATE) }

    fun storeVoiceTemplate(samples: List<FloatArray>): Boolean {
        if (samples.size < 3) return false
        val n = samples.maxOf { it.size }
        val avg = FloatArray(n)
        for (s in samples) for (i in s.indices) if (i < n) avg[i] += s[i]
        for (i in avg.indices) avg[i] /= samples.size
        val bytes = ByteArray(avg.size * 4)
        ByteBuffer.wrap(bytes).asFloatBuffer().put(avg)
        val enc = CryptoStore.encryptBytesToBase64(bytes)
        prefs.edit().putString("voice_template", enc).apply()
        return true
    }

    fun loadVoiceTemplate(): FloatArray? {
        val enc = prefs.getString("voice_template", null) ?: return null
        val dec = CryptoStore.decryptBase64ToBytes(enc) ?: return null
        val fb = java.nio.ByteBuffer.wrap(dec).asFloatBuffer()
        val arr = FloatArray(fb.remaining()); fb.get(arr); return arr
    }

    fun verifyVoice(probe: FloatArray, threshold: Float = 0.78f): Boolean {
        val ref = loadVoiceTemplate() ?: return false
        val score = AudioFeatureExtractor.cosine(ref, probe)
        return score >= threshold
    }

    fun storePin(pin: String): Boolean {
        if (pin.length !in 4..8 || !pin.all { it.isDigit() }) return false
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update("s@lt".toByteArray())
        val hash = digest.digest(pin.toByteArray())
        val b64 = Base64.encodeToString(hash, Base64.NO_WRAP)
        prefs.edit().putString("pin_hash", b64).apply()
        return true
    }

    fun verifyPin(pin: String): Boolean {
        val saved = prefs.getString("pin_hash", null) ?: return false
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update("s@lt".toByteArray())
        val hash = digest.digest(pin.toByteArray())
        val b64 = Base64.encodeToString(hash, Base64.NO_WRAP)
        return saved == b64
    }
}
