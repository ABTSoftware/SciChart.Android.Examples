package com.scichart.scishowcase

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.scichart.scishowcase.databinding.ActivityIntroBinding
import com.scichart.scishowcase.utils.PrefManager

class IntroActivity : AppCompatActivity() {

    private var activityBinding: ActivityIntroBinding? = null
    private var preferenceManager: PrefManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferenceManager = PrefManager(this)
        val doNotShowAgain = preferenceManager!!.isDoNotShowAgain()
        if (doNotShowAgain) {
            startHomePage()
        }

        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_intro)

        activityBinding!!.button.setOnClickListener {
            // Maybe set first time launch
            val doNotShowAgain = activityBinding!!.checkbox.isChecked
            preferenceManager!!.setDoNotShowAgain(doNotShowAgain)
            startHomePage()
        }
    }

    private fun startHomePage() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}