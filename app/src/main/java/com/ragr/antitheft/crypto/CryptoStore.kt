package com.ragr.antitheft.crypto

import android.content.Context
import android.util.Base64
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object CryptoStore {
    private fun getOrCreateKey(): SecretKey {
        val kg = KeyGenerator.getInstance("AES"); kg.init(256); return kg.generateKey()
    }
    fun encryptBytesToBase64(plain: ByteArray): String {
        val key = getOrCreateKey()
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val iv = cipher.iv
        val out = cipher.doFinal(plain)
        val combo = ByteArray(iv.size + out.size)
        System.arraycopy(iv, 0, combo, 0, iv.size)
        System.arraycopy(out, 0, combo, iv.size, out.size)
        return Base64.encodeToString(combo, Base64.NO_WRAP)
    }
    fun decryptBase64ToBytes(enc: String): ByteArray? = try {
        val combo = Base64.decode(enc, Base64.NO_WRAP)
        val iv = combo.copyOfRange(0, 12)
        val ct = combo.copyOfRange(12, combo.size)
        val key = getOrCreateKey()
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))
        cipher.doFinal(ct)
    } catch (_: Throwable) { null }
    fun encryptString(s: String) = encryptBytesToBase64(s.toByteArray())
    fun decryptStringOrNull(enc: String) = decryptBase64ToBytes(enc)?.decodeToString()
    fun encryptFileToFile(ctx: Context, inFile: File): File {
        val key = getOrCreateKey()
        val cipher = Cipher.getInstance("AES/GCM/NoPadding"); cipher.init(Cipher.ENCRYPT_MODE, key)
        val iv = cipher.iv
        val outFile = File(ctx.filesDir, "evidence/${inFile.nameWithoutExtension}.enc")
        outFile.parentFile?.mkdirs()
        FileOutputStream(outFile).use { fos ->
            fos.write(iv)
            val buf = ByteArray(4096)
            FileInputStream(inFile).use { fis ->
                while (true) { val r = fis.read(buf); if (r <= 0) break
                    val enc = cipher.update(buf, 0, r); if (enc != null) fos.write(enc) }
            }
            val final = cipher.doFinal(); if (final != null) fos.write(final)
        }
        return outFile
    }
}
