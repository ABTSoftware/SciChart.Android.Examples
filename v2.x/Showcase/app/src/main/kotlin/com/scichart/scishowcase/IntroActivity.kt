package com.scichart.scishowcase

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.scichart.scishowcase.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {

    private var activityBinding: ActivityIntroBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_intro)

        activityBinding!!.button.setOnClickListener {
            // Maybe set first time launch
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}