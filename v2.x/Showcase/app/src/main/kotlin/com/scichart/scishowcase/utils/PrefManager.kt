package com.scichart.scishowcase.utils

import android.content.Context
import android.content.SharedPreferences

class PrefManager(val context: Context) {

    private val PRIVATE_MODE = 0
    private val PREF_NAME = "sciShowcasePreferences"
    private val DO_NOT_SHOW_THIS_AGAIN = "DoNotShowThisAgain"

    private var preferences: SharedPreferences? = null

    init {
        preferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
    }

    fun setDoNotShowAgain(doNotShowAgain: Boolean) {
        val preferencesEditor = preferences!!.edit()
        preferencesEditor.putBoolean(DO_NOT_SHOW_THIS_AGAIN, doNotShowAgain)
        preferencesEditor.apply()
    }

    fun isDoNotShowAgain(): Boolean {
        return preferences!!.getBoolean(DO_NOT_SHOW_THIS_AGAIN, false)
    }
}