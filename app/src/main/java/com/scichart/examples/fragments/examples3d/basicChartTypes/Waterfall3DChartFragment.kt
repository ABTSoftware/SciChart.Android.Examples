//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// Waterfall3DChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples3d.basicChartTypes.kt

import android.view.View
import android.widget.AdapterView
import android.widget.CompoundButton
import com.scichart.charting.visuals.axes.AutoRange.Always
import com.scichart.charting3d.model.dataSeries.waterfall.WaterfallDataSeries3D
import com.scichart.charting3d.visuals.SciChartSurface3D
import com.scichart.charting3d.visuals.pointMarkers.SpherePointMarker3D
import com.scichart.charting3d.visuals.renderableSeries.data.GradientColorPalette
import com.scichart.charting3d.visuals.renderableSeries.data.SolidColorBrushPalette
import com.scichart.charting3d.visuals.renderableSeries.waterfall.WaterfallRenderableSeries3D
import com.scichart.drawing.utility.ColorUtil.*
import com.scichart.examples.R
import com.scichart.examples.data.DataManager
import com.scichart.examples.data.Radix2FFT
import com.scichart.examples.fragments.base.ExampleSingleChart3DBaseFragment
import com.scichart.examples.utils.ItemSelectedListenerBase
import com.scichart.examples.utils.ViewSettingsUtil
import com.scichart.examples.utils.scichartExtensions.*
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget
import com.scichart.examples.utils.widgetgeneration.Widget
import java.util.*
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class Waterfall3DChartFragment : ExampleSingleChart3DBaseFragment() {
    private val gradientFillColorPalette = GradientColorPalette(
        intArrayOf(Red, Orange, Yellow, GreenYellow, DarkGreen),
        floatArrayOf(0f, .25f, .5f, .75f, 1f)
    )

    private val gradientStrokeColorPalette = GradientColorPalette(
        intArrayOf(Crimson, DarkOrange, LimeGreen, LimeGreen),
        floatArrayOf(0f, .33f, .67f, 1f)
    )

    private val transparentColorPalette = SolidColorBrushPalette(Transparent)
    private val solidStrokeColorPalette = SolidColorBrushPalette(LimeGreen)
    private val solidFillColorPalette = SolidColorBrushPalette(0xAA00BFFF.toInt())

    private var currentFillColorPalette = 0 // by default YAxis
    private var currentStrokeColorPalette = 0 // by default YAxis

    private lateinit var rSeries: WaterfallRenderableSeries3D

    override fun initExample(surface3d: SciChartSurface3D) {
        surface3d.suspendUpdates {
            xAxis = numericAxis3D()
            yAxis = numericAxis3D()
            zAxis = numericAxis3D { autoRange = Always }

            renderableSeries {
                waterfallRenderableSeries3D {
                    waterfallDataSeries3D<Double, Double, Double>(POINTS_PER_SLICE, SLICE_COUNT) {
                        startX = 10.0
                        stepX = 1.0
                        startZ = 1.0
                        fillDataSeries(this)
                    }
                    strokeThickness = 1f
                    sliceThickness = 0f
                    yColorMapping = gradientFillColorPalette
                    yStrokeColorMapping = gradientStrokeColorPalette
                    opacity = 0.8f

                    rSeries = this
                }
            }

            chartModifiers {
                pinchZoomModifier3D()
                orbitModifier3D { receiveHandledEvents = true }
                zoomExtentsModifier3D()
                vertexSelectionModifier3D { receiveHandledEvents = true }
            }
        }
    }

    private fun fillDataSeries(ds: WaterfallDataSeries3D<Double, Double, Double>) {
        val dataManager = DataManager.getInstance()
        val count = POINTS_PER_SLICE * 2

        val re = DoubleArray(count)
        val im = DoubleArray(count)
        for (sliceIndex in 0 until SLICE_COUNT) {
            for (i in 0 until count) {
                re[i] = 2.0 * sin(Math.PI * i / 10) +
                        5.0 * sin(Math.PI * i / 5) +
                        2.0 * dataManager.randomDouble
                im[i] = -10.0
            }

            transform.run(re, im)

            val scaleCoef = 1.5.pow(sliceIndex * 0.3) / 1.5.pow(SLICE_COUNT * 0.3)

            for (pointIndex in 0 until POINTS_PER_SLICE) {
                val reValue = re[pointIndex]
                val imValue = im[pointIndex]
                val mag = sqrt(reValue * reValue + imValue * imValue)

                var yVal = (random.nextInt(10) + 10) * log10(mag / POINTS_PER_SLICE)
                yVal = if (yVal < -25 || yVal > -5)
                    (if (yVal < -25) -25 else random.nextInt(9) - 6).toDouble()
                else yVal

                ds.updateYAt(pointIndex, sliceIndex, -yVal * scaleCoef)
            }
        }
    }

    override fun getToolbarItems(): List<Widget> = ArrayList<Widget>().apply {
        add(ImageViewWidget.Builder().setId(R.drawable.example_toolbar_settings).setListener { openSettingsDialog() }.build())
    }

    private fun openSettingsDialog() {
        val dialog = ViewSettingsUtil.createSettingsPopup(activity, R.layout.example_waterfall_3d_popup_layout)

        ViewSettingsUtil.setUpSpinner(dialog, R.id.strokePaletteSelector, R.array.stroke_color_palette_list, currentStrokeColorPalette, object : ItemSelectedListenerBase() {
                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                    when (position) {
                        0 -> {
                            currentStrokeColorPalette = 0
                            rSeries.yStrokeColorMapping = gradientStrokeColorPalette
                            rSeries.zStrokeColorMapping = null
                        }
                        1 -> {
                            currentStrokeColorPalette = 1
                            rSeries.yStrokeColorMapping = null
                            rSeries.zStrokeColorMapping = gradientStrokeColorPalette
                        }
                        2 -> {
                            currentStrokeColorPalette = 2
                            rSeries.yStrokeColorMapping = solidStrokeColorPalette
                            rSeries.zStrokeColorMapping = solidStrokeColorPalette
                        }
                        3 -> {
                            currentStrokeColorPalette = 3
                            rSeries.yStrokeColorMapping = transparentColorPalette
                            rSeries.zStrokeColorMapping = transparentColorPalette
                        }
                    }
                }
            })

        ViewSettingsUtil.setUpSpinner(dialog, R.id.fillPaletteSelector, R.array.fill_color_palette_list, currentFillColorPalette,
            object : ItemSelectedListenerBase() {
                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                    when (position) {
                        0 -> {
                            currentFillColorPalette = 0
                            rSeries.yColorMapping = gradientFillColorPalette
                            rSeries.zColorMapping = null
                        }
                        1 -> {
                            currentFillColorPalette = 1
                            rSeries.yColorMapping = null
                            rSeries.zColorMapping = gradientFillColorPalette
                        }
                        2 -> {
                            currentFillColorPalette = 2
                            rSeries.yColorMapping = solidFillColorPalette
                            rSeries.zColorMapping = solidFillColorPalette
                        }
                        3 -> {
                            currentFillColorPalette = 3
                            rSeries.yColorMapping = transparentColorPalette
                            rSeries.zColorMapping = transparentColorPalette
                        }
                    }
                }
            })

        ViewSettingsUtil.setUpCheckBox(dialog, R.id.showPointMarkers, rSeries.pointMarker != null) { _: CompoundButton?, isChecked: Boolean ->
            rSeries.pointMarker = if (isChecked) SpherePointMarker3D().apply { fill = Blue; size = 5f } else null
        }

        ViewSettingsUtil.setUpCheckBox(dialog, R.id.isVolumetric, rSeries.sliceThickness > 0) { _: CompoundButton?, isChecked: Boolean ->
            rSeries.sliceThickness = if (isChecked) 10f else 0f
        }

        dialog.show()
    }

    companion object {
        private const val POINTS_PER_SLICE = 128
        private const val SLICE_COUNT = 20

        private val random = Random()
        private val transform = Radix2FFT(POINTS_PER_SLICE)
    }
}