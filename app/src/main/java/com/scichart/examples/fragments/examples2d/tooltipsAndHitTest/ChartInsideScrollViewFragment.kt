//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ChartInsideScrollViewFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.tooltipsAndHitTest.kt

import android.view.LayoutInflater
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.axes.AutoRange
import com.scichart.charting.visuals.axes.AxisAlignment.Left
import com.scichart.core.model.DoubleValues
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.utility.ColorUtil
import com.scichart.examples.R
import com.scichart.examples.data.DataManager
import com.scichart.examples.databinding.ExampleChartInsideScrollViewFragmentBinding
import com.scichart.examples.fragments.base.ExampleBaseFragment
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.Constant
import com.scichart.examples.utils.scichartExtensions.*
import java.util.Date

class ChartInsideScrollViewFragment: ExampleBaseFragment<ExampleChartInsideScrollViewFragmentBinding>() {

    override fun inflateBinding(inflater: LayoutInflater): ExampleChartInsideScrollViewFragmentBinding {
        return ExampleChartInsideScrollViewFragmentBinding.inflate(inflater)
    }

    override fun initExample(binding: ExampleChartInsideScrollViewFragmentBinding) {
        val priceSeries = DataManager.getInstance().getPriceDataIndu(activity)
        val size = priceSeries.size

        binding.surface.suspendUpdates {
            xAxes { categoryDateAxis {
                visibleRange = DoubleRange(size - 30.0, size.toDouble())
                growBy = DoubleRange(0.0, 0.1)
            }}
            yAxes { numericAxis {
                growBy = DoubleRange(0.0, 0.1)
            }}
            renderableSeries {
                fastCandlestickRenderableSeries {
                    ohlcDataSeries<Date, Double> {
                        append(priceSeries.dateData, priceSeries.openData, priceSeries.highData, priceSeries.lowData, priceSeries.closeData)
                    }
                    strokeUpStyle = SolidPenStyle(0xFF00AA00)
                    fillUpBrushStyle = SolidBrushStyle(0x8800AA00)
                    strokeDownStyle = SolidPenStyle(0xFFFF0000)
                    fillDownBrushStyle = SolidBrushStyle(0x88FF0000)
                }
            }
            chartModifiers {
                zoomPanModifier {  }
                pinchZoomModifier {  }
                zoomExtentsModifier {  }
            }
        }

        binding.chartInsideScrollViewToggle.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.surface.isDisallowInterceptTouchEvent = isChecked
        }
    }

}