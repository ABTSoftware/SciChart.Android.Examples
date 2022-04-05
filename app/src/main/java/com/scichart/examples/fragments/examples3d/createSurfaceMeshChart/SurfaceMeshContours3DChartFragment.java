//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SurfaceMeshContours3DChartFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples3d.createSurfaceMeshChart;

import com.scichart.charting3d.model.dataSeries.grid.UniformGridDataSeries3D;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.renderableSeries.data.DrawMeshAs;
import com.scichart.charting3d.visuals.renderableSeries.data.GradientColorPalette;
import com.scichart.charting3d.visuals.renderableSeries.surfaceMesh.SurfaceMeshRenderableSeries3D;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.fragments.base.ExampleSingleChart3DBaseFragment;

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

import androidx.annotation.NonNull;

public class SurfaceMeshContours3DChartFragment extends ExampleSingleChart3DBaseFragment {

    @Override
    protected void initExample(@NonNull SciChartSurface3D surface3d) {
        final int w = 64;
        final int h = 64;
        final double ratio =  200.0 / 64.0;

        final UniformGridDataSeries3D<Double, Double, Double> ds = new UniformGridDataSeries3D<>(Double.class, Double.class, Double.class, w, h);
        ds.setStepX(0.01);
        ds.setStepZ(0.01);

        for (int x = 0; x < w; x++) {
            for (int z = 0; z < h; z++) {
                final double v = (1 + Math.sin(x * 0.04 * ratio)) * 50 + (1 + Math.sin(z * 0.1 * ratio)) * 50;
                final double cx = w / 2d;
                final double cy = h / 2d;
                final double r = Math.sqrt((x - cx) * (x - cx) + (z - cy) * (z - cy)) * ratio;
                final double exp = Math.max(0, 1 - r * 0.008);
                final double zValue = v * exp;

                ds.updateYAt(x, z, zValue);
            }
        }

        final SurfaceMeshRenderableSeries3D rs = sciChart3DBuilder.newSurfaceMeshSeries3D()
                .withDataSeries(ds)
                .withDrawMeshAs(DrawMeshAs.SolidWithContours)
                .withContourStrokeThickness(2f)
                .withStroke(0x77228B22)
                .withMaximum(150)
                .withStrokeThicknes(1f)
                .withDrawSkirt(true)
                .withMeshColorPalette(new GradientColorPalette(
                        new int[]{Aqua, Green, ForestGreen, DarkKhaki, BurlyWood, DarkSalmon, GreenYellow, DarkOrange, SaddleBrown, Brown, Brown},
                        new float[]{0, .1f, .2f, .3f, .4f, .5f, .6f, .7f, .8f, .9f, 1})
                )
                .withOpacity(0.8f)
                .build();

        UpdateSuspender.using(surface3d, () -> {
            surface3d.setXAxis(sciChart3DBuilder.newNumericAxis3D().build());
            surface3d.setYAxis(sciChart3DBuilder.newNumericAxis3D().build());
            surface3d.setZAxis(sciChart3DBuilder.newNumericAxis3D().build());
            surface3d.getRenderableSeries().add(rs);
            surface3d.getChartModifiers().add(sciChart3DBuilder.newModifierGroupWithDefaultModifiers().build());

            surface3d.getCamera().getPosition().assign(-1300, 1300, -1300);
            surface3d.getWorldDimensions().assign(600, 300, 300);
        });
    }
}