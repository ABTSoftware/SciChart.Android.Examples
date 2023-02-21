//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// FanChartFragment.kt is part of SCICHART®, High Performance Scientific Charts
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
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.core.utility.IterableUtil
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.utility.ColorUtil.Red
import com.scichart.drawing.utility.ColorUtil.Transparent
import com.scichart.examples.data.RandomWalkGenerator
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.interpolator.DefaultInterpolator
import com.scichart.examples.utils.scichartExtensions.*
import java.util.*

class FanChartFragment: ExampleSingleChartBaseFragment() {
    override fun initExample(surface: SciChartSurface) {
        val actualDataSeries = XyDataSeries<Date, Double>()
        val var3DataSeries = XyyDataSeries<Date, Double>()
        val var2DataSeries = XyyDataSeries<Date, Double>()
        val var1DataSeries = XyyDataSeries<Date, Double>()

        val varianceData = getVarianceData()
        for (i in varianceData.indices) {
            val dataPoint = varianceData[i]
            actualDataSeries.append(dataPoint.date, dataPoint.actual)
            var3DataSeries.append(dataPoint.date, dataPoint.varMin, dataPoint.varMax)
            var2DataSeries.append(dataPoint.date, dataPoint.var1, dataPoint.var4)
            var1DataSeries.append(dataPoint.date, dataPoint.var2, dataPoint.var3)
        }

        surface.suspendUpdates {
            xAxes { dateAxis { growBy = DoubleRange(0.1, 0.1) } }
            yAxes { numericAxis { growBy = DoubleRange(0.1, 0.1) } }

            renderableSeries {
                fastBandRenderableSeries {
                    dataSeries = var3DataSeries
                    strokeY1Style = SolidPenStyle(Transparent)
                    strokeStyle = SolidPenStyle(Transparent)

                    sweepAnimation {
                        duration = 500
                        startDelay = 600
                        interpolator = DefaultInterpolator.getInterpolator()
                    }
                }
                fastBandRenderableSeries {
                    dataSeries = var2DataSeries
                    strokeY1Style = SolidPenStyle(Transparent)
                    strokeStyle = SolidPenStyle(Transparent)

                    sweepAnimation {
                        duration = 500
                        startDelay = 600
                        interpolator = DefaultInterpolator.getInterpolator()
                    }
                }
                fastBandRenderableSeries {
                    dataSeries = var1DataSeries
                    strokeY1Style = SolidPenStyle(Transparent)
                    strokeStyle = SolidPenStyle(Transparent)

                    sweepAnimation {
                        duration = 500
                        startDelay = 600
                        interpolator = DefaultInterpolator.getInterpolator()
                    }
                }
                fastLineRenderableSeries {
                    dataSeries = actualDataSeries
                    strokeStyle = SolidPenStyle(0xFFe97064)

                    sweepAnimation {
                        duration = 500
                        startDelay = 100
                        interpolator = DefaultInterpolator.getInterpolator()
                    }
                }
            }

            chartModifiers { defaultModifiers() }
        }
    }

    // Create a table of Variance data. Each row in the table consists of
    //
    //  DateTime, Actual (Y-Value), Projected Min, Variance 1, 2, 3, 4 and Projected Maximum
    //
    //        DateTime    Actual 	Min     Var1	Var2	Var3	Var4	Max
    //        Jan-11	  y0	    -	    -	    -	    -	    -	    -
    //        Feb-11	  y1	    -	    -	    -	    -	    -	    -
    //        Mar-11	  y2	    -	    -	    -	    -	    -	    -
    //        Apr-11	  y3	    -	    -	    -	    -	    -	    -
    //        May-11	  y4	    -	    -	    -	    -	    -	    -
    //        Jun-11	  y5        min0  var1_0  var2_0  var3_0  var4_0  max_0
    //        Jul-11	  y6        min1  var1_1  var2_1  var3_1  var4_1  max_1
    //        Aug-11	  y7        min2  var1_2  var2_2  var3_2  var4_2  max_2
    //        Dec-11	  y8        min3  var1_3  var2_3  var3_3  var4_3  max_3
    //        Jan-12      y9        min4  var1_4  var2_4  var3_4  var4_4  max_4
    private fun getVarianceData(): List<VarPoint> {
        val count = 10
        val dates = IterableUtil.toArray(IterableUtil.range(0, count) { arg ->
            val instance = Calendar.getInstance()
            instance.clear()
            instance[2011, 1] = 1
            instance.add(Calendar.MONTH, arg!!)
            instance.time
        }, Date::class.java)

        val yValues = RandomWalkGenerator().getRandomWalkSeries(count).yValues.itemsArray

        val result: MutableList<VarPoint> = ArrayList()
        for (i in 0 until count) {
            var varMax = Double.NaN
            var var4 = Double.NaN
            var var3 = Double.NaN
            var var2 = Double.NaN
            var var1 = Double.NaN
            var varMin = Double.NaN

            if (i > 4) {
                varMax = yValues[i] + (i - 5) * 0.3
                var4 = yValues[i] + (i - 5) * 0.2
                var3 = yValues[i] + (i - 5) * 0.1
                var2 = yValues[i] - (i - 5) * 0.1
                var1 = yValues[i] - (i - 5) * 0.2
                varMin = yValues[i] - (i - 5) * 0.3
            }

            result.add(VarPoint(dates[i], yValues[i], var4, var3, var2, var1, varMin, varMax))
        }
        return result
    }

    private class VarPoint(
        val date: Date,
        val actual: Double,
        val var4: Double,
        val var3: Double,
        val var2: Double,
        val var1: Double,
        val varMin: Double,
        val varMax: Double
    )
}