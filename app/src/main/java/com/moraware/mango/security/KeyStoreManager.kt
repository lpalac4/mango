package com.moraware.mango.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.moraware.mango.logger.MangoLogger
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.security.*
import java.util.logging.Level
import javax.crypto.*
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject


private const val AES_MODE = "AES/GCM/NoPadding"
private const val KEY_ALIAS = "Mango.alias"
private const val ANDROID_KEY_STORE = "AndroidKeyStore"
private const val IV_BYTE_LENGTH = 12

class KeyStoreManager(val mLogger: MangoLogger) {

    companion object {
        fun generateIV(): ByteArray{
            val secureRandom = SecureRandom()
            val ivBytes = ByteArray(IV_BYTE_LENGTH)
            secureRandom.nextBytes(ivBytes)

            return ivBytes
        }
    }

    fun encryptUserString(string: String, iv: ByteArray): String {
        return runCipher(CipherMode.ENCRYPT, string, iv)
    }

    fun decryptUserString(string: String, iv: ByteArray): String {
        return runCipher(CipherMode.DECRYPT, string, iv)
    }

    private fun createCipher(cipherMode: CipherMode, iv: ByteArray): Cipher {
        val key = getSecretKey()
        val cipher = Cipher.getInstance(AES_MODE)

        try {
            val spec = GCMParameterSpec(128, iv)
            val opmode = if (cipherMode == CipherMode.ENCRYPT) Cipher.ENCRYPT_MODE else Cipher.DECRYPT_MODE
            cipher.init(opmode, key, spec)
        } catch (error: NoSuchAlgorithmException) {
            mLogger.log(Level.SEVERE, "Unable to get Cipher instance")
        } catch (error: InvalidKeyException) {
            mLogger.log(Level.SEVERE, "Unable to init Cipher")
        }
        return cipher
    }

    private fun getSecretKey(): SecretKey? {
        var key: SecretKey? = null
        try {
            val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
            keyStore.load(null)
            if (!keyStore.containsAlias(KEY_ALIAS)) {
                key = createSecretKey()
            } else {
                key = keyStore.getKey(KEY_ALIAS, null) as SecretKey
            }
        } catch (error: KeyStoreException) {
            mLogger.log(Level.SEVERE, "Unable to get KeyStore instance or get key.")
        } catch (error: IOException) {
            mLogger.log(Level.SEVERE, "Unable to load KeyStore.")
        } catch (error: NoSuchAlgorithmException) {
            mLogger.log(Level.SEVERE, "Unable to load KeyStore or get key.")
        } catch (error: UnrecoverableKeyException) {
            mLogger.log(Level.SEVERE, "Unable to get key.")
        }
        return key
    }

    private fun createSecretKey(): SecretKey? {
        val builder = KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(false)
                .build()

        var key: SecretKey? = null
        try {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
            keyGenerator.init(builder)
            key = keyGenerator.generateKey()

        } catch (error: NoSuchAlgorithmException) {
            mLogger.log(Level.SEVERE, "Unable to get KeyGenerator instance.")
        } catch (error: NoSuchProviderException) {
            mLogger.log(Level.SEVERE, "Unable to get KeyGenerator instance.")
        } catch (error: InvalidAlgorithmParameterException) {
            mLogger.log(Level.SEVERE, "Unable to init KeyGenerator.")
        }
        return key
    }

    private fun runCipher(cipherMode: CipherMode, string: String, iv: ByteArray): String {
        var outputString = ""

        if (string.isEmpty()) {
            mLogger.log(Level.WARNING,"String is empty")
            return outputString
        }

        val cipher = createCipher(cipherMode, iv)

        try {
            val inputBytes = bytesFromString(cipherMode, string)
            val outputBytes = cipher.doFinal(inputBytes)
            outputString = stringFromBytes(cipherMode, outputBytes)
        } catch (error: IllegalBlockSizeException) {
            mLogger.log(Level.SEVERE, "Unable to run Cipher")
        } catch (error: BadPaddingException) {
            mLogger.log(Level.SEVERE, "Unable to run Cipher")
        } catch (error: UnsupportedEncodingException) {
            mLogger.log(Level.SEVERE, "Unable to convert between string & byte")
        } catch (error: IllegalStateException) {
            mLogger.log(Level.SEVERE, "Cipher key is possibly null")
        }
        return outputString
    }

    @Throws(UnsupportedEncodingException::class)
    private fun bytesFromString(cipherMode: CipherMode, string: String) =
            when (cipherMode) {
                CipherMode.ENCRYPT -> string.toByteArray(Charsets.UTF_8)
                CipherMode.DECRYPT -> Base64.decode(string, Base64.DEFAULT)
            }

    @Throws(UnsupportedEncodingException::class)
    private fun stringFromBytes(cipherMode: CipherMode, bytes: ByteArray) =
            when (cipherMode) {
                CipherMode.ENCRYPT -> Base64.encodeToString(bytes, Base64.DEFAULT)
                CipherMode.DECRYPT -> String(bytes, Charsets.UTF_8)
            }

    enum class CipherMode {
        ENCRYPT,
        DECRYPT
    }
}