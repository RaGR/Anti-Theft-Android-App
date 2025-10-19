package com.ragr.antitheft

import com.ragr.antitheft.crypto.CryptoStore
import org.junit.Assert.assertNotNull
import org.junit.Test

class CryptoStoreTest {
    @Test
    fun roundtrip() {
        val s = "hello"
        val enc = CryptoStore.encryptString(s)
        val dec = CryptoStore.decryptStringOrNull(enc)
        assertNotNull(dec)
        assert(dec == s)
    }
}
