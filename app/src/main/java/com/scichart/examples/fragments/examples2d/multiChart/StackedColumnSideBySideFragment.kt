//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// StackedColumnSideBySideFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

import android.view.Gravity.*
import android.view.animation.DecelerateInterpolator
import com.scichart.charting.numerics.labelProviders.FormatterLabelProviderBase
import com.scichart.charting.numerics.labelProviders.LabelFormatterBase
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.axes.AutoRange.Always
import com.scichart.charting.visuals.axes.NumericAxis
import com.scichart.data.model.DoubleRange
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.Constant
import com.scichart.examples.utils.interpolator.DefaultInterpolator
import com.scichart.examples.utils.scichartExtensions.*

class StackedColumnSideBySideFragment : ExampleSingleChartBaseFragment() {
    override fun initExample(surface: SciChartSurface) {
        val seriesData = mutableListOf(
            doubleArrayOf(1.269, 1.330, 1.356, 1.304),
            doubleArrayOf(1.004, 1.173, Double.NaN, 1.656),
            doubleArrayOf(0.282, 0.310, Double.NaN, 0.439),
            doubleArrayOf(0.214, 0.243, Double.NaN, 0.313),
            doubleArrayOf(0.176, 0.201, Double.NaN, 0.261),
            doubleArrayOf(0.146, 0.184, 0.196, 0.276),
            doubleArrayOf(0.123, 0.152, 0.177, 0.264),
            doubleArrayOf(0.130, 0.156, 0.166, 0.234),
            doubleArrayOf(0.147, 0.139, 0.142, 0.109),
            doubleArrayOf(0.126, 0.127, 0.127, 0.094),
            doubleArrayOf(2.466, 2.829, 3.005, 4.306)
        )
        val countries = arrayOf("China", "India", "USA", "Indonesia", "Brazil", "Pakistan", "Nigeria", "Bangladesh", "Russia", "Japan", "Rest Of The World", "Total")
        val fills = listOf(0xff3399ff, 0xff014358, 0xff1f8a71, 0xffbdd63b, 0xffffe00b, 0xfff27421, 0xffbb0000, 0xff550033, 0xff339933, 0xff00aba9, 0xff560068)
        val strokes = listOf(0xff2D68BC, 0xff013547, 0xff1B5D46, 0xff7E952B, 0xffAA8F0B, 0xffA95419, 0xff840000, 0xff370018, 0xff2D732D, 0xff006C6A, 0xff3D0049)

        surface.suspendUpdates {
            xAxes {
                numericAxis {
                    autoTicks = false
                    majorDelta = 1.0
                    minorDelta = 0.5
                    labelProvider = YearsLabelProvider()
                }
            }
            yAxes {
                numericAxis {
                    autoRange = Always
                    axisTitle = "billions of People"
                    growBy = DoubleRange(0.0, 0.1)
                }
            }
            renderableSeries {
                horizontallyStackedColumnsCollection {
                    for (i in 0 until seriesData.size) {
                        stackedColumnRenderableSeries {
                            xyDataSeries<Double, Double>(countries[i]) {
                                val points = seriesData[i]
                                for (j in points.indices) {
                                    append(j.toDouble(), points[j])
                                }
                            }
                            fillBrushStyle = SolidBrushStyle(fills[i])
                            strokeStyle = SolidPenStyle(strokes[i], 0f)

                            waveAnimation {
                                duration = Constant.ANIMATION_DURATION
                                startDelay = Constant.ANIMATION_START_DELAY
                                interpolator = DefaultInterpolator.getInterpolator()
                            }
                        }
                    }
                }
            }

            chartModifiers {
                legendModifier { setLegendPosition(TOP or START, 10) }
                tooltipModifier()
            }
        }
    }

    private class YearsLabelFormatter<T : NumericAxis> : LabelFormatterBase<T>() {
        private val _xLabels = arrayOf("2000", "2010", "2014", "2050")

        override fun update(axis: T) { }

        override fun formatLabel(dataValue: Double): CharSequence {
            val i = dataValue.toInt()
            return if (i in 0 until 4) _xLabels[i] else ""
        }

        override fun formatCursorLabel(dataValue: Double): CharSequence {
            val i = dataValue.toInt()
            return when {
                i in 0 until 4 -> { _xLabels[i] }
                i < 0 -> { _xLabels[0] }
                else -> { _xLabels[3] }
            }
        }
    }

    private class YearsLabelProvider : FormatterLabelProviderBase<NumericAxis>(NumericAxis::class.java, YearsLabelFormatter())
}