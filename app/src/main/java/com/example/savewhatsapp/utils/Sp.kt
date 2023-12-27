package com.example.savewhatsapp.utils

import android.content.Context

class Sp(val context: Context) {
    val prefrences = context.getSharedPreferences("sp", Context.MODE_PRIVATE)

    fun setBusinessPath(path: String) {
        val editor = prefrences.edit()
        editor.putString("business", path)
        editor.apply()
    }

    fun getBusinessPath(): String {
        return prefrences.getString("business", "").toString()
    }

    fun setWhatsAppPath(path: String) {
        val editor = prefrences.edit()
        editor.putString("whatsapp", path)
        editor.apply()
    }

    fun getWhatsAppPath(): String {
        return prefrences.getString("whatsapp", "").toString()
    }
}