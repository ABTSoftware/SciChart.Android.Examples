//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// IntroActivity.kt is part of the SCICHART® Showcases. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.scishowcase

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.scichart.scishowcase.databinding.ActivityIntroBinding
import com.scichart.scishowcase.utils.PrefManager

class IntroActivity : AppCompatActivity() {

    private var activityBinding: ActivityIntroBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferenceManager = PrefManager(this)
        if (!preferenceManager.showAppIntro) {
            startHomePage()
        }

        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_intro)

        activityBinding!!.button.setOnClickListener {
            // Maybe set first time launch
            preferenceManager.showAppIntro = !activityBinding!!.checkbox.isChecked
            startHomePage()
        }
    }

    private fun startHomePage() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}