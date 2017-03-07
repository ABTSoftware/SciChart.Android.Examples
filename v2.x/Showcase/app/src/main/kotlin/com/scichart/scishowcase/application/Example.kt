//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// Example.kt is part of the SCICHART® Showcases. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.scishowcase.application

import android.support.annotation.DrawableRes
import kotlin.reflect.KClass

data class Example(val exampleType: KClass<*>) {
    val title: String
    val description: String
    @DrawableRes val icon: Int

    init {
        val annotation = (exampleType.annotations.find { it is ExampleDefinition } as ExampleDefinition)

        title = annotation.title
        description = annotation.description
        icon = annotation.icon
    }
}