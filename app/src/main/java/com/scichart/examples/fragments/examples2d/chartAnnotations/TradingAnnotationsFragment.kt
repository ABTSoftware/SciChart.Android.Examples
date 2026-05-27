//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2026. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// TradingAnnotationsFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.chartAnnotations.kt

import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import com.scichart.charting.modifiers.*
import com.scichart.charting.visuals.annotations.IAnnotation
import com.scichart.charting.visuals.annotations.tradingAnnotations.models.ComparablePoint
import com.scichart.data.model.DoubleRange
import com.scichart.examples.R
import com.scichart.examples.components.SpinnerStringAdapter
import com.scichart.examples.data.MarketDataService
import com.scichart.examples.databinding.ExampleTradingAnnotationsFragmentBinding
import com.scichart.examples.fragments.base.ExampleBaseFragment
import com.scichart.examples.utils.ItemSelectedListenerBase
import com.scichart.examples.utils.scichartExtensions.*
import java.util.*

class TradingAnnotationsFragment : ExampleBaseFragment<ExampleTradingAnnotationsFragmentBinding>(), OnAnnotationCreatedListener {

    private val xabcdModifier = XabcdAnnotationCreationModifier()
    private val pitchforkModifier = PitchforkAnnotationCreationModifier()
    private val extendedLineModifier = ExtendedLineAnnotationCreationModifier()

    override fun inflateBinding(inflater: LayoutInflater): ExampleTradingAnnotationsFragmentBinding {
        return ExampleTradingAnnotationsFragmentBinding.inflate(inflater)
    }

    override fun initExample(binding: ExampleTradingAnnotationsFragmentBinding) {
        binding.annotationTypeSelector.run {
            adapter = SpinnerStringAdapter(activity, R.array.trading_annotation_type_list)
            onItemSelectedListener = object : ItemSelectedListenerBase() {
                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                    disableAllModifiers()
                    when (position) {
                        0 -> xabcdModifier.isEnabled = true
                        1 -> pitchforkModifier.isEnabled = true
                        2 -> extendedLineModifier.isEnabled = true
                    }
                }
            }
        }

        xabcdModifier.setAnnotationCreationListener(this)
        pitchforkModifier.setAnnotationCreationListener(this)
        extendedLineModifier.setAnnotationCreationListener(this)

        val surface = binding.surface
        surface.suspendUpdates {
            xAxes { categoryDateAxis { } }
            yAxes { numericAxis { visibleRange = DoubleRange(30.0, 37.0) } }
            renderableSeries {
                fastCandlestickRenderableSeries {
                    ohlcDataSeries<Date, Double> {
                        val marketDataService = MarketDataService(Calendar.getInstance().time, 5, 5)
                        val data = marketDataService.getHistoricalData(200)

                        append(data.dateData, data.openData, data.highData, data.lowData, data.closeData)
                    }
                    opacity = 0.4f
                }
            }
            annotations {
                xabcdAnnotation {
                    initialBasePoints.add(ComparablePoint(10, 30.6))
                    initialBasePoints.add(ComparablePoint(30, 31.5))
                    initialBasePoints.add(ComparablePoint(50, 30.3))
                    initialBasePoints.add(ComparablePoint(70, 31.5))
                    initialBasePoints.add(ComparablePoint(90, 30.6))
                    setIsEditable(true)
                }
                pitchforkAnnotation {
                    initialBasePoints.add(ComparablePoint(100, 30.6))
                    initialBasePoints.add(ComparablePoint(110, 31.5))
                    initialBasePoints.add(ComparablePoint(130, 30.7))
                    setIsEditable(true)
                }
                extendedLineAnnotation {
                    isExtendStart = true
                    isExtendEnd = true
                    x1 = 70; y1 = 33.3
                    x2 = 90; y2 = 33.7
                    setIsEditable(true)
                }
            }
            chartModifiers {
                modifier(xabcdModifier)
                modifier(pitchforkModifier)
                modifier(extendedLineModifier)
            }
        }

        disableAllModifiers()
        xabcdModifier.isEnabled = true

        binding.deleteAnnotation.setOnClickListener {
            val annotations = surface.annotations
            for (i in annotations.indices.reversed()) {
                val annotation = annotations[i]
                if (annotation.isSelected) {
                    annotations.removeAt(i)
                }
            }
        }
    }

    private fun disableAllModifiers() {
        xabcdModifier.isEnabled = false
        pitchforkModifier.isEnabled = false
        extendedLineModifier.isEnabled = false
    }

    override fun onAnnotationCreated(newAnnotation: IAnnotation) {
        newAnnotation.setIsEditable(true)
    }
}
