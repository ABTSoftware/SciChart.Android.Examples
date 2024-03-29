//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// PanAndZoomChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.zoomAndPanAChart.kt

import android.view.animation.DecelerateInterpolator
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.data.model.DoubleRange
import com.scichart.examples.data.DataManager
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.Constant
import com.scichart.examples.utils.interpolator.DefaultInterpolator
import com.scichart.examples.utils.scichartExtensions.*

class PanAndZoomChartFragment : ExampleSingleChartBaseFragment() {

    override fun initExample(surface: SciChartSurface) {
        val data1 = DataManager.getInstance().getDampedSinewave(300, 1.0, 0.0, 0.01, 1000, 10)
        val data2 = DataManager.getInstance().getDampedSinewave(300, 1.0, 0.0, 0.024, 1000, 10)
        val data3 = DataManager.getInstance().getDampedSinewave(300, 1.0, 0.0, 0.049, 1000, 10)

        surface.suspendUpdates {
            xAxes {
                numericAxis {
                    growBy = DoubleRange(0.1, 0.1)
                    visibleRange = DoubleRange(3.0, 6.0)
                }
            }
            yAxes { numericAxis { growBy = DoubleRange(0.1, 0.1) } }

            renderableSeries {
                fastMountainRenderableSeries {
                    areaStyle = SolidBrushStyle(0xFF47bde6)
                    strokeStyle = SolidPenStyle(0xFF47bde6)
                    xyDataSeries<Double, Double> { append(data1.xValues, data1.yValues) }

                    scaleAnimation {
                        interpolator = DefaultInterpolator.getInterpolator()
                        duration = Constant.ANIMATION_DURATION
                        startDelay = Constant.ANIMATION_START_DELAY
                    }
                }
                fastMountainRenderableSeries {
                    areaStyle = SolidBrushStyle(0x77ae418d)
                    strokeStyle = SolidPenStyle(0xFFae418d)
                    xyDataSeries<Double, Double> { append(data2.xValues, data2.yValues) }

                    scaleAnimation {
                        interpolator = DefaultInterpolator.getInterpolator()
                        duration = Constant.ANIMATION_DURATION
                        startDelay = Constant.ANIMATION_START_DELAY
                    }
                }
                fastMountainRenderableSeries {
                    areaStyle = SolidBrushStyle(0x77e97064)
                    strokeStyle = SolidPenStyle(0xFFefcdb4)
                    xyDataSeries<Double, Double> { append(data3.xValues, data3.yValues) }

                    scaleAnimation {
                        interpolator = DefaultInterpolator.getInterpolator()
                        duration = Constant.ANIMATION_DURATION
                        startDelay = Constant.ANIMATION_START_DELAY
                    }
                }
            }

            chartModifiers {
                pinchZoomModifier { receiveHandledEvents = true }
                zoomPanModifier { receiveHandledEvents = true }
                zoomExtentsModifier()
            }
        }
    }
}