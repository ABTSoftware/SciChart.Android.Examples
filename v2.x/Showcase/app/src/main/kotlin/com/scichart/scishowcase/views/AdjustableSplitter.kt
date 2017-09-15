//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// AdgustableGuideLine.kt is part of the SCICHART® Showcases. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® Showcases are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.scishowcase.views

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import com.scichart.scishowcase.R

class AdjustableSplitter : View {
    private var lastY: Int = 0

    var prevViewId: Int = -1
    var nextViewId: Int = -1

    private val prevView: View
        get() = (parent as View).findViewById(prevViewId)

    private val nextView: View
        get() = (parent as View).findViewById(nextViewId)

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        if (attrs != null) {
            val attrArray = context.obtainStyledAttributes(attrs, R.styleable.AdjustableSplitter)

            prevViewId = attrArray.getResourceId(R.styleable.AdjustableSplitter_prevViewId, 0)
            nextViewId = attrArray.getResourceId(R.styleable.AdjustableSplitter_nextViewId, 0)

            attrArray.recycle()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                lastY = event.rawY.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                val pixelsDragged = event.rawY.toInt() - lastY
                val weightPerPixel = (layoutParams as LinearLayout.LayoutParams).weight / height

                var lWeightDragged = weightPerPixel * pixelsDragged

                val allowedWeight = weightPerPixel * TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150f, resources.displayMetrics)
                lWeightDragged = prevView.allowedWeightChange(lWeightDragged, allowedWeight)
                lWeightDragged = -nextView.allowedWeightChange(-lWeightDragged, allowedWeight)

                prevView.addWeight(lWeightDragged)
                nextView.addWeight(-lWeightDragged)

                lastY = event.rawY.toInt()
            }
            MotionEvent.ACTION_UP -> {
                lastY = 0
            }
            else -> return false
        }
        this.requestLayout()
        return true
    }

    fun View.addWeight(weightToAdd: Float) {
        val lParams = this.layoutParams as LinearLayout.LayoutParams
        lParams.weight = lParams.weight + weightToAdd
        this.layoutParams = lParams
    }

    fun View.allowedWeightChange(supposedWeight: Float, allowedWeight: Float): Float {
        val lParams = this.layoutParams as LinearLayout.LayoutParams

        return if (lParams.weight + supposedWeight >= allowedWeight) supposedWeight else allowedWeight - lParams.weight
    }
}