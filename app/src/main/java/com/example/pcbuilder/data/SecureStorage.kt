package com.example.pcbuilder.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

object SecureStorage {

    private fun getEncryptedSharedPreferences(context: Context) =
        EncryptedSharedPreferences.create(
            "secret_credentials",
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    
    fun saveCredentials(context: Context, email: String, pass: String) {
        val prefs = getEncryptedSharedPreferences(context)
        with(prefs.edit()) {
            putString("EMAIL_KEY", email)
            putString("PASS_KEY", pass)
            apply()
        }
    }

    fun getCredentials(context: Context): Pair<String, String>? {
        val prefs = getEncryptedSharedPreferences(context)
        val email = prefs.getString("EMAIL_KEY", null)
        val pass = prefs.getString("PASS_KEY", null)
        
        return if (email != null && pass != null) {
            Pair(email, pass)
        } else {
            null
        }
    }
    
    fun hasCredentials(context: Context): Boolean {
        return getCredentials(context) != null
    }
}