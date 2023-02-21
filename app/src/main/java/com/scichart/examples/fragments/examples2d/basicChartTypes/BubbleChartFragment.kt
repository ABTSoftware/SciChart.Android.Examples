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

import android.view.animation.DecelerateInterpolator
import android.widget.SeekBar
import com.scichart.charting.modifiers.RubberBandXyZoomModifier
import com.scichart.charting.modifiers.ZoomExtentsModifier
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.renderableSeries.FastBubbleRenderableSeries
import com.scichart.charting.visuals.renderableSeries.XyzRenderableSeriesBase
import com.scichart.charting.visuals.renderableSeries.data.XyzRenderPassData
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IFillPaletteProvider
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IPointMarkerPaletteProvider
import com.scichart.charting.visuals.renderableSeries.paletteProviders.PaletteProviderBase
import com.scichart.core.model.IntegerValues
import com.scichart.data.model.DoubleRange
import com.scichart.examples.R
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.*
import com.scichart.examples.utils.interpolator.DefaultInterpolator
import com.scichart.examples.utils.SeekBarChangeListenerBase
import com.scichart.examples.utils.ViewSettingsUtil
import com.scichart.examples.utils.interpolator.ElasticOutInterpolator
import com.scichart.examples.utils.scichartExtensions.*
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget
import com.scichart.examples.utils.widgetgeneration.Widget
import kotlin.math.sin

class BubbleChartFragment : ExampleSingleChartBaseFragment() {
    private val minSeekBarValue = 5
    private var zScaleFactor = 30

    override fun getToolbarItems(): List<Widget> = ArrayList<Widget>().apply {
        add(
            ImageViewWidget.Builder().setId(R.drawable.example_toolbar_settings)
                .setListener { openSettingsDialog() }.build()
        )
    }

    override fun initExample(surface: SciChartSurface) {
        val dataSeries = XyzDataSeries<Double, Double, Double>().apply {
            var prevYValue = 0
            for (i in 0..19) {
                val curYValue = sin(i.toDouble()) * 10 + 5
                val size = sin(i.toDouble()) * 60 + 3
                append(i.toDouble(), prevYValue + curYValue, size)
                prevYValue += curYValue.toInt()
            }
        }

        surface.suspendUpdates {
            xAxes { numericAxis { growBy = DoubleRange(0.0, 0.1) } }
            yAxes { numericAxis { growBy = DoubleRange(0.0, 0.1) } }
            renderableSeries {
                splineLineRenderableSeries {
                    this.dataSeries = dataSeries
                    strokeStyle = SolidPenStyle(0xffE4F5FC, 2f)

                    sweepAnimation {
                        duration = Constant.ANIMATION_DURATION
                        startDelay = Constant.ANIMATION_START_DELAY
                        interpolator = DefaultInterpolator.getInterpolator()
                    }
                }
                fastBubbleRenderableSeries {
                    this.dataSeries = dataSeries
                    autoZRange = false
                    paletteProvider = BubblePaletteProvider(XyzRenderableSeriesBase::class.java)

                    sweepAnimation {
                        duration = Constant.ANIMATION_DURATION
                        startDelay = Constant.ANIMATION_START_DELAY
                        interpolator = DefaultInterpolator.getInterpolator()
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
        val dialog = ViewSettingsUtil.createSettingsPopup(
            activity,
            R.layout.example_bubble_chart_popop_layout
        )
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

    internal open class BubblePaletteProvider (renderableSeriesType: Class<XyzRenderableSeriesBase>) :
        PaletteProviderBase<XyzRenderableSeriesBase>(renderableSeriesType),
        IPointMarkerPaletteProvider, IFillPaletteProvider {

        private val colors = IntegerValues()
        override fun update() {
            val currentRenderPassData =
                renderableSeries.currentRenderPassData as XyzRenderPassData
            val xValues = currentRenderPassData.xValues

            val size = currentRenderPassData.pointsCount()
            colors.setSize(size)

            val colorsArray = colors.itemsArray
            val valuesArray = xValues.itemsArray

            for (i in 0 until size) {
                val value = valuesArray[i]
                if (value in 9.0..12.0) {
                    colorsArray[i] = -0x780b7be0
                } else {
                    colorsArray[i] = -0x78af3820
                }
            }
        }

        override fun getPointMarkerColors(): IntegerValues {
            return colors
        }

        override fun getFillColors(): IntegerValues {
            return colors
        }
    }
}