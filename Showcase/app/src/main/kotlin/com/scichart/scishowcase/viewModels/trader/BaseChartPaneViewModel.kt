//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// BaseChartPaneViewModel.kt is part of the SCICHART® Showcases. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® Showcases are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.scishowcase.viewModels.trader

import android.content.Context
import android.databinding.Bindable
import android.databinding.ObservableBoolean
import android.widget.ImageView
import com.scichart.charting.modifiers.*
import com.scichart.charting.visuals.annotations.CustomAnnotation
import com.scichart.charting.visuals.annotations.TextAnnotation
import com.scichart.scishowcase.R
import com.scichart.scishowcase.viewModels.ChartViewModel

open class BaseChartPaneViewModel(context: Context, mainAxisId: String, listener: OnAnnotationCreatedListener) : ChartViewModel(context) {

    private val annotationFactory = DefaultAnnotationFactory().apply {
        setFactoryForAnnotationType(DefaultAnnotationFactory.TEXT_ANNOTATION, { _, _ ->
            TextAnnotation(context).apply {
                text = "Your text here"
                setIsEditable(true)

            }
        })
        setFactoryForAnnotationType(CustomAnnotationTypes.BUY_MARKER_ANNOTATION, { _, _ ->
            CustomAnnotation(context).apply {
                setContentView(ImageView(context).apply { setImageDrawable(resources.getDrawable(R.drawable.buy_marker_annotation)) })
            }
        })
        setFactoryForAnnotationType(CustomAnnotationTypes.SELL_MARKER_ANNOTATION, { _, _ ->
            CustomAnnotation(context).apply {
                setContentView(ImageView(context).apply { setImageDrawable(resources.getDrawable(R.drawable.sell_marker_annotation)) })
            }
        })
    }

    private val annotationCreationModifier: AnnotationCreationModifier = AnnotationCreationModifier().apply {
        yAxisId = mainAxisId
        isEnabled = false
        annotationFactory = this@BaseChartPaneViewModel.annotationFactory
        setAnnotationCreationListener(listener)
    }

    var isVisible: ObservableBoolean = ObservableBoolean(true)

    init {
        chartModifiers.addAll(arrayOf(
                ModifierGroup(ZoomPanModifier(), PinchZoomModifier(), ZoomExtentsModifier()),
                annotationCreationModifier
        ))
    }

    fun switchAnnotationCreationState(isEnabled: Boolean) {
        annotationCreationModifier.isEnabled = isEnabled
        chartModifiers[0].isEnabled = !isEnabled
    }

    fun changeAnnotationType(annotationType: Int) {
        annotationCreationModifier.annotationType = annotationType
    }
}