package com.ragr.antitheft.capture

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object LocationProvider {
    @SuppressLint("MissingPermission")
    suspend fun getCurrent(ctx: Context): Location? {
        val fused = LocationServices.getFusedLocationProviderClient(ctx)
        return suspendCancellableCoroutine { cont ->
            fused.lastLocation.addOnSuccessListener { cont.resume(it) }
                .addOnFailureListener { cont.resume(null) }
        }
    }
}
