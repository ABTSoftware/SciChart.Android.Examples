//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// Style3DChartFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples3d.style3DChart;

import androidx.annotation.NonNull;

import com.scichart.charting3d.common.utils.FontUtil3D;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.axes.NumericAxis3D;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.fragments.base.ExampleSingleChart3DBaseFragment;

public class Style3DChartFragment extends ExampleSingleChart3DBaseFragment {

    @Override
    protected void initExample(@NonNull SciChartSurface3D surface3d) {
        final String font = "RobotoCondensed-BoldItalic";
        FontUtil3D.registerFont(String.format("/system/fonts/%s.ttf", font));

        final NumericAxis3D xAxis = sciChart3DBuilder.newNumericAxis3D()
                .withMinorsPerMajor(5)
                .withMaxAutoTicks(7)
                .withTextSize(13)
                .withTextColor(ColorUtil.Lime)
                .withTextFont(font)
                .withAxisBandsFill(ColorUtil.DarkOliveGreen)
                .withMajorTickLineStyle(ColorUtil.Lime, 1)
                .withMajorTickLineLength(8f)
                .withMinorTickLineStyle(ColorUtil.MediumVioletRed, 1)
                .withMajorTickLineLength(4f)
                .withMajorGridLineStyle(ColorUtil.Lime, 1)
                .withMinorGridLineStyle(ColorUtil.DarkViolet, 1)
                .build();
        final NumericAxis3D yAxis = sciChart3DBuilder.newNumericAxis3D()
                .withMinorsPerMajor(5)
                .withMaxAutoTicks(7)
                .withTextSize(13)
                .withTextColor(ColorUtil.Firebrick)
                .withTextFont(font)
                .withAxisBandsFill(ColorUtil.Tomato)
                .withMajorTickLineStyle(ColorUtil.Firebrick, 1)
                .withMajorTickLineLength(8f)
                .withMinorTickLineStyle(ColorUtil.IndianRed, 1)
                .withMajorTickLineLength(4f)
                .withMajorGridLineStyle(ColorUtil.DarkGreen, 1)
                .withMinorGridLineStyle(ColorUtil.DarkSkyBlue, 1)
                .build();
        final NumericAxis3D zAxis = sciChart3DBuilder.newNumericAxis3D()
                .withMinorsPerMajor(5)
                .withMaxAutoTicks(7)
                .withTextSize(13)
                .withTextColor(ColorUtil.PaleVioletRed)
                .withTextFont(font)
                .withAxisBandsFill(ColorUtil.GreenYellow)
                .withMajorTickLineStyle(ColorUtil.PaleVioletRed, 1)
                .withMajorTickLineLength(8f)
                .withMinorTickLineStyle(ColorUtil.Chartreuse, 1)
                .withMajorTickLineLength(4f)
                .withMajorGridLineStyle(ColorUtil.Beige, 1)
                .withMinorGridLineStyle(ColorUtil.Brown, 1)
                .build();

        UpdateSuspender.using(surface3d, () -> {
            surface3d.setBackgroundResource(R.drawable.example_custom_3d_chart_background);

            surface3d.setXAxis(xAxis);
            surface3d.setYAxis(yAxis);
            surface3d.setZAxis(zAxis);

            surface3d.getChartModifiers().add(sciChart3DBuilder.newModifierGroupWithDefaultModifiers().build());
        });
    }
}
