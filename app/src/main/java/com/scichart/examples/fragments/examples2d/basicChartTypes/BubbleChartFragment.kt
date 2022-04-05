//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// BubbleChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.basicChartTypes.kt

import android.widget.SeekBar
import com.scichart.charting.modifiers.RubberBandXyZoomModifier
import com.scichart.charting.modifiers.ZoomExtentsModifier
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.renderableSeries.FastBubbleRenderableSeries
import com.scichart.data.model.DoubleRange
import com.scichart.examples.R
import com.scichart.examples.data.DataManager
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.*
import com.scichart.examples.utils.interpolator.ElasticOutInterpolator
import com.scichart.examples.utils.scichartExtensions.*
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget
import com.scichart.examples.utils.widgetgeneration.Widget
import java.util.*

class BubbleChartFragment : ExampleSingleChartBaseFragment() {
    private val minSeekBarValue = 5
    private var zScaleFactor = 30

    override fun getToolbarItems(): List<Widget> = ArrayList<Widget>().apply {
        add(ImageViewWidget.Builder().setId(R.drawable.example_toolbar_settings).setListener { openSettingsDialog() }.build())
    }

    override fun initExample(surface: SciChartSurface) {
        val dataSeries = XyzDataSeries<Date, Double, Double>().apply {
            val tradeTicks = DataManager.getInstance().getTradeTicks(activity)
            for (i in tradeTicks.indices) {
                val tradeData = tradeTicks[i]
                append(tradeData.tradeDate, tradeData.tradePrice, tradeData.tradeSize)
            }
        }

        surface.suspendUpdates {
            xAxes { dateAxis { growBy = DoubleRange(0.0, 0.1) } }
            yAxes { numericAxis { growBy = DoubleRange(0.0, 0.1) } }
            renderableSeries {
                fastLineRenderableSeries {
                    this.dataSeries = dataSeries
                    strokeStyle = SolidPenStyle(0xffff3333, 2f)

                    scaleAnimation {
                        interpolator = ElasticOutInterpolator()
                        zeroLine = 10600.0
                    }
                }
                fastBubbleRenderableSeries {
                    this.dataSeries = dataSeries
                    this.zScaleFactor = this@BubbleChartFragment.zScaleFactor.toDouble() / 10f
                    bubbleBrushStyle = SolidBrushStyle(0x77CCCCCC)
                    strokeStyle = SolidPenStyle(0xFFCCCCCC, 2f)
                    autoZRange = false

                    scaleAnimation {
                        interpolator = ElasticOutInterpolator()
                        zeroLine = 10600.0
                    }
                }
            }
            chartModifiers {
                modifier(RubberBandXyZoomModifier())
                modifier(ZoomExtentsModifier())
            }
        }
    }

    private fun openSettingsDialog() {
        val dialog = ViewSettingsUtil.createSettingsPopup(activity, R.layout.example_bubble_chart_popop_layout)
        with(dialog.findViewById<SeekBar>(R.id.z_scale_seek_bar)) {
            progress = zScaleFactor
            setOnSeekBarChangeListener(object : SeekBarChangeListenerBase() {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    zScaleFactor = progress + minSeekBarValue
                    onZScaleFactorChanged()
                }
            })
        }

        dialog.show()
    }

    private fun onZScaleFactorChanged() {
        binding.surface.suspendUpdates {
            val rSeries = renderableSeries[1] as FastBubbleRenderableSeries
            rSeries.zScaleFactor = (zScaleFactor / 10f).toDouble()
        }
    }
}