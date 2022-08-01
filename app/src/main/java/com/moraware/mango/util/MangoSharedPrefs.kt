package com.moraware.mango.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.annotation.VisibleForTesting
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.moraware.mango.logger.MangoLogger
import com.moraware.mango.security.KeyStoreManager
import com.moraware.mango.util.Preferences.PREF_ACCESS_TOKEN
import com.moraware.mango.util.Preferences.PREF_DARK_MODE_ENABLED
import com.moraware.mango.util.Preferences.PREF_EMAIL
import com.moraware.mango.util.Preferences.PREF_IV
import com.moraware.mango.util.Preferences.PREF_NOTIFICATIONS_ENABLED
import com.moraware.mango.util.Preferences.PREF_REFRESH_TOKEN
import com.moraware.mango.util.Preferences.PREF_TERMS_OF_SERVICE
import java.lang.reflect.Type
import java.util.logging.Level

class MangoSharedPrefs(context: Context, val keyStoreManager: KeyStoreManager, val mLogger: MangoLogger) {

    var preferences: SharedPreferences = context.getSharedPreferences(Preferences.NAME, Context.MODE_PRIVATE)

    var email: String
        get() = loadAndDecryptString(PREF_EMAIL)
        set(value) = encryptAndSaveString(PREF_EMAIL, value)

    var notificationsEnabled: String
        get() = preferences.getString(PREF_NOTIFICATIONS_ENABLED, "") ?: ""
        set(value) = preferences.edit()
                .putString(PREF_NOTIFICATIONS_ENABLED, value)
                .apply()

    var darkModeEnabled: Boolean
        get() = preferences.getBoolean(PREF_DARK_MODE_ENABLED, false)
        set(value) = preferences.edit()
                .putBoolean(PREF_DARK_MODE_ENABLED, value)
                .apply()

    private fun loadString(prefKey: String) =
            preferences.getString(prefKey, "")

    private fun saveString(prefKey: String, value: String) =
            preferences.edit()
                    .putString(prefKey, value)
                    .apply()

    /** terms of service **/

    var termsOfServiceAccepted: Boolean
        set(didAccept) {
            if (!didAccept) return

            markTermsOfServiceVersion(email)
        }
        get() = isNewestTermsOfServiceAccepted()

    private var termsOfServiceVersionsAccepted: MutableMap<String, Int>
        get() {
            val jsonString = preferences.getString(PREF_TERMS_OF_SERVICE, null)
            return try {
                val type: Type = object : TypeToken<MutableMap<String, Int>>() {}.type
                jsonString?.let { it ->
                    Gson().fromJson<MutableMap<String, Int>>(it, type)
                } ?: mutableMapOf()
            } catch (t: Throwable) {
                mLogger.log(Level.SEVERE,
                        "termsOfServiceVersionAccepted: Assuming false in face of parse error jsonString=$jsonString")
                mutableMapOf()
            }
        }
        set(newValue) {
            val jsonString = Gson().toJson(newValue)
            preferences.edit()
                    .putString(PREF_TERMS_OF_SERVICE, jsonString)
                    .apply()
        }

    @VisibleForTesting
    fun markTermsOfServiceVersion(email: String, version: Int = TermsOfService.newestTermsOfServiceVersion) {
        termsOfServiceVersionsAccepted =
                termsOfServiceVersionsAccepted.also {
                    it[email] = version
                }
    }

    @VisibleForTesting
    fun isNewestTermsOfServiceAccepted(): Boolean {
        return termsOfServiceVersionsAccepted[email]?.let {
            it == TermsOfService.newestTermsOfServiceVersion
        } ?: false
    }

    /** encryption layer **/

    var IV: ByteArray
        get() = Base64.decode(loadString(PREF_IV), Base64.DEFAULT)
        set(value) = saveString(PREF_IV, Base64.encodeToString(value, Base64.DEFAULT))

    private fun encryptAndSaveString(prefKey: String, value: String) {
        val encrypted = keyStoreManager.encryptUserString(value, this.IV)
        saveString(prefKey, encrypted)
    }

    private fun loadAndDecryptString(prefKey: String): String {

        if (this.IV.isEmpty()) {
            logout()
            return ""
        }

        val encrypted = loadString(prefKey)
        return keyStoreManager.decryptUserString(encrypted ?: "", this.IV)
    }

    fun saveCredentials(email: String) {
        this.IV = KeyStoreManager.generateIV()
        this.email = email
        mLogger.log(Level.FINE,"Credentials Stored")
    }

    fun logout() {
        email = ""
        notificationsEnabled = ""
        darkModeEnabled = false
        preferences.edit()
                .remove(PREF_EMAIL)
                .remove(PREF_ACCESS_TOKEN)
                .remove(PREF_REFRESH_TOKEN)
                .remove(PREF_IV)
                .remove(PREF_NOTIFICATIONS_ENABLED)
                .remove(PREF_DARK_MODE_ENABLED)
                .apply()
    }
}