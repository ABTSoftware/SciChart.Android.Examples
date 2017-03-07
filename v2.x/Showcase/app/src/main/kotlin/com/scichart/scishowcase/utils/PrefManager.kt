//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// PrefManager.kt is part of the SCICHART® Showcases. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

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