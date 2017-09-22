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
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import com.scichart.scishowcase.R

class AdjustableSplitter : View {
    val paint = Paint().apply {
        color = Color.GRAY
        strokeWidth = 5f
    }

    private var weightPerPixel: Float = 0f
    private var allowedHeight: Float = 0f

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

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas!!.drawLine(0f, canvas.height / 2f, canvas.width.toFloat(), canvas.height / 2f, paint)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        val parentView = parent as View
        parentView.post {
            weightPerPixel = (layoutParams as LinearLayout.LayoutParams).weight / height
            allowedHeight = (parent as View).height * 0.2f
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                lastY = event.rawY.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                var lWeightDragged = weightPerPixel * (event.rawY.toInt() - lastY)
                val allowedWeight = weightPerPixel * allowedHeight

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