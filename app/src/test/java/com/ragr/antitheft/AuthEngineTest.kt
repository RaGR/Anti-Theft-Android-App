package com.ragr.antitheft

import com.ragr.antitheft.auth.AudioFeatureExtractor
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthEngineTest {
    @Test
    fun cosineSimilarityRange() {
        val a = FloatArray(10) { 1f }
        val b = FloatArray(10) { 1f }
        val c = FloatArray(10) { if (it%2==0) 1f else -1f }
        val s1 = AudioFeatureExtractor.cosine(a,b)
        val s2 = AudioFeatureExtractor.cosine(a,c)
        assertTrue(s1 > s2)
    }
}
