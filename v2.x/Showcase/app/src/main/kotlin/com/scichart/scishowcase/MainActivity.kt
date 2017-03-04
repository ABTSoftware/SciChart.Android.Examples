package com.scichart.scishowcase

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.scichart.scishowcase.databinding.ActivityMainBinding
import com.scichart.scishowcase.views.HomePageFragment

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