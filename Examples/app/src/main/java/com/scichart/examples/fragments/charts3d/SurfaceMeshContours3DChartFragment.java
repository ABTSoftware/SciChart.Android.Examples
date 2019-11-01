//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2019. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SurfaceMeshContours3DChartFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.charts3d;

import com.scichart.charting3d.model.dataSeries.grid.UniformGridDataSeries3D;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.axes.NumericAxis3D;
import com.scichart.charting3d.visuals.camera.Camera3D;
import com.scichart.charting3d.visuals.renderableSeries.data.DrawMeshAs;
import com.scichart.charting3d.visuals.renderableSeries.data.GradientColorPalette;
import com.scichart.charting3d.visuals.renderableSeries.surfaceMesh.SurfaceMeshRenderableSeries3D;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.R;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import butterknife.BindView;

import static com.scichart.drawing.utility.ColorUtil.Aqua;
import static com.scichart.drawing.utility.ColorUtil.Brown;
import static com.scichart.drawing.utility.ColorUtil.BurlyWood;
import static com.scichart.drawing.utility.ColorUtil.DarkKhaki;
import static com.scichart.drawing.utility.ColorUtil.DarkOrange;
import static com.scichart.drawing.utility.ColorUtil.DarkSalmon;
import static com.scichart.drawing.utility.ColorUtil.ForestGreen;
import static com.scichart.drawing.utility.ColorUtil.Green;
import static com.scichart.drawing.utility.ColorUtil.GreenYellow;
import static com.scichart.drawing.utility.ColorUtil.SaddleBrown;

public class SurfaceMeshContours3DChartFragment extends ExampleBaseFragment {
    @BindView(R.id.chart3d)
    SciChartSurface3D surface3d;

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart3d_fragment;
    }

    @Override
    protected void initExample() {
        final Camera3D camera = sciChart3DBuilder.newCamera3D().withPosition(-1300, 1300, -1300).build();

        final NumericAxis3D xAxis = sciChart3DBuilder.newNumericAxis3D().build();
        final NumericAxis3D yAxis = sciChart3DBuilder.newNumericAxis3D().build();
        final NumericAxis3D zAxis = sciChart3DBuilder.newNumericAxis3D().build();

        final int w = 64;
        final int h = 64;

        double ratio =  200.0 / 64.0;

        final UniformGridDataSeries3D<Double, Double, Double> ds = new UniformGridDataSeries3D<>(Double.class, Double.class, Double.class, w, h);
        ds.setStepX(0.01);
        ds.setStepZ(0.01);

        for (int x = 0; x < w; x++) {
            for (int z = 0; z < h; z++) {
                final double v = (1 + Math.sin(x * 0.04 * ratio)) * 50 + (1 + Math.sin(z * 0.1* ratio)) * 50;
                final double cx = w / 2d;
                final double cy = h / 2d;
                final double r = Math.sqrt((x - cx) * (x - cx) + (z - cy) * (z - cy))* ratio;
                final double exp = Math.max(0, 1 - r * 0.008);
                final double zValue = v * exp;

                ds.updateYAt(x, z, zValue);
            }
        }

        final int[] colors = new int[]{Aqua, Green, ForestGreen, DarkKhaki, BurlyWood, DarkSalmon, GreenYellow, DarkOrange, SaddleBrown, Brown, Brown};
        final float[] stops = new float[]{0, .1f, .2f, .3f, .4f, .5f, .6f, .7f, .8f, .9f, 1};

        final SurfaceMeshRenderableSeries3D rs = sciChart3DBuilder.newSurfaceMeshSeries3D()
                .withDataSeries(ds)
                .withDrawMeshAs(DrawMeshAs.SolidWithContours)
                .withContourStrokeThickness(2f)
                .withStroke(0x77228B22)
                .withMaximum(150)
                .withStrokeThicknes(1f)
                .withDrawSkirt(true)
                .withMeshColorPalette(new GradientColorPalette(colors, stops))
                .withOpacity(0.8f)
                .build();

        UpdateSuspender.using(surface3d, new Runnable() {
            @Override
            public void run() {
                surface3d.getWorldDimensions().assign(600, 300, 300);

                surface3d.setCamera(camera);

                surface3d.setXAxis(xAxis);
                surface3d.setYAxis(yAxis);
                surface3d.setZAxis(zAxis);

                surface3d.getRenderableSeries().add(rs);

                surface3d.getChartModifiers().add(sciChart3DBuilder.newModifierGroupWithDefaultModifiers().build());
            }
        });
    }
}