package com.ragr.antitheft.fsm

import android.content.Context
import kotlinx.coroutines.*
import com.ragr.antitheft.comms.EmergencyModule

object DelayFSM {
    private var job: Job? = null
    fun start(ctx: Context) {
        job?.cancel()
        job = CoroutineScope(Dispatchers.Default).launch {
            val delays = intArrayOf(2, 5, 10, 15)
            for (d in delays) {
                repeat(d * 60) { delay(1000L) }
            }
            EmergencyModule.escalate(ctx)
        }
    }
    fun cancel() { job?.cancel() }
}
