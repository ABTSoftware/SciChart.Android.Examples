//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2019. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SurfaceMeshWithMetadataProvider3DChartFragment.java is part of the SCICHART® Examples. Permission is hereby granted
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
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.ISurfaceMeshMetadataProvider3D;
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.MetadataProvider3DBase;
import com.scichart.charting3d.visuals.renderableSeries.surfaceMesh.SurfaceMeshRenderPassData3D;
import com.scichart.charting3d.visuals.renderableSeries.surfaceMesh.SurfaceMeshRenderableSeries3D;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.model.IntegerValues;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

import static com.scichart.drawing.utility.ColorUtil.Blue;
import static com.scichart.drawing.utility.ColorUtil.CadetBlue;
import static com.scichart.drawing.utility.ColorUtil.Cyan;
import static com.scichart.drawing.utility.ColorUtil.DarkBlue;
import static com.scichart.drawing.utility.ColorUtil.DarkRed;
import static com.scichart.drawing.utility.ColorUtil.GreenYellow;
import static com.scichart.drawing.utility.ColorUtil.IndianRed;
import static com.scichart.drawing.utility.ColorUtil.LimeGreen;
import static com.scichart.drawing.utility.ColorUtil.Red;
import static com.scichart.drawing.utility.ColorUtil.Tomato;
import static com.scichart.drawing.utility.ColorUtil.Yellow;

public class SurfaceMeshWithMetadataProvider3DChartFragment extends ExampleBaseFragment {
    private static final int X_SIZE = 49, Z_SIZE = 49;

    @BindView(R.id.chart3d)
    SciChartSurface3D surface3d;

    private final UniformGridDataSeries3D<Double, Double, Double> meshDataSeries0 = new UniformGridDataSeries3D<>(Double.class, Double.class, Double.class, X_SIZE, Z_SIZE);
    private final UniformGridDataSeries3D<Double, Double, Double> meshDataSeries1 = new UniformGridDataSeries3D<>(Double.class, Double.class, Double.class, X_SIZE, Z_SIZE);

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> schedule;

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart3d_fragment;
    }

    @Override
    protected void initExample() {
        final Camera3D camera = sciChart3DBuilder.newCamera3D().build();

        final NumericAxis3D xAxis = sciChart3DBuilder.newNumericAxis3D().withDrawMajorBands(false).withDrawLabels(false).withDrawMajorGridLines(false).withDrawMajorTicks(false).withDrawMinorGridLines(false).withDrawMinorTicks(false).withPlaneBoderThickness(0f).build();
        final NumericAxis3D yAxis = sciChart3DBuilder.newNumericAxis3D().withDrawMajorBands(false).withDrawLabels(false).withDrawMajorGridLines(false).withDrawMajorTicks(false).withDrawMinorGridLines(false).withDrawMinorTicks(false).withPlaneBoderThickness(0f).build();
        final NumericAxis3D zAxis = sciChart3DBuilder.newNumericAxis3D().withDrawMajorBands(false).withDrawLabels(false).withDrawMajorGridLines(false).withDrawMajorTicks(false).withDrawMinorGridLines(false).withDrawMinorTicks(false).withPlaneBoderThickness(0f).build();

        for (int x = 48; x >= 24; x--) {
            final double y = Math.pow(x - 23.7, 0.3);
            final double y2 = Math.pow(49.5 - x, 0.3);

            meshDataSeries0.updateYAt(x, 24, y);
            meshDataSeries1.updateYAt(x, 24, y2 + 1.505);
        }

        for (int x = 24; x >= 0; x--) {
            for (int z = 49; z > 25; z--) {
                final double y = Math.pow(z - 23.7, 0.3);
                final double y2 = Math.pow(50.5 - z, 0.3) + 1.505;

                meshDataSeries0.updateYAt(x + 24, 49 - z, y);
                meshDataSeries0.updateYAt(z - 1, 24 - x, y);

                meshDataSeries1.updateYAt(x + 24, 49 - z, y2);
                meshDataSeries1.updateYAt(z - 1, 24 - x, y2);

                meshDataSeries0.updateYAt(24 - x, 49 - z, y);
                meshDataSeries0.updateYAt(49 - z, 24 - x, y);

                meshDataSeries1.updateYAt(24 - x, 49 - z, y2);
                meshDataSeries1.updateYAt(49 - z, 24 - x, y2);

                meshDataSeries0.updateYAt(x + 24, z - 1, y);
                meshDataSeries0.updateYAt(z - 1, x + 24, y);

                meshDataSeries1.updateYAt(x + 24, z - 1, y2);
                meshDataSeries1.updateYAt(z - 1, x + 24, y2);

                meshDataSeries0.updateYAt(24 - x, z - 1, y);
                meshDataSeries0.updateYAt(49 - z, x + 24, y);

                meshDataSeries1.updateYAt(24 - x, z - 1, y2);
                meshDataSeries1.updateYAt(49 - z, x + 24, y2);
            }
        }

        final int[] colors = new int[]{DarkBlue, Blue, CadetBlue, Cyan, LimeGreen, GreenYellow, Yellow, Tomato, IndianRed, Red, DarkRed};
        final float[] stops = new float[]{0f, .1f, .2f, .3f, .4f, .5f, .6f, .7f, .8f, .9f, 1f};

        final SurfaceMeshRenderableSeries3D rs0 = sciChart3DBuilder.newSurfaceMeshSeries3D()
                .withDataSeries(meshDataSeries0)
                .withDrawMeshAs(DrawMeshAs.SolidMesh)
                .withDrawSkirt(false)
                .withMeshColorPalette(new GradientColorPalette(colors, stops))
                .withMetadataProvider(new SurfaceMeshMetadataProvider3D())
                .build();

        final SurfaceMeshRenderableSeries3D rs1 = sciChart3DBuilder.newSurfaceMeshSeries3D()
                .withDataSeries(meshDataSeries1)
                .withDrawMeshAs(DrawMeshAs.SolidMesh)
                .withDrawSkirt(false)
                .withMeshColorPalette(new GradientColorPalette(colors, stops))
                .withMetadataProvider(new SurfaceMeshMetadataProvider3D())
                .build();

        UpdateSuspender.using(surface3d, new Runnable() {
            @Override
            public void run() {
                surface3d.setCamera(camera);

                surface3d.setXAxis(xAxis);
                surface3d.setYAxis(yAxis);
                surface3d.setZAxis(zAxis);

                Collections.addAll(surface3d.getRenderableSeries(), rs0, rs1);

                surface3d.getChartModifiers().add(sciChart3DBuilder.newModifierGroupWithDefaultModifiers().build());
            }
        });

        schedule = scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                UpdateSuspender.using(surface3d, new Runnable() {
                    @Override
                    public void run() {
                        rs0.invalidateMetadata();
                        rs1.invalidateMetadata();
                    }
                });
            }
        }, 0, 10, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (schedule != null)
            schedule.cancel(true);
    }

    private static class SurfaceMeshMetadataProvider3D extends MetadataProvider3DBase<SurfaceMeshRenderableSeries3D> implements ISurfaceMeshMetadataProvider3D {
        SurfaceMeshMetadataProvider3D() {
            super(SurfaceMeshRenderableSeries3D.class);
        }

        @Override
        public void updateMeshColors(IntegerValues cellColors) {
            final SurfaceMeshRenderPassData3D currentRenderPassData = (SurfaceMeshRenderPassData3D) renderableSeries.getCurrentRenderPassData();

            final DataManager dataManager = DataManager.getInstance();

            final int countX = currentRenderPassData.countX - 1;
            final int countZ = currentRenderPassData.countZ - 1;

            cellColors.setSize(currentRenderPassData.getPointsCount());

            final int[] items = cellColors.getItemsArray();
            for (int x = 0; x < countX; x++) {
                for (int z = 0; z < countZ; z++) {
                    final int index = x * countZ + z;

                    final int color;
                    if ((x >= 20 && x <= 26 && z > 0 && z < 47) || (z >= 20 && z <= 26 && x > 0 && x < 47)) {
                        // need to use special transparent color definition for MetadataProvider3D for it to work
                        color = TRANSPARENT;
                    } else {
                        color = dataManager.getRandomColor();
                    }

                    items[index] = color;
                }
            }
        }
    }
}
