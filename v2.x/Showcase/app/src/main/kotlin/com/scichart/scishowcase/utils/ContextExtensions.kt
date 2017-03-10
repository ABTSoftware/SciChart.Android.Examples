package com.scichart.scishowcase.utils

import android.content.Context
import android.util.TypedValue

/**
 * Gets the size in DIP
 */
fun Context.dip(value: Float) : Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, this.resources.displayMetrics)
}
