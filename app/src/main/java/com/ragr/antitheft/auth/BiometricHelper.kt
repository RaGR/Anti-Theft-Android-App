package com.ragr.antitheft.auth

import android.app.Activity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat

object BiometricHelper {
    private var enabled = false
    fun enableBiometric() { enabled = true }

    fun canAuth(activity: Activity): Boolean {
        val mgr = BiometricManager.from(activity)
        return enabled && mgr.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)
            == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun prompt(activity: Activity, onResult: (Boolean) -> Unit) {
        val executor = ContextCompat.getMainExecutor(activity)
        val prompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) = onResult(true)
            override fun onAuthenticationFailed() = onResult(false)
            override fun onAuthenticationError(code: Int, errString: CharSequence) = onResult(false)
        })
        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Confirm owner")
            .setSubtitle("Biometric verification")
            .setNegativeButtonText("Cancel")
            .build()
        prompt.authenticate(info)
    }
}
