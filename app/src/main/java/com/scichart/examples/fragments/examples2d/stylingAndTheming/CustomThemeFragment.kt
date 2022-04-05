//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CustomThemeFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.stylingAndTheming.kt

import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.axes.AutoRange.Always
import com.scichart.charting.visuals.axes.AxisAlignment.Left
import com.scichart.charting.visuals.axes.AxisAlignment.Right
import com.scichart.data.model.DoubleRange
import com.scichart.examples.R
import com.scichart.examples.data.DataManager
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.BillionsLabelProvider
import com.scichart.examples.utils.ThousandsLabelProvider
import com.scichart.examples.utils.interpolator.ElasticOutInterpolator
import com.scichart.examples.utils.scichartExtensions.*

class CustomThemeFragment: ExampleSingleChartBaseFragment() {
    override fun initExample(surface: SciChartSurface) {
        val dataManager = DataManager.getInstance()
        val priceBars = dataManager.getPriceDataIndu(context)

        surface.suspendUpdates {
            // set theme id from styles
            theme = R.style.SciChart_BerryBlue

            xAxes {
                numericAxis {
                    growBy = DoubleRange(0.1, 0.1)
                    visibleRange = DoubleRange(150.0, 180.0)
                }
            }
            yAxes {
                numericAxis {
                    axisId = "PrimaryAxisId"
                    axisAlignment = Right
                    autoRange = Always
                    growBy = DoubleRange(0.1, 0.1)
                    drawMajorTicks = false
                    drawMinorTicks = false
                    labelProvider = ThousandsLabelProvider()
                }
                numericAxis {
                    axisId = "SecondaryAxisId"
                    axisAlignment = Left
                    autoRange = Always
                    growBy = DoubleRange(0.0, 3.0)
                    drawMajorTicks = false
                    drawMinorTicks = false
                    labelProvider = BillionsLabelProvider()
                }
            }
            renderableSeries {
                fastMountainRenderableSeries {
                    yAxisId = "PrimaryAxisId"
                    xyDataSeries<Double, Double>("Mountain Series") {
                        append(priceBars.indexesAsDouble, dataManager.offset(priceBars.lowData, -1000.0))
                    }

                    scaleAnimation { zeroLine = 10500.0; interpolator = ElasticOutInterpolator() }
                }
                fastCandlestickRenderableSeries {
                    yAxisId = "PrimaryAxisId"
                    ohlcDataSeries<Double, Double>("Candlestick Series") {
                        append(priceBars.indexesAsDouble, priceBars.openData, priceBars.highData, priceBars.lowData, priceBars.closeData)
                    }

                    scaleAnimation { zeroLine = 11700.0; interpolator = ElasticOutInterpolator() }
                }
                fastLineRenderableSeries {
                    yAxisId = "PrimaryAxisId"
                    xyDataSeries<Double, Double>("Line Series") {
                        append(priceBars.indexesAsDouble, dataManager.computeMovingAverage(priceBars.closeData, 50))
                    }

                    scaleAnimation { zeroLine = 12250.0; interpolator = ElasticOutInterpolator() }
                }
                fastColumnRenderableSeries {
                    yAxisId = "SecondaryAxisId"
                    xyDataSeries<Double, Long>("Column Series") {
                        append(priceBars.indexesAsDouble, priceBars.volumeData)
                    }

                    scaleAnimation { zeroLine = 10500.0; interpolator = ElasticOutInterpolator() }
                }
            }

            chartModifiers {
                defaultModifiers()
                legendModifier { setShowCheckboxes(false) }
            }
        }
    }
}