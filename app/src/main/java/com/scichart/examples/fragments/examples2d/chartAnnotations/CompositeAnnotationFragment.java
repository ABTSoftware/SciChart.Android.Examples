//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2026. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CompositeAnnotationFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.chartAnnotations;

import androidx.annotation.NonNull;

import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.annotations.CompositeAnnotation;
import com.scichart.charting.visuals.annotations.IAnnotation;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;

import java.util.Collections;

/**
 * Demonstrates the use of {@link CompositeAnnotation} which groups multiple child annotations together
 * so they can be manipulated as a single unit.
 */
public class CompositeAnnotationFragment extends ExampleSingleChartBaseFragment {

    @Override
    public boolean showDefaultModifiersInToolbar() {
        return false;
    }

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        surface.setTheme(R.style.SciChart_NavyBlue);

        UpdateSuspender.using(surface, () -> {
            final IAxis xAxis = sciChartBuilder.newNumericAxis()
                    .withVisibleRange(0d, 10d)
                    .withGrowBy(0.1d, 0.1d)
                    .withTextFormatting("0.0#")
                    .build();

            final IAxis yAxis = sciChartBuilder.newNumericAxis()
                    .withVisibleRange(0d, 10d)
                    .withGrowBy(0.1d, 0.1d)
                    .withTextFormatting("0.0#")
                    .build();

            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);

            // Create a composite annotation that groups multiple annotations together
            final CompositeAnnotation compositeAnnotation = new CompositeAnnotation(surface.getContext());
            compositeAnnotation.setX1(2.0);
            compositeAnnotation.setY1(2.0);
            compositeAnnotation.setX2(6.0);
            compositeAnnotation.setY2(6.0);
            compositeAnnotation.setIsEditable(true);
            compositeAnnotation.setBackgroundColor(ColorUtil.argb(0x33, 0xFF, 0x68, 0xBC));

            // Create child annotations that will be grouped together
            // Child annotations use relative positions (0.0-1.0) relative to the composite annotation
            // 0.0, 0.0 = top-left corner, 1.0, 1.0 = bottom-right corner, 0.5, 0.5 = center
            
            final IAnnotation textAnnotation1 = sciChartBuilder.newTextAnnotation()
                    .withX1(0.25)  // 25% from left edge
                    .withY1(0.75)  // 75% from top edge
                    .withText("Text 1\n(0.25, 0.75)")
                    .withFontStyle(14, ColorUtil.White)
                    .build();

            final IAnnotation textAnnotation2 = sciChartBuilder.newTextAnnotation()
                    .withX1(0.75)  // 75% from left edge
                    .withY1(0.25)  // 25% from top edge
                    .withText("Text 2\n(0.75, 0.25)")
                    .withFontStyle(14, ColorUtil.White)
                    .build();

            final IAnnotation textAnnotationCenter = sciChartBuilder.newTextAnnotation()
                    .withX1(0.5)   // Center horizontally
                    .withY1(0.5)   // Center vertically
                    .withText("Center\n(0.5, 0.5)")
                    .withFontStyle(14, ColorUtil.White)
                    .build();

            final IAnnotation lineAnnotation = sciChartBuilder.newLineAnnotation()
                    .withPosition(0.2, 0.2, 0.8, 0.8)  // Line from 20% to 80% of composite bounds
                    .withStroke(2f, ColorUtil.White)
                    .build();

            // Add child annotations to the composite
            compositeAnnotation.getAnnotations().add(textAnnotation1);
            compositeAnnotation.getAnnotations().add(textAnnotation2);
            compositeAnnotation.getAnnotations().add(textAnnotationCenter);
            compositeAnnotation.getAnnotations().add(lineAnnotation);

            // Add the composite annotation to the surface
            Collections.addAll(surface.getAnnotations(), compositeAnnotation);

            // Add a regular annotation for comparison
            Collections.addAll(surface.getAnnotations(),
                    sciChartBuilder.newTextAnnotation()
                            .withX1(7.0)
                            .withY1(7.0)
                            .withText("Regular Annotation\n(not grouped)")
                            .build());

            surface.getChartModifiers().add(sciChartBuilder.newModifierGroupWithDefaultModifiers().build());
        });
    }
}
