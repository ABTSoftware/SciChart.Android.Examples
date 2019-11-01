//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2019. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SurfaceMeshFloorAndCeiling3DChartFragment.java is part of the SCICHART® Examples. Permission is hereby granted
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

import java.util.Collections;

import butterknife.BindView;

import static com.scichart.drawing.utility.ColorUtil.Blue;
import static com.scichart.drawing.utility.ColorUtil.Cyan;
import static com.scichart.drawing.utility.ColorUtil.DarkRed;
import static com.scichart.drawing.utility.ColorUtil.GreenYellow;
import static com.scichart.drawing.utility.ColorUtil.Red;
import static com.scichart.drawing.utility.ColorUtil.Yellow;

public class SurfaceMeshFloorAndCeiling3DChartFragment extends ExampleBaseFragment {
    @BindView(R.id.chart3d)
    SciChartSurface3D surface3d;

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart3d_fragment;
    }


    @Override
    protected void initExample() {
        final Camera3D camera = sciChart3DBuilder.newCamera3D().withPosition(-1300, 1300, -1300).build();

        final NumericAxis3D xAxis = sciChart3DBuilder.newNumericAxis3D().withMaxAutoTicks(7).build();
        final NumericAxis3D yAxis = sciChart3DBuilder.newNumericAxis3D().withVisibleRange(-4d, 4d).build();
        final NumericAxis3D zAxis = sciChart3DBuilder.newNumericAxis3D().build();

        final int xSize = 11;
        final int zSize = 4;

        final UniformGridDataSeries3D<Double, Double, Double> ds = new UniformGridDataSeries3D<>(Double.class, Double.class, Double.class, xSize, zSize);
        ds.setStartX(0d);
        ds.setStepX(0.09);
        ds.setStartZ(0d);
        ds.setStepZ(0.75);

        final double[][] data = new double[][]{
                {-1.43, -2.95, -2.97, -1.81, -1.33, -1.53, -2.04, 2.08, 1.94, 1.42, 1.58},
                {1.77, 1.76, -1.1, -0.26, 0.72, 0.64, 3.26, 3.2, 3.1, 1.94, 1.54},
                {0, 0, 0, 0, 0, 3.7, 3.7, 3.7, 3.7, -0.48, -0.48},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        };

        for (int z = 0; z < zSize; z++) {
            for (int x = 0; x < xSize; x++) {
                ds.updateYAt(x, z, data[z][x]);
            }
        }

        final int[] colors = new int[] {0xFF1D2C6B, Blue, Cyan, GreenYellow, Yellow, Red, DarkRed};
        final float[] stops = new float[]{0, .1f, .2f, .4f, .6f, .8f, 1};

        final SurfaceMeshRenderableSeries3D rs0 = sciChart3DBuilder.newSurfaceMeshSeries3D()
                .withDataSeries(ds)
                .withHeightScaleFactor(0f)
                .withDrawMeshAs(DrawMeshAs.SolidWireframe)
                .withStroke(0xFF228B22)
                .withStrokeThicknes(1f)
                .withMaximum(4)
                .withMeshColorPalette(new GradientColorPalette(colors, stops))
                .withOpacity(0.7f)
                .build();

        final SurfaceMeshRenderableSeries3D rs1 = sciChart3DBuilder.newSurfaceMeshSeries3D()
                .withDataSeries(ds)
                .withDrawMeshAs(DrawMeshAs.SolidWireframe)
                .withStroke(0xFF228B22)
                .withStrokeThicknes(1f)
                .withMaximum(4)
                .withDrawSkirt(false)
                .withMeshColorPalette(new GradientColorPalette(colors, stops))
                .withOpacity(0.9f)
                .build();

        final SurfaceMeshRenderableSeries3D rs2 = sciChart3DBuilder.newSurfaceMeshSeries3D()
                .withDataSeries(ds)
                .withHeightScaleFactor(0f)
                .withDrawMeshAs(DrawMeshAs.SolidWireframe)
                .withStroke(0xFF228B22)
                .withStrokeThicknes(1f)
                .withMaximum(4)
                .withYOffset(400)
                .withMeshColorPalette(new GradientColorPalette(colors, stops))
                .withOpacity(0.7f)
                .build();

        UpdateSuspender.using(surface3d, new Runnable() {
            @Override
            public void run() {
                surface3d.getWorldDimensions().assign(1100, 400, 400);

                surface3d.setCamera(camera);

                surface3d.setXAxis(xAxis);
                surface3d.setYAxis(yAxis);
                surface3d.setZAxis(zAxis);

                Collections.addAll(surface3d.getRenderableSeries(), rs0, rs1, rs2);

                surface3d.getChartModifiers().add(sciChart3DBuilder.newModifierGroup()
                        .withPinchZoomModifier3D().build()
                        .withOrbitModifier3D().withReceiveHandledEvents(true).build()
                        .withZoomExtentsModifier3D().withResetPosition(-1300, 1300, -1300).build()
                        .build());
            }
        });
    }
}
