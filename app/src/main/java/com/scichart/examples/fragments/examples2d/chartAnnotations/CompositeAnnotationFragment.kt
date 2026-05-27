//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2026. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CompositeAnnotationFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.annotations.CompositeAnnotation
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.utility.ColorUtil
import com.scichart.examples.R
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment
import com.scichart.examples.utils.scichartExtensions.annotation
import com.scichart.examples.utils.scichartExtensions.annotations
import com.scichart.examples.utils.scichartExtensions.numericAxis
import com.scichart.examples.utils.scichartExtensions.suspendUpdates
import com.scichart.examples.utils.scichartExtensions.textAnnotation
import com.scichart.examples.utils.scichartExtensions.xAxes
import com.scichart.examples.utils.scichartExtensions.yAxes

/**
 * Demonstrates the use of {@link CompositeAnnotation} which groups multiple child annotations together
 * so they can be manipulated as a single unit.
 */
class CompositeAnnotationFragment : ExampleSingleChartBaseFragment() {

    override fun showDefaultModifiersInToolbar(): Boolean = false

    override fun initExample(surface: SciChartSurface) {
        surface.theme = R.style.SciChart_NavyBlue

        surface.suspendUpdates {
            xAxes { numericAxis {
                visibleRange = DoubleRange(0.0, 10.0)
                growBy = DoubleRange(0.1, 0.1)
                textFormatting = "0.0#"
            }}
            yAxes { numericAxis {
                visibleRange = DoubleRange(0.0, 10.0)
                growBy = DoubleRange(0.1, 0.1)
                textFormatting = "0.0#"
            }}

            // Create a composite annotation that groups multiple annotations together
            val compositeAnnotation = CompositeAnnotation(surface.context).apply {
                x1 = 2.0
                y1 = 2.0
                x2 = 6.0
                y2 = 6.0
                setIsEditable(true)
                setBackgroundColor(ColorUtil.argb(0x33, 0xFF, 0x68, 0xBC))
            }

            // Create child annotations that will be grouped together
            // Child annotations use relative positions (0.0-1.0) relative to the composite annotation
            // 0.0, 0.0 = top-left corner, 1.0, 1.0 = bottom-right corner, 0.5, 0.5 = center
            
            val textAnnotation1 = sciChartBuilder.newTextAnnotation()
                    .withX1(0.25)  // 25% from left edge
                    .withY1(0.75)  // 75% from top edge
                    .withText("Text 1\n(0.25, 0.75)")
                    .withFontStyle(14f, ColorUtil.White)
                    .build()

            val textAnnotation2 = sciChartBuilder.newTextAnnotation()
                    .withX1(0.75)  // 75% from left edge
                    .withY1(0.25)  // 25% from top edge
                    .withText("Text 2\n(0.75, 0.25)")
                    .withFontStyle(14f, ColorUtil.White)
                    .build()

            val textAnnotationCenter = sciChartBuilder.newTextAnnotation()
                    .withX1(0.5)   // Center horizontally
                    .withY1(0.5)   // Center vertically
                    .withText("Center\n(0.5, 0.5)")
                    .withFontStyle(14f, ColorUtil.White)
                    .build()

            val lineAnnotation = sciChartBuilder.newLineAnnotation()
                    .withPosition(0.2, 0.2, 0.8, 0.8)  // Line from 20% to 80% of composite bounds
                    .withStroke(2f, ColorUtil.White)
                    .build()

            // Add child annotations to the composite
            compositeAnnotation.annotations.add(textAnnotation1)
            compositeAnnotation.annotations.add(textAnnotation2)
            compositeAnnotation.annotations.add(textAnnotationCenter)
            compositeAnnotation.annotations.add(lineAnnotation)

            // Add the composite annotation to the surface
            annotations {
                annotation(compositeAnnotation)

                // Add a regular annotation for comparison
                textAnnotation {
                    x1 = 7.0; y1 = 7.0
                    text = "Regular Annotation\n(not grouped)"
                }
            }

            chartModifiers.add(sciChartBuilder.newModifierGroupWithDefaultModifiers().build())
        }
    }
}
