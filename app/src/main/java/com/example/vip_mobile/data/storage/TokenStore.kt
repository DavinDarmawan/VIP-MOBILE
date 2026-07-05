package com.example.vip_mobile.data.storage

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object TokenStore {
    private const val PREFS = "ibs_core_secure_prefs"
    private const val KEY_TOKEN = "access_token"

    private fun getEncryptedPrefs(context: Context) = EncryptedSharedPreferences.create(
        context,
        PREFS,
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveToken(context: Context, token: String) {
        getEncryptedPrefs(context).edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(context: Context): String? {
        return getEncryptedPrefs(context).getString(KEY_TOKEN, null)
    }

    fun clear(context: Context) {
        getEncryptedPrefs(context).edit().remove(KEY_TOKEN).apply()
    }
}