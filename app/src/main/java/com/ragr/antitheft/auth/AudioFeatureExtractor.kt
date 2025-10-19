package com.ragr.antitheft.auth

import kotlin.math.sqrt

object AudioFeatureExtractor {
    fun extract(pcm: ShortArray, sampleRate: Int): FloatArray {
        val win = sampleRate / 10
        val windows = (pcm.size / win).coerceAtMost(20)
        val rms = FloatArray(windows)
        val zcr = FloatArray(windows)
        for (i in 0 until windows) {
            var sum = 0.0
            var z = 0
            val start = i * win
            val end = minOf(pcm.size, start + win)
            var prev = pcm[start]
            for (j in start until end) {
                val v = pcm[j].toInt()
                sum += (v * v)
                if ((v >= 0 && prev < 0) || (v < 0 && prev >= 0)) z++
                prev = v.toShort()
            }
            val n = (end - start).coerceAtLeast(1)
            rms[i] = sqrt(sum / n).toFloat()
            zcr[i] = z.toFloat() / n
        }
        val vec = FloatArray(windows * 2)
        for (i in 0 until windows) { vec[i] = rms[i]; vec[windows + i] = zcr[i] }
        var norm = 1e-9f
        for (v in vec) norm += v * v
        val inv = 1.0f / sqrt(norm)
        for (i in vec.indices) vec[i] *= inv
        return vec
    }

    fun cosine(a: FloatArray, b: FloatArray): Float {
        val n = minOf(a.size, b.size)
        var dot = 0f; var na = 1e-9f; var nb = 1e-9f
        for (i in 0 until n) { dot += a[i] * b[i]; na += a[i]*a[i]; nb += b[i]*b[i] }
        return (dot / (sqrt(na) * sqrt(nb))).coerceIn(-1f, 1f)
    }
}
