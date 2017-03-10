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