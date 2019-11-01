//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2019. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// Style3DChartFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.charts3d;

import com.scichart.charting3d.common.utils.FontUtil3D;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.axes.NumericAxis3D;
import com.scichart.charting3d.visuals.camera.Camera3D;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import butterknife.BindView;

public class Style3DChartFragment extends ExampleBaseFragment {
    @BindView(R.id.chart3d)
    SciChartSurface3D surface3d;

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart3d_fragment;
    }

    @Override
    protected void initExample() {
        final String font = "RobotoCondensed-BoldItalic";
        FontUtil3D.registerFont(String.format("/system/fonts/%s.ttf", font));

        final Camera3D camera = sciChart3DBuilder.newCamera3D().build();

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

        UpdateSuspender.using(surface3d, new Runnable() {
            @Override
            public void run() {
                surface3d.setBackgroundResource(R.drawable.example_custom_3d_chart_background);
                surface3d.setCamera(camera);

                surface3d.setXAxis(xAxis);
                surface3d.setYAxis(yAxis);
                surface3d.setZAxis(zAxis);

                surface3d.getChartModifiers().add(sciChart3DBuilder.newModifierGroupWithDefaultModifiers().build());
            }
        });
    }
}
