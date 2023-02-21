//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// MultipleChartsFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

import android.content.res.Configuration
import android.graphics.Color
import android.view.LayoutInflater
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.annotations.HorizontalAnchorPoint
import com.scichart.charting.visuals.annotations.VerticalAnchorPoint
import com.scichart.charting.visuals.renderableSeries.hitTest.DefaultXySeriesInfoProvider
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.common.FontStyle
import com.scichart.examples.R
import com.scichart.examples.databinding.ExampleMultipleChartsFragmentBinding
import com.scichart.examples.fragments.base.ExampleBaseFragment
import com.scichart.examples.utils.Constant
import com.scichart.examples.utils.interpolator.DefaultInterpolator
import com.scichart.examples.utils.scichartExtensions.*
import kotlin.math.sin

class MultipleChartsFragment: ExampleBaseFragment<ExampleMultipleChartsFragmentBinding>() {
    private val sharedXRange = DoubleRange()
    private val sharedYRange = DoubleRange()

    override fun showDefaultModifiersInToolbar(): Boolean = false

    override fun inflateBinding(inflater: LayoutInflater): ExampleMultipleChartsFragmentBinding {
        return ExampleMultipleChartsFragmentBinding.inflate(inflater)
    }

    override fun initExample(binding: ExampleMultipleChartsFragmentBinding) {
        val orientation = resources.configuration.orientation
        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            binding.multiChartLinearLayout.orientation = LinearLayout.HORIZONTAL
        } else {
            binding.multiChartLinearLayout.orientation = LinearLayout.VERTICAL
        }

        initChart0(binding.chart0)
        initChart1(binding.chart1)
        initChart2(binding.chart2)
    }

    private fun initChart0(surface: SciChartSurface) {
        surface.theme = R.style.SciChart_NavyBlue

        surface.suspendUpdates {
            xAxes { numericAxis { growBy = DoubleRange(0.1, 0.1) } }
            yAxes { numericAxis { growBy = DoubleRange(0.1, 0.1) } }
            renderableSeries {
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double> {
                        for (i in 1 until POINTS_COUNT) {
                            append(i.toDouble(), Math.asin(i * 0.01) + Math.sin(i * Math.PI * 0.1))
                        }
                    }
                    strokeStyle = SolidPenStyle(0xFF47bde6)

                    sweepAnimation {
                        duration = Constant.ANIMATION_DURATION
                        startDelay = Constant.ANIMATION_START_DELAY
                        interpolator = DefaultInterpolator.getInterpolator()
                    }
                }
            }

            chartModifiers {
                modifierGroup(context) {
                    annotations {
                        textAnnotation {
                            text = "Simple Line Chart"
                            x1 = 50f; y1 = 2f
                            fontStyle = FontStyle(50f, Color.WHITE)
                            horizontalAnchorPoint = HorizontalAnchorPoint.Center
                            verticalAnchorPoint = VerticalAnchorPoint.Center
                        }
                    }
                }
            }
        }

        surface.zoomExtents()
    }

    private fun initChart1(surface: SciChartSurface) {
        surface.theme = R.style.SciChart_NavyBlue

        val xValues = intArrayOf(
            0,
            1,
            2,
            3,
            4,
            5,
            6,
            7,
            8,
            9,
            10,
            11,
            12,
            13,
            14,
            15,
            16,
            17,
            18,
            19,
            20,
            21,
            22,
            23,
            24
        )
        val yValues = arrayOf(
            0.3933834,
            -0.0493884,
            0.4083136,
            -0.0458077,
            -0.5242618,
            -0.9631066,
            -0.6873195,
            0.0,
            -0.1682597,
            0.1255406,
            -0.0313127,
            -0.3261995,
            -0.5490017,
            -0.2462973,
            0.2475873,
            0.15,
            -0.2443795,
            -0.7002707,
            0.0,
            -1.24664,
            -0.8722853,
            -1.1531512,
            -0.7264951,
            -0.9779677,
            -0.5377044
        )

        surface.suspendUpdates {
            xAxes { numericAxis { growBy = DoubleRange(0.1, 0.1) } }
            yAxes { numericAxis { growBy = DoubleRange(0.1, 0.1) } }
            renderableSeries {
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double> {
                        for (i in 1 until xValues.size) {
                            append(xValues[i].toDouble(), yValues[i])
                        }
                    }
                    strokeStyle = SolidPenStyle(Color.WHITE)
                    seriesInfoProvider = DefaultXySeriesInfoProvider()

                    sweepAnimation {
                        duration = Constant.ANIMATION_DURATION
                        startDelay = Constant.ANIMATION_START_DELAY
                        interpolator = DefaultInterpolator.getInterpolator()
                    }
                }
            }

            chartModifiers {
                modifierGroup(context) {
                    rolloverModifier()
                    annotations {
                        textAnnotation {
                            text = "Tooltip on Line Chart"
                            x1 = 12f; y1 = 0.4f
                            fontStyle = FontStyle(50f, Color.WHITE)
                            horizontalAnchorPoint = HorizontalAnchorPoint.Center
                            verticalAnchorPoint = VerticalAnchorPoint.Center
                        }
                    }
                }
            }
        }

        surface.zoomExtents()
    }

    private fun initChart2(surface: SciChartSurface) {
        surface.theme = R.style.SciChart_NavyBlue

        val xValues = intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8)
        val yValues = doubleArrayOf(1.0, 2.0, 3.0, 2.0, 0.5, 1.0, 2.5, 1.0, 1.0)

        surface.suspendUpdates {
            xAxes { numericAxis { growBy = DoubleRange(0.1, 0.1)} }
            yAxes { numericAxis { growBy = DoubleRange(0.1, 0.1)} }
            renderableSeries {
                fastLineRenderableSeries {
                    xyDataSeries<Double, Double> {
                        for (i in 1 until xValues.size) {
                            append(xValues[i].toDouble(), yValues[i])
                        }
                    }
                    strokeStyle = SolidPenStyle(0xFFae418d)
                    setIsDigitalLine(true)

                    ellipsePointMarker {
                        width = 20; height = 20
                        fillStyle = SolidBrushStyle(0xFFf4840b)
                    }

                    sweepAnimation {
                        duration = Constant.ANIMATION_DURATION
                        startDelay = Constant.ANIMATION_START_DELAY
                        interpolator = DefaultInterpolator.getInterpolator()
                    }
                }
            }

            chartModifiers {
                modifierGroup(context) {
                    annotations {
                        textAnnotation {
                            text = "Digital Line Chart"
                            x1 = 5f; y1 = 3f
                            fontStyle = FontStyle(50f, Color.WHITE)
                            horizontalAnchorPoint = HorizontalAnchorPoint.Center
                            verticalAnchorPoint = VerticalAnchorPoint.Center
                        }
                    }
                }
            }
        }

        surface.zoomExtents()
    }

    companion object {
        private const val POINTS_COUNT = 100
    }
}