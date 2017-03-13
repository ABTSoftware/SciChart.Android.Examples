//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// MainActivity.kt is part of the SCICHART® Showcases. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.scishowcase

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.scichart.scishowcase.databinding.ActivityMainBinding
import com.scichart.scishowcase.views.HomePageFragment

// The main activity for the application
class MainActivity : AppCompatActivity() {

    private var activityBinding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initAppBar()

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomePageFragment()).commit()
        supportFragmentManager.addOnBackStackChangedListener {
            when {
                supportFragmentManager.backStackEntryCount > 0 -> supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                else -> supportActionBar!!.setDisplayHomeAsUpEnabled(false)
            }
        }
    }

    private fun initAppBar() {
        setSupportActionBar(activityBinding!!.appToolbar)

        activityBinding!!.appToolbar.setNavigationOnClickListener({ onBackPressed() })
    }
}