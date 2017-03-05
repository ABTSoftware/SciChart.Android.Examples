package com.scichart.scishowcase.application

import android.support.annotation.DrawableRes
import com.scichart.scishowcase.R

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
internal annotation class ExampleDefinition(val title: String, val description: String = "Example description should be here", @DrawableRes val icon: Int = R.drawable.ic_realtime)
