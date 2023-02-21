//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// StackedColumnChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

import android.view.animation.DecelerateInterpolator
import com.scichart.charting.modifiers.ZoomExtentsModifier
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.Constant
import com.scichart.examples.utils.interpolator.DefaultInterpolator
import com.scichart.examples.utils.scichartExtensions.*

class StackedColumnChartFragment: ExampleSingleChartBaseFragment() {
    override fun initExample(surface: SciChartSurface) {
        surface.suspendUpdates {
            xAxes { numericAxis() }
            yAxes { numericAxis() }

            renderableSeries {
                horizontallyStackedColumnsCollection {
                    spacing = 0.0
                    verticallyStackedColumnsCollection {
                        stackedColumnRenderableSeries {
                            xyDataSeries<Double, Double>("Pork Series") {
                                for (i in porkData.indices) append((initialDate + i).toDouble(), porkData[i])
                            }
                            fillBrushStyle = SolidBrushStyle(0xff226fb7)
                            strokeStyle = SolidPenStyle(0xff22579D, 0f)

                            waveAnimation {
                                duration = Constant.ANIMATION_DURATION
                                startDelay = Constant.ANIMATION_START_DELAY
                                interpolator = DefaultInterpolator.getInterpolator()
                            }
                        }
                        stackedColumnRenderableSeries {
                            xyDataSeries<Double, Double>("Veal Series") {
                                for (i in vealData.indices) append((initialDate + i).toDouble(), vealData[i])
                            }
                            fillBrushStyle = SolidBrushStyle(0xffff9a2e)
                            strokeStyle = SolidPenStyle(0xffBE642D, 0f)

                            waveAnimation {
                                duration = Constant.ANIMATION_DURATION
                                startDelay = Constant.ANIMATION_START_DELAY
                                interpolator = DefaultInterpolator.getInterpolator()
                            }
                        }
                    }
                    verticallyStackedColumnsCollection {
                        stackedColumnRenderableSeries {
                            xyDataSeries<Double, Double>("Tomato Series") {
                                for (i in tomatoesData.indices) append((initialDate + i).toDouble(), tomatoesData[i])
                            }
                            fillBrushStyle = SolidBrushStyle(0xffdc443f)
                            strokeStyle = SolidPenStyle(0xffA33631, 0f)

                            waveAnimation {
                                duration = Constant.ANIMATION_DURATION
                                startDelay = Constant.ANIMATION_START_DELAY
                                interpolator = DefaultInterpolator.getInterpolator()
                            }
                        }
                        stackedColumnRenderableSeries {
                            xyDataSeries<Double, Double>("Cucumber Series") {
                                for (i in cucumberData.indices) append((initialDate + i).toDouble(), cucumberData[i])
                            }
                            fillBrushStyle = SolidBrushStyle(0xffaad34f)
                            strokeStyle = SolidPenStyle(0xff73953D, 0f)

                            waveAnimation {
                                duration = Constant.ANIMATION_DURATION
                                startDelay = Constant.ANIMATION_START_DELAY
                                interpolator = DefaultInterpolator.getInterpolator()
                            }
                        }
                        stackedColumnRenderableSeries {
                            xyDataSeries<Double, Double>("Pepper Series") {
                                for (i in pepperData.indices) append((initialDate + i).toDouble(), pepperData[i])
                            }
                            fillBrushStyle = SolidBrushStyle(0xff8562b4)
                            strokeStyle = SolidPenStyle(0xff64458A, 0f)

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
                rolloverModifier()
                ZoomExtentsModifier()
            }
        }
    }

    companion object {
        const val initialDate = 1992
        val porkData = doubleArrayOf(10.0, 13.0, 7.0, 16.0, 4.0, 6.0, 20.0, 14.0, 16.0, 10.0, 24.0, 11.0)
        val vealData = doubleArrayOf(12.0, 17.0, 21.0, 15.0, 19.0, 18.0, 13.0, 21.0, 22.0, 20.0, 5.0, 10.0)
        val tomatoesData = doubleArrayOf(7.0, 30.0, 27.0, 24.0, 21.0, 15.0, 17.0, 26.0, 22.0, 28.0, 21.0, 22.0)
        val cucumberData = doubleArrayOf(16.0, 10.0, 9.0, 8.0, 22.0, 14.0, 12.0, 27.0, 25.0, 23.0, 17.0, 17.0)
        val pepperData = doubleArrayOf(7.0, 24.0, 21.0, 11.0, 19.0, 17.0, 14.0, 27.0, 26.0, 22.0, 28.0, 16.0)
    }
}