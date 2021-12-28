//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// DateAxis3DFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples3d.axis3D.kt

import com.scichart.charting3d.visuals.SciChartSurface3D
import com.scichart.charting3d.visuals.renderableSeries.data.GradientColorPalette
import com.scichart.core.utility.DateIntervalUtil
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.utility.ColorUtil
import com.scichart.examples.fragments.base.ExampleSingleChart3DBaseFragment
import com.scichart.examples.utils.scichartExtensions.*
import java.util.*

class DateAxis3DFragment : ExampleSingleChart3DBaseFragment() {

    override fun initExample(surface3d: SciChartSurface3D) {
        val calendar = Calendar.getInstance().apply {
            clear()
            set(2019, Calendar.MAY, 1)
        }

        val date = calendar.time
        val daysCount = 7
        val measurementsCount = 24

        surface3d.suspendUpdates {
            xAxis = dateAxis3D {
                subDayTextFormatting = "HH:mm"
                maxAutoTicks = 8
            }
            yAxis = numericAxis3D { growBy = DoubleRange(0.0, 0.1) }
            zAxis = dateAxis3D {
                textFormatting = "dd MMM"
                maxAutoTicks = 5
            }

            renderableSeries {
                waterfallRenderableSeries3D {
                    waterfallDataSeries3D<Date, Double, Date>(measurementsCount, daysCount) {
                        startX = date
                        stepX = Date(DateIntervalUtil.fromMinutes(30.0))
                        startZ = date
                        stepX = Date(DateIntervalUtil.fromDays(1.0))

                        for (z in 0 until daysCount) {
                            val temperatures = TEMPERATURES[z]
                            for (x in 0 until measurementsCount) {
                                updateYAt(x, z, temperatures[x])
                            }
                        }
                    }
                    stroke = ColorUtil.Blue
                    strokeThickness = 1f
                    sliceThickness = 2f
                    yColorMapping = GradientColorPalette(
                        intArrayOf(ColorUtil.Red, ColorUtil.Orange, ColorUtil.Yellow, ColorUtil.GreenYellow, ColorUtil.DarkGreen),
                        floatArrayOf(0f, .25f, .5f, .75f, 1f)
                    )
                }
            }

            chartModifiers { defaultModifiers3D() }
        }
    }

    companion object {
        private val TEMPERATURES = arrayOf(
            doubleArrayOf(
                8.0, 8.0, 7.0, 7.0, 6.0, 6.0, 6.0, 6.0,
                6.0, 6.0, 6.0, 7.0, 7.0, 7.0, 8.0, 9.0,
                9.0, 10.0, 10.0, 10.0, 10.0, 10.0, 9.0, 9.0
            ),
            doubleArrayOf(
                9.0, 7.0, 7.0, 7.0, 6.0, 6.0, 6.0, 6.0,
                7.0, 7.0, 8.0, 9.0, 9.0, 12.0, 15.0, 16.0,
                16.0, 16.0, 17.0, 16.0, 15.0, 13.0, 12.0, 11.0
            ),
            doubleArrayOf(
                11.0, 10.0, 9.0, 11.0, 7.0, 7.0, 7.0, 9.0,
                11.0, 13.0, 15.0, 16.0, 17.0, 18.0, 17.0, 18.0,
                19.0, 19.0, 18.0, 10.0, 10.0, 11.0, 10.0, 10.0
            ),
            doubleArrayOf(
                11.0, 10.0, 11.0, 10.0, 11.0, 10.0, 10.0, 11.0,
                11.0, 13.0, 13.0, 13.0, 15.0, 15.0, 15.0, 16.0,
                17.0, 18.0, 17.0, 17.0, 15.0, 13.0, 12.0, 11.0
            ),
            doubleArrayOf(
                13.0, 14.0, 12.0, 12.0, 11.0, 12.0, 12.0, 12.0,
                13.0, 15.0, 17.0, 18.0, 20.0, 21.0, 21.0, 22.0,
                22.0, 21.0, 20.0, 19.0, 17.0, 16.0, 15.0, 16.0
            ),
            doubleArrayOf(
                16.0, 16.0, 16.0, 15.0, 14.0, 14.0, 14.0, 12.0,
                13.0, 13.0, 14.0, 14.0, 13.0, 15.0, 15.0, 15.0,
                15.0, 15.0, 14.0, 15.0, 15.0, 14.0, 14.0, 14.0
            ),
            doubleArrayOf(
                14.0, 15.0, 14.0, 13.0, 14.0, 13.0, 13.0, 14.0,
                14.0, 16.0, 18.0, 17.0, 16.0, 18.0, 20.0, 19.0,
                16.0, 16.0, 16.0, 16.0, 15.0, 14.0, 13.0, 12.0
            )
        )
    }
}