//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2026. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// BrushAnnotationFragment.kt is part of SCICHART®, High Performance Scientific Charts
// For full terms and conditions of the license, see http://www.scichart.com/scichart-eula/
//
// This source code is protected by international copyright law. Unauthorized
// reproduction, reverse-engineering, or distribution of all or any portion of
// this source code is strictly prohibited.
//
// This source code contains confidential and proprietary trade secrets of
// SciChart Ltd., and should at no time be copied, transferred, sold,
// distributed or made available without express written permission.
//******************************************************************************

package com.scichart.examples.fragments.examples2d.chartAnnotations.kt

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import com.scichart.charting.modifiers.FreehandDrawingModifier
import com.scichart.charting.modifiers.OnAnnotationCreatedListener
import com.scichart.charting.modifiers.PinchZoomModifier
import com.scichart.charting.modifiers.ZoomExtentsModifier
import com.scichart.charting.modifiers.ZoomPanModifier
import com.scichart.charting.visuals.annotations.IAnnotation
import com.scichart.data.model.DoubleRange
import com.scichart.examples.data.MarketDataService
import com.scichart.examples.databinding.ExampleFreehandAnnotationFragmentBinding
import com.scichart.examples.fragments.base.ExampleBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import java.util.*

class FreehandDrawingAnnotationFragment : ExampleBaseFragment<ExampleFreehandAnnotationFragmentBinding>(), OnAnnotationCreatedListener {

    private val freehandDrawingModifier = FreehandDrawingModifier()
    private val zoomPanModifier = ZoomPanModifier()
    private val pinchZoomModifier = PinchZoomModifier()
    private val zoomExtentsModifier = ZoomExtentsModifier()

    private var isDrawingMode = true

    override fun inflateBinding(inflater: LayoutInflater): ExampleFreehandAnnotationFragmentBinding {
        return ExampleFreehandAnnotationFragmentBinding.inflate(inflater)
    }

    override fun initExample(binding: ExampleFreehandAnnotationFragmentBinding) {
        // Drawing modifier starts enabled
        freehandDrawingModifier.setAnnotationCreationListener(this)
        freehandDrawingModifier.isEnabled = true

        binding.thicknessSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                freehandDrawingModifier.brushThickness = progress.toFloat()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.colorWhite.setOnClickListener { selectColor(it, Color.WHITE) }
        binding.colorYellow.setOnClickListener { selectColor(it, Color.YELLOW) }
        binding.colorGreen.setOnClickListener { selectColor(it, Color.GREEN) }
        binding.colorBlue.setOnClickListener { selectColor(it, Color.BLUE) }

        // Set initial selection
        selectColor(binding.colorWhite, Color.WHITE)

        binding.drawingModeSwitch.setOnCheckedChangeListener { _, isChecked -> setDrawingMode(isChecked, binding) }

        val surface = binding.surface
        surface.suspendUpdates {
            xAxes { categoryDateAxis { } }
            yAxes { numericAxis { visibleRange = DoubleRange(30.0, 37.0) } }
            renderableSeries {
                fastCandlestickRenderableSeries {
                    ohlcDataSeries<Date, Double> {
                        val marketDataService = MarketDataService(Calendar.getInstance().time, 5, 5)
                        val data = marketDataService.getHistoricalData(200)

                        append(data.dateData, data.openData, data.highData, data.lowData, data.closeData)
                    }
                    opacity = 0.4f
                }
            }
            chartModifiers {
                modifier(freehandDrawingModifier)
                modifier(zoomPanModifier.apply { isEnabled = !isDrawingMode })
                modifier(pinchZoomModifier.apply { isEnabled = !isDrawingMode })
                modifier(zoomExtentsModifier.apply { isEnabled = !isDrawingMode })
            }
        }
        binding.deleteAnnotation.setOnClickListener {
            val annotations = surface.annotations
            for (i in annotations.indices.reversed()) {
                val annotation = annotations[i]
                if (annotation.isSelected) {
                    annotations.removeAt(i)
                }
            }
        }
    }

    override fun onAnnotationCreated(newAnnotation: IAnnotation) {
        newAnnotation.setIsEditable(!isDrawingMode)
    }

    private fun selectColor(view: View, color: Int) {
        binding.colorWhite.background = getColorDrawable(Color.WHITE, view === binding.colorWhite)
        binding.colorYellow.background = getColorDrawable(Color.YELLOW, view === binding.colorYellow)
        binding.colorGreen.background = getColorDrawable(Color.GREEN, view === binding.colorGreen)
        binding.colorBlue.background = getColorDrawable(Color.BLUE, view === binding.colorBlue)

        freehandDrawingModifier.brushColor = color
    }

    private fun getColorDrawable(color: Int, isSelected: Boolean): Drawable {
        return GradientDrawable().apply {
            setColor(color)
            shape = GradientDrawable.RECTANGLE
            if (isSelected) {
                setStroke(4, Color.MAGENTA)
            }
        }
    }

    /**
     * Toggles between Drawing Mode and Selection Mode.
     * Drawing Mode ON: drawing modifier is active, all existing annotations become non-editable/non-selectable.
     * Drawing Mode OFF: drawing modifier is disabled, all existing annotations become editable/selectable.
     */
    private fun setDrawingMode(drawingMode: Boolean, binding: ExampleFreehandAnnotationFragmentBinding) {
        isDrawingMode = drawingMode
        freehandDrawingModifier.setIsEnabled(drawingMode)

        zoomPanModifier.isEnabled = !drawingMode
        pinchZoomModifier.isEnabled = !drawingMode
        zoomExtentsModifier.isEnabled = !drawingMode

        val annotations = binding.surface.annotations
        for (i in 0 until annotations.size) {
            annotations[i].setIsEditable(!drawingMode)
        }
    }
}
