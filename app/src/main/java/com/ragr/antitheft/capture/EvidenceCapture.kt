package com.ragr.antitheft.capture

import android.content.Context
import android.graphics.ImageFormat
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.ragr.antitheft.crypto.CryptoStore
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class DummyLifecycleOwner(ctx: Context) : androidx.lifecycle.LifecycleOwner {
    private val lifecycle = androidx.lifecycle.LifecycleRegistry(this).apply {
        currentState = androidx.lifecycle.Lifecycle.State.STARTED
    }
    override fun getLifecycle() = lifecycle
}

object EvidenceCapture {
    suspend fun quickCaptureAndPersist(ctx: Context) {
        try {
            val file = captureFrontJpeg(ctx)
            val enc = CryptoStore.encryptFileToFile(ctx, file)
            Log.d("Evidence", "Captured & encrypted: ${enc.absolutePath}")
        } catch (t: Throwable) {
            Log.e("Evidence", "capture failed", t)
        }
    }
    private suspend fun captureFrontJpeg(ctx: Context): File {
        val cameraProvider = ProcessCameraProvider.getInstance(ctx).get()
        val imageCapture = ImageCapture.Builder()
            .setTargetRotation(0)
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setBufferFormat(ImageFormat.JPEG)
            .build()
        val selector = CameraSelector.DEFAULT_FRONT_CAMERA
        val executor = ContextCompat.getMainExecutor(ctx)
        cameraProvider.bindToLifecycle(DummyLifecycleOwner(ctx), selector, imageCapture)
        val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())
        val out = File(ctx.filesDir, "evidence/ev_$ts.jpg").apply { parentFile?.mkdirs() }
        return suspendCancellableCoroutine { cont ->
            imageCapture.takePicture(
                ImageCapture.OutputFileOptions.Builder(out).build(),
                executor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        cameraProvider.unbindAll(); cont.resume(out)
                    }
                    override fun onError(exception: ImageCaptureException) {
                        cameraProvider.unbindAll(); cont.resume(out)
                    }
                })
        }
    }
}
