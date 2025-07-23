package com.example.drugstore_vnc.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.drugstore_vnc.privateCustomers.model.ResponseInforCustomer
import com.google.gson.Gson

class SharedPreferencesToken(private var context: Context) {
    private val PREF_NAME = "Language"
    private val KEYSHARE = "MyAppPreferences"
    private val KEYSHARE1 = "MyAppPreferencesImage"
    private val TOKEN = "StringKey"
    private val IMAGE = "ImageKey"
    private val CHECKPHARMACY = "BooleanKey"
    private val KEY_LANGUAGE = "username"
    fun saveToken(value: ResponseInforCustomer, pharmacy: String?) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(KEYSHARE, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val gson = Gson()
        val json: String = gson.toJson(value)
        editor.putString(TOKEN, json)
        editor.putBoolean(CHECKPHARMACY, false)
        if ((pharmacy?.length ?: 0) > 0)
            editor.putBoolean(CHECKPHARMACY, true)
        editor.apply()
    }

    fun getToken(): ResponseInforCustomer? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(KEYSHARE, Context.MODE_PRIVATE)
        val json: String? = sharedPreferences.getString(TOKEN, null)
        Log.e("gg", json.toString())
        return if (json != null) {
            val gson = Gson()
            gson.fromJson(json, ResponseInforCustomer::class.java)
        } else {
            null
        }
    }
    fun saveDataLanguage(context: Context, Language: String?) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(KEY_LANGUAGE)
        editor.putString(KEY_LANGUAGE, Language)
        editor.apply()
    }

    fun saveImageRank(imageString: String) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(KEYSHARE1, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(IMAGE, imageString)
        editor.apply()
    }
    fun getLanguage(): String? {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_LANGUAGE, "")
    }

    fun getImageRank(): String {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(KEYSHARE1, Context.MODE_PRIVATE)
        return sharedPreferences.getString(IMAGE, "").toString();
    }

    fun deleteToken() {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(KEYSHARE, Context.MODE_PRIVATE)
        val sharedPreferences1: SharedPreferences =
            context.getSharedPreferences(KEYSHARE1, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val editor1: SharedPreferences.Editor = sharedPreferences1.edit()
        editor.clear().apply()
        editor1.clear().apply()
    }

    fun getPharmacy(): Boolean {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(KEYSHARE, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(CHECKPHARMACY, false) ?: false
    }

}