package com.example.newapp

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor

private const val KEY_USER_INFO = "userInfo"
private const val TOKEN = "token"
class SessionManager(private val sharedPreferences: SharedPreferences) {


    fun isLoggedIn(): Boolean {
        return sharedPreferences.getString(KEY_USER_INFO, null) != null
    }

    fun logout() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    fun userInfo(userInfo: String?) {
        sharedPreferences.edit().putString(KEY_USER_INFO, userInfo).apply()
    }

    fun getUserInfo() : String? {
        return sharedPreferences.getString(KEY_USER_INFO, null)
    }

    fun saveToken(token: String?) {
        sharedPreferences.edit().putString(TOKEN, token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(TOKEN, null)
    }



}