//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2026. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// MultipleCharts3DFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples3d.axis3D;

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

import android.view.View;

import androidx.annotation.NonNull;

import com.scichart.charting3d.model.dataSeries.grid.UniformGridDataSeries3D;
import com.scichart.charting3d.model.dataSeries.xyz.XyzDataSeries3D;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.axes.NumericAxis3D;
import com.scichart.charting3d.visuals.pointMarkers.SpherePointMarker3D;
import com.scichart.charting3d.visuals.renderableSeries.data.DrawMeshAs;
import com.scichart.charting3d.visuals.renderableSeries.data.GradientColorPalette;
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.PointMetadataProvider3D;
import com.scichart.charting3d.visuals.renderableSeries.pointLine.PointLineRenderableSeries3D;
import com.scichart.charting3d.visuals.renderableSeries.surfaceMesh.SurfaceMeshRenderableSeries3D;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.fragments.base.ExampleSingleChart3DBaseFragment;

import java.util.List;

public class MultipleCharts3DFragment extends ExampleSingleChart3DBaseFragment {

    @Override
    protected void initExample(@NonNull SciChartSurface3D surface3d) {
        final DataManager dataManager = DataManager.getInstance();

        final NumericAxis3D xAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(.1, .1).build();
        final NumericAxis3D yAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(.1, .1).build();
        final NumericAxis3D zAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(.1, .1).build();

        final XyzDataSeries3D<Double, Double, Double> xyzDataSeries3D = new XyzDataSeries3D<>(Double.class, Double.class, Double.class);
        final PointMetadataProvider3D pointMetadataProvider3D = new PointMetadataProvider3D();

        final List<PointMetadataProvider3D.PointMetadata3D> metadata = pointMetadataProvider3D.metadata;
        for (int i = 0; i < 100; i++) {
            final double x = 5 * Math.sin(i);
            final double y = i;
            final double z = 5 * Math.cos(i);
            xyzDataSeries3D.append(x, y, z);

            metadata.add(new PointMetadataProvider3D.PointMetadata3D(dataManager.getRandomColor(), dataManager.getRandomScale()));
        }

        final SpherePointMarker3D pointMarker = sciChart3DBuilder.newSpherePointMarker3D()
                .withFill(ColorUtil.Red)
                .withSize(5f)
                .build();

        final PointLineRenderableSeries3D pointLineRenderableSeries3D = sciChart3DBuilder.newPointLinesSeries3D()
                .withDataSeries(xyzDataSeries3D)
                .withPointMarker(pointMarker)
                .withStroke(ColorUtil.Green)
                .withStrokeThicknes(2f)
                .withIsAntialiased(false)
                .withIsLineStrips(true)
                .withMetadataProvider(pointMetadataProvider3D)
                .build();

        UpdateSuspender.using(surface3d, () -> {
            surface3d.setXAxis(xAxis);
            surface3d.setYAxis(yAxis);
            surface3d.setZAxis(zAxis);

            surface3d.getRenderableSeries().add(pointLineRenderableSeries3D );

            surface3d.getChartModifiers().add(sciChart3DBuilder.newModifierGroupWithDefaultModifiers().build());
        });

        // Second surface
        SciChartSurface3D surface3d2 = binding.surface3d2;
        surface3d2.setTheme(R.style.SciChart_NavyBlue);
        surface3d2.setVisibility(View.VISIBLE);
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

        final SurfaceMeshRenderableSeries3D surfaceMeshRenderableSeries3D = sciChart3DBuilder.newSurfaceMeshSeries3D()
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

        UpdateSuspender.using(surface3d2, () -> {
            surface3d2.setXAxis(sciChart3DBuilder.newNumericAxis3D().build());
            surface3d2.setYAxis(sciChart3DBuilder.newNumericAxis3D().build());
            surface3d2.setZAxis(sciChart3DBuilder.newNumericAxis3D().build());
            surface3d2.getRenderableSeries().add(surfaceMeshRenderableSeries3D);
            surface3d2.getChartModifiers().add(sciChart3DBuilder.newModifierGroupWithDefaultModifiers().build());

            surface3d2.getCamera().getPosition().assign(-1300, 1300, -1300);
            surface3d2.getWorldDimensions().assign(600, 300, 300);
        });
    }
}
