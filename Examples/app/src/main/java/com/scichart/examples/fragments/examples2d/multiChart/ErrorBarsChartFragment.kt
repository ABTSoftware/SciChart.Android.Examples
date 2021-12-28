//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ErrorBarsChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.multiChart.kt

import android.widget.SeekBar
import com.scichart.charting.model.dataSeries.HlDataSeries
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.renderableSeries.ErrorDirection.Vertical
import com.scichart.charting.visuals.renderableSeries.ErrorType.Absolute
import com.scichart.charting.visuals.renderableSeries.FastErrorBarsRenderableSeries
import com.scichart.examples.R
import com.scichart.examples.data.DataManager
import com.scichart.examples.data.DoubleSeries
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.SeekBarChangeListenerBase
import com.scichart.examples.utils.ViewSettingsUtil
import com.scichart.examples.utils.interpolator.ElasticOutInterpolator
import com.scichart.examples.utils.scichartExtensions.*
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget
import com.scichart.examples.utils.widgetgeneration.Widget
import java.util.*

class ErrorBarsChartFragment : ExampleSingleChartBaseFragment() {
    override fun showDefaultModifiersInToolbar(): Boolean = false

    override fun getToolbarItems(): List<Widget> = ArrayList<Widget>().apply {
        add(ImageViewWidget.Builder().setId(R.drawable.example_toolbar_settings).setListener { openSettingsDialog() }.build())
    }

    override fun initExample(surface: SciChartSurface) {
        val data = DataManager.getInstance().getFourierSeries(1.0, 0.1, 5.0, 5.15, 5000)
        val dataSeries0 = HlDataSeries<Double, Double>().apply { fillSeries(this, data,1.0) }
        val dataSeries1 = HlDataSeries<Double, Double>().apply { fillSeries(this, data,1.3) }

        val color = 0xFFC6E6FF

        surface.suspendUpdates {
            xAxes { numericAxis() }
            yAxes { numericAxis() }
            renderableSeries {
                fastErrorBarsRenderableSeries {
                    dataSeries = dataSeries0
                    strokeStyle = SolidPenStyle(color)
                    errorDirection = Vertical
                    errorType = Absolute

                    scaleAnimation { interpolator = ElasticOutInterpolator() }
                }
                fastLineRenderableSeries {
                    dataSeries = dataSeries0
                    strokeStyle = SolidPenStyle(color)
                    ellipsePointMarker {
                        setSize(5)
                        fillStyle = SolidBrushStyle(color)
                    }

                    scaleAnimation { interpolator = ElasticOutInterpolator() }
                }

                fastErrorBarsRenderableSeries {
                    dataSeries = dataSeries1
                    strokeStyle = SolidPenStyle(color)
                    errorDirection = Vertical
                    errorType = Absolute

                    scaleAnimation { interpolator = ElasticOutInterpolator() }
                }
                xyScatterRenderableSeries {
                    dataSeries = dataSeries1
                    ellipsePointMarker {
                        setSize(7)
                        fillStyle = SolidBrushStyle(0x00FFFFFF)
                        strokeStyle = SolidPenStyle(color)
                    }

                    scaleAnimation { interpolator = ElasticOutInterpolator() }
                }
            }

            chartModifiers { defaultModifiers() }
        }
    }

    private fun fillSeries(dataSeries: HlDataSeries<Double, Double>, sourceData: DoubleSeries, scale: Double) {
        val xValues = sourceData.xValues
        val yValues = sourceData.yValues

        val random = Random()
        for (i in 0 until xValues.size()) {
            val y = yValues[i] * scale
            dataSeries.append(xValues[i], y, random.nextDouble() * 0.2, random.nextDouble() * 0.2)
        }
    }

    private fun openSettingsDialog() {
        val dialog = ViewSettingsUtil.createSettingsPopup(activity, R.layout.example_error_bars_chart_popop_layout)

        ViewSettingsUtil.setUpSeekBar(dialog, R.id.data_point_width_seek_bar, 50, object : SeekBarChangeListenerBase() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                onDataPointWidthChanged(progress)
            }
        })
        ViewSettingsUtil.setUpSeekBar(dialog, R.id.stroke_thickness_seek_bar, 1, object : SeekBarChangeListenerBase() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                onStrokeThicknessChanged(progress + 1)
            }
        })

        dialog.show()
    }

    private fun onDataPointWidthChanged(dataPointWidth: Int) {
        binding.surface.suspendUpdates {
            (renderableSeries[2] as FastErrorBarsRenderableSeries).dataPointWidth = (dataPointWidth / 100f).toDouble()
            (renderableSeries[3] as FastErrorBarsRenderableSeries).dataPointWidth = (dataPointWidth / 100f).toDouble()
        }
    }

    private fun onStrokeThicknessChanged(strokeThickness: Int) {
        binding.surface.suspendUpdates {
            for (i in renderableSeries.indices) {
                renderableSeries[i].run {
                    val currentStyle = strokeStyle
                    strokeStyle = SolidPenStyle(currentStyle.color, strokeThickness.toFloat(), currentStyle.antiAliasing)
                }
            }
        }
    }
}