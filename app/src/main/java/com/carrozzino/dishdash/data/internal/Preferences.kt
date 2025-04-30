package com.carrozzino.dishdash.data.internal

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

class Preferences @Inject constructor(
    private val shared : SharedPreferences
) {
    companion object {
        const val TAG : String = "Preferences"
    }

    fun getFirebaseToken() : String {
        return shared.getString("token_firebase_key", "") ?: ""
    }

    fun putFirebaseToken(token : String) {
        shared.edit { putString("token_firebase_key", token) }
    }

    fun isLogged() : Boolean {
        return shared.getBoolean("is_logged", false)
    }

    fun setLogged(isLogged : Boolean) {
        shared.edit { putBoolean("is_logged", isLogged) }
    }

    fun getString(key : String) : String {
        return shared.getString(key, "") ?: ""
    }

    fun putString(value : String, key : String) {
        shared.edit { putString(key, value) }
    }

    fun getBoolean(key : String) : Boolean {
        return shared.getBoolean(key, false)
    }

    fun putBoolean(value : Boolean, key : String) {
        shared.edit { putBoolean(key, value) }
    }
}