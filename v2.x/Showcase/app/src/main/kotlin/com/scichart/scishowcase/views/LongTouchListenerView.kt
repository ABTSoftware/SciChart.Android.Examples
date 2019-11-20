//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// LongTouchListenerView.kt is part of the SCICHART® Showcases. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® Showcases are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.scishowcase.views

import android.content.Context
import android.graphics.PointF
import android.support.constraint.ConstraintLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.GestureDetectorCompat
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import io.reactivex.subjects.PublishSubject

class LongTouchListenerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : CoordinatorLayout(context, attrs, defStyleAttr) {
    val clickSubject = PublishSubject.create<PointF>()!!
    val longPressSubject = PublishSubject.create<PointF>()!!
    private val gestureDetector = GestureDetectorCompat(context, LongTouchListener(clickSubject, longPressSubject))

    var isLongTouchEnabled: Boolean = true

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)
        return true
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        val onInterceptTouchEvent = super.onInterceptTouchEvent(ev)
        val onTouchEvent = gestureDetector.onTouchEvent(ev)
        return onInterceptTouchEvent || onTouchEvent
    }

    inner class LongTouchListener(val clickSubject: PublishSubject<PointF>, val longPressSubject: PublishSubject<PointF>) : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            val onSingleTapConfirmed = super.onSingleTapConfirmed(e)

            if (e != null) {
                clickSubject.onNext(PointF(e.x, e.y))
            }
            return onSingleTapConfirmed
        }

        override fun onLongPress(e: MotionEvent?) {
            super.onLongPress(e)

            if (e != null && isLongTouchEnabled) {
                longPressSubject.onNext(PointF(e.x, e.y))
            }
        }
    }
}