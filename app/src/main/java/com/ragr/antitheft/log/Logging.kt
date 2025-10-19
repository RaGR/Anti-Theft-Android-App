package com.ragr.antitheft.log

import android.content.Context
import android.util.Log
import com.ragr.antitheft.crypto.CryptoStore
import java.io.File

object Logging {
    fun e(ctx: Context, tag: String, msg: String, t: Throwable? = null) {
        Log.e(tag, msg, t); write(ctx, "E", tag, msg)
    }
    fun d(ctx: Context, tag: String, msg: String) {
        Log.d(tag, msg); write(ctx, "D", tag, msg)
    }
    private fun write(ctx: Context, level: String, tag: String, msg: String) {
        val ts = System.currentTimeMillis()
        val safeMsg = msg.replace(""","'").trim()
        val line = "{"ts":" + ts + ","lvl":"" + level + "","tag":"" + tag + "","msg":"" + safeMsg + ""}"
        val enc = CryptoStore.encryptString(line)
        val f = File(ctx.filesDir, "logs/events.log.enc")
        f.parentFile?.mkdirs(); f.appendText(enc + "\n")
    }
}
