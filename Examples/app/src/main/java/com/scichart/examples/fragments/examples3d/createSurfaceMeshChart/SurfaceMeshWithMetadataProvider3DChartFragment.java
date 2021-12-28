//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SurfaceMeshWithMetadataProvider3DChartFragment.java is part of SCICHART®, High Performance Scientific Charts
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

import androidx.annotation.NonNull;

import com.scichart.charting3d.model.dataSeries.grid.UniformGridDataSeries3D;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.axes.NumericAxis3D;
import com.scichart.charting3d.visuals.renderableSeries.data.DrawMeshAs;
import com.scichart.charting3d.visuals.renderableSeries.data.GradientColorPalette;
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.ISurfaceMeshMetadataProvider3D;
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.MetadataProvider3DBase;
import com.scichart.charting3d.visuals.renderableSeries.surfaceMesh.SurfaceMeshRenderPassData3D;
import com.scichart.charting3d.visuals.renderableSeries.surfaceMesh.SurfaceMeshRenderableSeries3D;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.model.IntegerValues;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.fragments.base.ExampleSingleChart3DBaseFragment;

import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SurfaceMeshWithMetadataProvider3DChartFragment extends ExampleSingleChart3DBaseFragment {
    private static final int X_SIZE = 49, Z_SIZE = 49;

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> schedule;

    @Override
    protected void initExample(@NonNull SciChartSurface3D surface3d) {
        final UniformGridDataSeries3D<Double, Double, Double> meshDataSeries0 = new UniformGridDataSeries3D<>(Double.class, Double.class, Double.class, X_SIZE, Z_SIZE);
        final UniformGridDataSeries3D<Double, Double, Double> meshDataSeries1 = new UniformGridDataSeries3D<>(Double.class, Double.class, Double.class, X_SIZE, Z_SIZE);
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

        final GradientColorPalette palette = new GradientColorPalette(
                new int[]{DarkBlue, Blue, CadetBlue, Cyan, LimeGreen, GreenYellow, Yellow, Tomato, IndianRed, Red, DarkRed},
                new float[]{0f, .1f, .2f, .3f, .4f, .5f, .6f, .7f, .8f, .9f, 1f}
        );

        final SurfaceMeshRenderableSeries3D rs0 = sciChart3DBuilder.newSurfaceMeshSeries3D()
                .withDataSeries(meshDataSeries0)
                .withDrawMeshAs(DrawMeshAs.SolidMesh)
                .withDrawSkirt(false)
                .withMeshColorPalette(palette)
                .withMetadataProvider(new SurfaceMeshMetadataProvider3D())
                .build();

        final SurfaceMeshRenderableSeries3D rs1 = sciChart3DBuilder.newSurfaceMeshSeries3D()
                .withDataSeries(meshDataSeries1)
                .withDrawMeshAs(DrawMeshAs.SolidMesh)
                .withDrawSkirt(false)
                .withMeshColorPalette(palette)
                .withMetadataProvider(new SurfaceMeshMetadataProvider3D())
                .build();

        UpdateSuspender.using(surface3d, () -> {
            surface3d.setXAxis(createNumericAxis3D());
            surface3d.setYAxis(createNumericAxis3D());
            surface3d.setZAxis(createNumericAxis3D());
            Collections.addAll(surface3d.getRenderableSeries(), rs0, rs1);
            surface3d.getChartModifiers().add(sciChart3DBuilder.newModifierGroupWithDefaultModifiers().build());
        });

        schedule = scheduledExecutorService.scheduleWithFixedDelay(() -> UpdateSuspender.using(surface3d, () -> {
            rs0.invalidateMetadata();
            rs1.invalidateMetadata();
        }), 0, 10, TimeUnit.MILLISECONDS);
    }

    private NumericAxis3D createNumericAxis3D() {
        return sciChart3DBuilder.newNumericAxis3D()
                .withDrawMajorBands(false)
                .withDrawLabels(false)
                .withDrawMajorGridLines(false)
                .withDrawMajorTicks(false)
                .withDrawMinorGridLines(false)
                .withDrawMinorTicks(false)
                .withPlaneBoderThickness(0f)
                .build();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (schedule != null) {
            schedule.cancel(true);
        }
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
