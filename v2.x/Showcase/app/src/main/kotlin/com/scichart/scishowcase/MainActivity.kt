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

import android.Manifest
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.scichart.charting.themes.ThemeManager
import com.scichart.core.annotations.Visibility
import com.scichart.scishowcase.databinding.ActivityMainBinding
import com.scichart.scishowcase.utils.PermissionManager
import com.scichart.scishowcase.views.HomePageFragment

// The main activity for the application
class MainActivity : AppCompatActivity() {

    private var activityBinding: ActivityMainBinding? = null

    private val permissionManager = PermissionManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setUpSciChartLicense()
        requestShowcasePermissions()
        initAppBar()
        setUpThemes()

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomePageFragment()).commit()
        supportFragmentManager.addOnBackStackChangedListener {
            val isBackStackEmpty = supportFragmentManager.backStackEntryCount > 0
            supportActionBar!!.setDisplayHomeAsUpEnabled(isBackStackEmpty)
            activityBinding!!.logo.visibility = if (isBackStackEmpty) View.GONE else View.VISIBLE
        }
    }

    private fun setUpThemes() {
        ThemeManager.addTheme(this, R.style.SciChart_Bright_Spark)
    }

    private fun requestShowcasePermissions() {
        // permissions for Audio Analyzer
        permissionManager.requestPermission(Manifest.permission.RECORD_AUDIO)
        permissionManager.requestPermission(Manifest.permission.MODIFY_AUDIO_SETTINGS)

        // permissions for SciTrader
        permissionManager.requestPermission(Manifest.permission.INTERNET)
        permissionManager.requestPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    }

    private fun initAppBar() {
        setSupportActionBar(activityBinding!!.appToolbar)

        activityBinding!!.appToolbar.setNavigationOnClickListener({ onBackPressed() })
    }

    private fun setUpSciChartLicense() {
        // Set your license code here to license the SciChart Android Examples app
        // You can get a trial license key from https://www.scichart.com/licensing-scichart-android/
        // Purchased license keys can be viewed at https://www.scichart.com/profile
        //
        // e.g.
        //
        // com.scichart.charting.visuals.SciChartSurface.setRuntimeLicenseKey(
        //        "<LicenseContract>" +
        //                "<Customer>your-email@company.com</Customer>" +
        //                "<OrderId>Trial</OrderId>" +
        //                "<LicenseCount>1</LicenseCount>" +
        //                "<IsTrialLicense>true</IsTrialLicense>" +
        //                "<SupportExpires>12/21/2017 00:00:00</SupportExpires>" +
        //                "<ProductCode>SC-ANDROID-2D-ENTERPRISE-SRC</ProductCode>" +
        //                "<KeyCode>6ccc960b22b7b12360a141a7c2a89bce4e40.....09744b6c195022e9fa1ebcf9a0e78167cbaa8f9b8eee9221</KeyCode>" +
        //        "</LicenseContract>"
        // );

        try {
            com.scichart.charting.visuals.SciChartSurface.setRuntimeLicenseKey("")
        } catch (e: Exception) {
            Log.e("SciChart", "Error when setting the license", e)
        }
    }
}