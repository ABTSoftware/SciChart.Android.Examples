//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// StepProgressBar.kt is part of the SCICHART® Showcases. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.scishowcase.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.view.View
import com.scichart.drawing.utility.ColorUtil
import com.scichart.scishowcase.R
import com.scichart.scishowcase.utils.init

class StepProgressBar : View {
    private var _progressColor: Int = ColorUtil.Green
    private var _progressBackgroundColor: Int = ColorUtil.Transparent
    private var _progress: Int = 0
    private var _max: Int = 10
    private var _isVertical: Boolean = false
    private var _spacing: Float = 1f
    private var _barSize: Float = 10f

    private val paint = Paint().init { style = Paint.Style.FILL }

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs, defStyleAttr, defStyleRes)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0, defStyleRes: Int = 0) {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.StepProgressBar, defStyleAttr, defStyleRes)
        try {
            _progressColor = typedArray.getColor(R.styleable.StepProgressBar_progressColor, ColorUtil.Green)
            _progressBackgroundColor = typedArray.getColor(R.styleable.StepProgressBar_progressBackgroundColor, ColorUtil.Transparent)
            _progress = typedArray.getInt(R.styleable.StepProgressBar_progress, 0)
            _max = typedArray.getInt(R.styleable.StepProgressBar_max, 0)
            _isVertical = typedArray.getBoolean(R.styleable.StepProgressBar_isVertical, false)
            _spacing = typedArray.getDimension(R.styleable.StepProgressBar_spacing, 1f)
            _barSize = typedArray.getDimension(R.styleable.StepProgressBar_barSize, 10f)
        } finally {
            typedArray.recycle()
        }
    }

    var progressColor: Int
        get() = _progressColor
        set(value) {
            this._progressColor = value
            invalidate()
        }

    var progressBackgroundColor : Int
        get() = _progressBackgroundColor
        set(value) {
            this._progressBackgroundColor = value
            invalidate()
        }

    var progress : Int
        get() = _progress
        set(value) {
            this._progress = value
            invalidate()
        }

    var max : Int
        get() = _max
        set(value) {
            this._max = value
            requestLayout()
            invalidate()
        }

    var isVertical : Boolean
        get() = _isVertical
        set(value) {
            this._isVertical = value
            requestLayout()
            invalidate()
        }

    var spacing : Float
        get() = _spacing
        set(value) {
            this._spacing = value
            requestLayout()
            invalidate()
        }

    var barSize : Float
        get() = _barSize
        set(value) {
            this._barSize = value
            requestLayout()
            invalidate()
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width: Int
        val height: Int

        val requiredSize: Int = (_max * _barSize + _spacing * (_max - 1)).toInt()
        if (_isVertical) {
            width = View.resolveSizeAndState(suggestedMinimumHeight, widthMeasureSpec, 0)
            height = requiredSize
        } else {
            width = requiredSize
            height = View.resolveSizeAndState(suggestedMinimumWidth, heightMeasureSpec, 0)
        }

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) return

        val step = _barSize + _spacing

        if (_isVertical) {
            var position = canvas.height.toFloat()

            val width = canvas.width.toFloat()
            paint.color = _progressColor
            for (index in 0.._progress) {
                canvas.drawRect(0f, position - _barSize, width, position, paint)

                position -= step
            }

            paint.color = _progressBackgroundColor
            for (index in _progress + 1.._max - 1) {
                canvas.drawRect(0f, position - _barSize, width, position, paint)

                position -= step
            }

        } else {
            var position = 0f

            val height = canvas.height.toFloat()
            paint.color = _progressColor
            for (index in 0.._progress) {
                canvas.drawRect(position, 0f, position + _barSize, height, paint)

                position += step
            }

            paint.color = _progressBackgroundColor
            for (index in _progress + 1.._max - 1) {
                canvas.drawRect(position, 0f, position + _barSize, height, paint)

                position += step
            }
        }
    }
}
