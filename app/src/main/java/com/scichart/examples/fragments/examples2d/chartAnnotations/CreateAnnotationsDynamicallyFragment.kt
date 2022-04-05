//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CreateAnnotationsDynamicallyFragment.kt is part of SCICHART®, High Performance Scientific Charts
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
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import com.scichart.charting.modifiers.*
import com.scichart.charting.modifiers.DefaultAnnotationFactory.*
import com.scichart.charting.visuals.annotations.IAnnotation
import com.scichart.data.model.DoubleRange
import com.scichart.examples.R
import com.scichart.examples.components.SpinnerStringAdapter
import com.scichart.examples.data.MarketDataService
import com.scichart.examples.databinding.ExampleCreateAnnotationsDynamicallyFragmentBinding
import com.scichart.examples.fragments.base.ExampleBaseFragment
import com.scichart.examples.utils.ItemSelectedListenerBase
import com.scichart.examples.utils.scichartExtensions.*
import java.util.*

class CreateAnnotationsDynamicallyFragment : ExampleBaseFragment<ExampleCreateAnnotationsDynamicallyFragmentBinding>(), OnAnnotationCreatedListener {

    private val annotationCreationModifier = AnnotationCreationModifier()

    override fun inflateBinding(inflater: LayoutInflater): ExampleCreateAnnotationsDynamicallyFragmentBinding {
        return ExampleCreateAnnotationsDynamicallyFragmentBinding.inflate(inflater)
    }

    override fun initExample(binding: ExampleCreateAnnotationsDynamicallyFragmentBinding) {
        binding.annotationTypeSelector.run {
            adapter = SpinnerStringAdapter(activity, R.array.annotation_type_list)
            setSelection(1)
            onItemSelectedListener = object : ItemSelectedListenerBase() {
                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                    annotationCreationModifier.annotationType = position
                }
            }
        }

        annotationCreationModifier.run {
            annotationFactory = DefaultAnnotationFactory().apply {
                setFactoryForAnnotationType(BOX_ANNOTATION) { _, _ -> sciChartBuilder.newBoxAnnotation().withBackgroundColor(0x6600cc00).build() }
                setFactoryForAnnotationType(CUSTOM_ANNOTATION) { _, _ ->
                    val annotationContent = ImageView(activity)
                    annotationContent.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.example_scichartlogo, null))
                    sciChartBuilder.newCustomAnnotation().withContent(annotationContent).build()
                }
                setFactoryForAnnotationType(TEXT_ANNOTATION) { _, _ -> sciChartBuilder.newTextAnnotation().withText("!!! Your text here !!!").build() }
                setAnnotationCreationListener(this@CreateAnnotationsDynamicallyFragment)
            }
        }

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
            chartModifiers { modifier(annotationCreationModifier) }
        }
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

    override fun onAnnotationCreated(newAnnotation: IAnnotation) {
        newAnnotation.setIsEditable(true)
    }
}