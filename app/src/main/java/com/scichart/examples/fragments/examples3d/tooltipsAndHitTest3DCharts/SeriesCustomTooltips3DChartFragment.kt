//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SeriesCustomTooltips3DChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples3d.tooltipsAndHitTest3DCharts.kt

import android.content.Context
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import com.scichart.charting3d.modifiers.CrosshairMode.Lines
import com.scichart.charting3d.modifiers.TooltipModifier3D
import com.scichart.charting3d.visuals.renderableSeries.XyzRenderableSeries3DBase
import com.scichart.charting3d.visuals.renderableSeries.hitTest.DefaultXyzSeriesInfo3DProvider
import com.scichart.charting3d.visuals.renderableSeries.hitTest.XyzSeriesInfo3D
import com.scichart.charting3d.visuals.renderableSeries.tooltips.ISeriesTooltip3D
import com.scichart.charting3d.visuals.renderableSeries.tooltips.XyzSeriesTooltip3D
import com.scichart.core.utility.StringUtil
import com.scichart.data.model.DoubleRange
import com.scichart.examples.data.DataManager
import com.scichart.examples.databinding.ExampleSingleChart3dWithModifierTipFragmentBinding
import com.scichart.examples.fragments.base.ExampleBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import kotlin.math.sqrt

class SeriesCustomTooltips3DChartFragment : ExampleBaseFragment<ExampleSingleChart3dWithModifierTipFragmentBinding>() {
    override fun inflateBinding(inflater: LayoutInflater): ExampleSingleChart3dWithModifierTipFragmentBinding {
        return ExampleSingleChart3dWithModifierTipFragmentBinding.inflate(inflater)
    }

    override fun initExample(binding: ExampleSingleChart3dWithModifierTipFragmentBinding) {
        val dataManager = DataManager.getInstance()

        binding.surface3d.suspendUpdates {
            xAxis = numericAxis3D { growBy = DoubleRange(0.2, 0.2); visibleRange = DoubleRange(-1.1, 1.1) }
            yAxis = numericAxis3D { growBy = DoubleRange(0.2, 0.2); visibleRange = DoubleRange(-1.1, 1.1) }
            zAxis = numericAxis3D { growBy = DoubleRange(0.2, 0.2); visibleRange = DoubleRange(-1.1, 1.1) }

            renderableSeries {
                scatterRenderableSeries3D {
                    xyzDataSeries3D<Double, Double, Double> {
                        for (i in 0 until 500) {
                            val m1 = if (dataManager.randomBoolean) -1.0 else 1.0
                            val m2 = if (dataManager.randomBoolean) -1.0 else 1.0

                            val x1 = dataManager.randomDouble * m1
                            val x2 = dataManager.randomDouble * m2
                            val temp = 1 - x1 * x1 - x2 * x2

                            val x = 2 * x1 * sqrt(temp)
                            val y = 2 * x2 * sqrt(temp)
                            val z = 1 - 2 * (x1 * x1 + x2 * x2)

                            append(x, y, z)
                        }
                    }
                    spherePointMarker3D { fill = 0x88FFFFFF.toInt(); size = 7f }
                    seriesInfoProvider = CustomSeriesInfo3DProvider()
                }
            }

            chartModifiers {
                pinchZoomModifier3D()
                orbitModifier3D { receiveHandledEvents = true; executeOnPointerCount = 2 }
                zoomExtentsModifier3D()
                tooltipModifier3D {
                    receiveHandledEvents = true
                    crosshairMode = Lines
                    crosshairPlanesFill = 0x33FF6600
                    executeOnPointerCount = 1
                }
            }
        }
    }

    private class CustomSeriesInfo3DProvider : DefaultXyzSeriesInfo3DProvider() {
        override fun getSeriesTooltipInternal(context: Context, seriesInfo: XyzSeriesInfo3D<out XyzRenderableSeries3DBase>, modifierType: Class<*>): ISeriesTooltip3D {
            return if (modifierType == TooltipModifier3D::class.java) {
                CustomXyzSeriesTooltip3D(context, seriesInfo)
            } else {
                super.getSeriesTooltipInternal(context, seriesInfo, modifierType)
            }
        }

        private class CustomXyzSeriesTooltip3D(context: Context?, seriesInfo: XyzSeriesInfo3D<*>) : XyzSeriesTooltip3D(context, seriesInfo) {
            override fun internalUpdate(seriesInfo: XyzSeriesInfo3D<*>) {
                val sb = SpannableStringBuilder()

                sb.append("This is Custom Tooltip").append(StringUtil.NEW_LINE)
                sb.append("VertexId: ").append(seriesInfo.vertexId.toString()).append(StringUtil.NEW_LINE)

                sb.append("X: ").append(seriesInfo.formattedXValue).append(StringUtil.NEW_LINE)
                sb.append("Y: ").append(seriesInfo.formattedYValue).append(StringUtil.NEW_LINE)
                sb.append("Z: ").append(seriesInfo.formattedZValue)

                text = sb
                setSeriesColor(seriesInfo.seriesColor)
            }
        }
    }
}