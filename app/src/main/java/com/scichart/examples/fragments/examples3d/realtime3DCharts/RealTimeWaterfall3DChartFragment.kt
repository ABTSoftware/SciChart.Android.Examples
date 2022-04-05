//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// RealTimeWaterfall3DChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples3d.realtime3DCharts.kt

import android.view.View
import android.widget.AdapterView
import android.widget.CompoundButton
import com.scichart.charting3d.model.dataSeries.waterfall.WaterfallDataSeries3D
import com.scichart.charting3d.visuals.SciChartSurface3D
import com.scichart.charting3d.visuals.pointMarkers.SpherePointMarker3D
import com.scichart.charting3d.visuals.renderableSeries.data.GradientColorPalette
import com.scichart.charting3d.visuals.renderableSeries.data.SolidColorBrushPalette
import com.scichart.charting3d.visuals.renderableSeries.waterfall.WaterfallRenderableSeries3D
import com.scichart.drawing.utility.ColorUtil.*
import com.scichart.examples.R
import com.scichart.examples.data.DataManager
import com.scichart.examples.fragments.base.ExampleSingleChart3DBaseFragment
import com.scichart.examples.utils.ItemSelectedListenerBase
import com.scichart.examples.utils.ViewSettingsUtil
import com.scichart.examples.utils.scichartExtensions.*
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget
import com.scichart.examples.utils.widgetgeneration.Widget
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class RealTimeWaterfall3DChartFragment : ExampleSingleChart3DBaseFragment() {
    private var currentFillColorPalette = 0 // by default YAxis
    private var currentStrokeColorPalette = 0 // by default YAxis
    private var tick = 0

    private lateinit var waterfallDataSeries: WaterfallDataSeries3D<Double, Double, Double>
    private val scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private var schedule: ScheduledFuture<*>? = null

    override fun initExample(surface3d: SciChartSurface3D) {
        val dataManager = DataManager.getInstance()
        val fftValues = dataManager.loadFFT(requireContext())

        surface3d.suspendUpdates {
            xAxis = numericAxis3D()
            yAxis = numericAxis3D()
            zAxis = numericAxis3D()

            renderableSeries {
                waterfallRenderableSeries3D {
                    waterfallDataSeries3D<Double, Double, Double>(POINTS_PER_SLICE, SLICE_COUNT) {
                        startX = 10.0
                        stepX = 1.0
                        startZ = 25.0
                        stepZ = 10.0

                        pushRow(fftValues[0])
                        waterfallDataSeries = this
                    }
                    strokeThickness = 1f
                    sliceThickness = 5f
                    yColorMapping = gradientFillColorPalette
                    yStrokeColorMapping = gradientStrokeColorPalette
                }
            }

            chartModifiers { defaultModifiers3D() }

            worldDimensions.assign(200f, 100f, 200f)
        }

        schedule = scheduledExecutorService.scheduleWithFixedDelay({
            surface3d.suspendUpdates {
                val index = tick++ % fftValues.size
                waterfallDataSeries.pushRow(fftValues[index])
            }
        }, 0, 25, TimeUnit.MILLISECONDS)
    }

    override fun getToolbarItems(): List<Widget> = ArrayList<Widget>().apply {
        add(ImageViewWidget.Builder().setId(R.drawable.example_toolbar_settings).setListener { openSettingsDialog() }.build())
    }

    private fun openSettingsDialog() {
        val surface3d = binding.surface3d
        val rs = surface3d.renderableSeries[0] as WaterfallRenderableSeries3D

        val dialog = ViewSettingsUtil.createSettingsPopup(activity, R.layout.example_waterfall_3d_popup_layout)

        ViewSettingsUtil.setUpSpinner(dialog,
            R.id.strokePaletteSelector,
            R.array.stroke_color_palette_list,
            currentStrokeColorPalette,
            object : ItemSelectedListenerBase() {
                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                    when (position) {
                        0 -> {
                            currentStrokeColorPalette = 0
                            rs.yStrokeColorMapping = gradientStrokeColorPalette
                            rs.zStrokeColorMapping = null
                        }
                        1 -> {
                            currentStrokeColorPalette = 1
                            rs.yStrokeColorMapping = null
                            rs.zStrokeColorMapping = gradientStrokeColorPalette
                        }
                        2 -> {
                            currentStrokeColorPalette = 2
                            rs.yStrokeColorMapping = solidStrokeColorPalette
                            rs.zStrokeColorMapping = solidStrokeColorPalette
                        }
                        3 -> {
                            currentStrokeColorPalette = 3
                            rs.yStrokeColorMapping = transparentColorPalette
                            rs.zStrokeColorMapping = transparentColorPalette
                        }
                    }
                }
            })

        ViewSettingsUtil.setUpSpinner(dialog,
            R.id.fillPaletteSelector,
            R.array.fill_color_palette_list,
            currentFillColorPalette,
            object : ItemSelectedListenerBase() {
                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                    when (position) {
                        0 -> {
                            currentFillColorPalette = 0
                            rs.yColorMapping = gradientStrokeColorPalette
                            rs.zColorMapping = null
                        }
                        1 -> {
                            currentFillColorPalette = 1
                            rs.yColorMapping = null
                            rs.zColorMapping = gradientStrokeColorPalette
                        }
                        2 -> {
                            currentFillColorPalette = 2
                            rs.yColorMapping = solidFillColorPalette
                            rs.zColorMapping = solidFillColorPalette
                        }
                        3 -> {
                            currentFillColorPalette = 3
                            rs.yColorMapping = transparentColorPalette
                            rs.zColorMapping = transparentColorPalette
                        }
                    }
                }
            })

        ViewSettingsUtil.setUpCheckBox(dialog, R.id.showPointMarkers, rs.pointMarker != null ) { _: CompoundButton?, isChecked: Boolean ->
            rs.pointMarker = if (isChecked) SpherePointMarker3D().apply { fill = Blue; size = 5f } else null
        }

        ViewSettingsUtil.setUpCheckBox(dialog, R.id.isVolumetric, rs.sliceThickness > 0 ) { _: CompoundButton?, isChecked: Boolean ->
            rs.sliceThickness = if (isChecked) 5f else 0f
        }

        dialog.show()
    }

    companion object {
        private const val POINTS_PER_SLICE = 128
        private const val SLICE_COUNT = 10

        private val transparentColorPalette = SolidColorBrushPalette(Transparent)
        private val solidStrokeColorPalette = SolidColorBrushPalette(LimeGreen)
        private val solidFillColorPalette = SolidColorBrushPalette(0xAA00BFFF.toInt())

        private val gradientFillColorPalette = GradientColorPalette(
            intArrayOf(Red, Orange, Yellow, GreenYellow, DarkGreen),
            floatArrayOf(0f, .4f, .5f, .6f, 1f)
        )

        private val gradientStrokeColorPalette = GradientColorPalette(
            intArrayOf(Crimson, DarkOrange, LimeGreen, LimeGreen),
            floatArrayOf(0f, .33f, .67f, 1f)
        )
    }
}