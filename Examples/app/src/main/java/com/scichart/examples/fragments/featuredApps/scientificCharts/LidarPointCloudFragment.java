//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// LidarPointCloudFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.featuredApps.scientificCharts;

import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.scichart.charting.visuals.renderableSeries.ColorMap;
import com.scichart.charting3d.model.dataSeries.grid.UniformGridDataSeries3D;
import com.scichart.charting3d.model.dataSeries.xyz.XyzDataSeries3D;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.axes.NumericAxis3D;
import com.scichart.charting3d.visuals.renderableSeries.data.DrawMeshAs;
import com.scichart.charting3d.visuals.renderableSeries.data.GradientColorPalette;
import com.scichart.charting3d.visuals.renderableSeries.data.MeshPaletteMode;
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.PointMetadataProvider3D;
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.PointMetadataProvider3D.PointMetadata3D;
import com.scichart.charting3d.visuals.renderableSeries.scatter.ScatterRenderableSeries3D;
import com.scichart.charting3d.visuals.renderableSeries.surfaceMesh.SurfaceMeshRenderableSeries3D;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.model.IntegerValues;
import com.scichart.examples.data.AscData;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.databinding.ExampleSingleChart3dFragmentBinding;
import com.scichart.examples.fragments.base.ShowcaseExampleBaseFragment;

import java.util.Collections;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static com.scichart.drawing.utility.ColorUtil.Orange;
import static com.scichart.drawing.utility.ColorUtil.Purple;
import static com.scichart.drawing.utility.ColorUtil.Red;

public class LidarPointCloudFragment extends ShowcaseExampleBaseFragment<ExampleSingleChart3dFragmentBinding> {
    private final static double MIN = 0;
    private final static double MAX = 50;

    @NonNull
    @Override
    protected ExampleSingleChart3dFragmentBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ExampleSingleChart3dFragmentBinding.inflate(inflater);
    }

    @Override
    protected void initExample(ExampleSingleChart3dFragmentBinding binding) {
        final NumericAxis3D xAxis = sciChart3DBuilder.newNumericAxis3D().withAxisTitle("X Distance (metres)").withTextFormatting("0m").build();
        final NumericAxis3D yAxis = sciChart3DBuilder.newNumericAxis3D().withAxisTitle("Height (metres)").withTextFormatting("0m").withVisibleRange(MIN, MAX).build();
        final NumericAxis3D zAxis = sciChart3DBuilder.newNumericAxis3D().withAxisTitle("Y Distance (metres)").withTextFormatting("0m").build();

        final ScatterRenderableSeries3D scatterSeries = sciChart3DBuilder.newScatterSeries3D()
                .withPointMarker(sciChart3DBuilder.newPixelPointMarker3D().build())
                .build();

        final SurfaceMeshRenderableSeries3D surfaceMeshSeries = sciChart3DBuilder.newSurfaceMeshSeries3D()
                .withDrawMeshAs(DrawMeshAs.SolidWithContours)
                .withMeshColorPalette(new GradientColorPalette(
                        new int[]{0xFF1e90FF, 0xFF32CD32, Orange, Red, Purple},
                        new float[]{0.0f, 0.2f, 0.5f, 0.7f, 1.0f})
                )
                .withMeshPaletteMode(MeshPaletteMode.HeightMapInterpolated)
                .withContourStroke(0xFFF0FFFF)
                .withContourStrokeThickness(2)
                .withMinimum(MIN)
                .withMaximum(MAX)
                .withOpacity(0.5f)
                .build();

        // read and assign LIDAR to data series async
        getLidarData().doOnNext(lidarData -> {
            final PointMetadataProvider3D pointMetadataProvider = createPointMetadataProvider(MIN, MAX, lidarData);

            final XyzDataSeries3D<Integer, Double, Integer> xyzDataSeries3D = new XyzDataSeries3D<>(Integer.class, Double.class, Integer.class);
            xyzDataSeries3D.append(lidarData.xValues, lidarData.yValues, lidarData.zValues);

            final UniformGridDataSeries3D<Integer, Double, Integer> gridDataSeries3D = new UniformGridDataSeries3D<>(Integer.class, Double.class, Integer.class, lidarData.numberColumns, lidarData.numberRows);
            gridDataSeries3D.setStepX(lidarData.cellSize);
            gridDataSeries3D.setStepZ(lidarData.cellSize);
            int index = 0;
            for (int z = 0; z < lidarData.numberRows; z++) {
                for (int x = 0; x < lidarData.numberColumns; x++) {
                    gridDataSeries3D.updateYAt(x, z, lidarData.yValues.get(index++));
                }
            }

            scatterSeries.setDataSeries(xyzDataSeries3D);
            scatterSeries.setMetadataProvider(pointMetadataProvider);

            surfaceMeshSeries.setDataSeries(gridDataSeries3D);
        }).subscribeOn(Schedulers.io()).compose(bindToLifecycle()).subscribe();

        final SciChartSurface3D surface3d = binding.surface3d;
        UpdateSuspender.using(surface3d, () -> {
            surface3d.setXAxis(xAxis);
            surface3d.setYAxis(yAxis);
            surface3d.setZAxis(zAxis);
            Collections.addAll(surface3d.getRenderableSeries(), scatterSeries, surfaceMeshSeries);

            surface3d.getChartModifiers().add(sciChart3DBuilder.newModifierGroup()
                    .withOrbitModifier3D().build()
                    .withZoomExtentsModifier3D().withResetPosition(800, 1000, 800).withResetTarget(0, 25, 0).build()
                    .withPinchZoomModifier3D().build()
                    .build()
            );

            surface3d.getCamera().getPosition().assign(800, 1000, 800);
            surface3d.getCamera().setFarClip(10000);
            surface3d.getWorldDimensions().assign(1000, 200, 1000);
        });
    }

    private Observable<AscData> getLidarData() {
        return Observable.fromCallable(() -> DataManager.getInstance().getLidarData(requireContext()));
    }

    private PointMetadataProvider3D createPointMetadataProvider(double min, double max, AscData lidarData) {
        final PointMetadataProvider3D metadataProvider3D = new PointMetadataProvider3D();
        final ColorMap colorMap = new ColorMap(
                new int[]{0xFF1E90FF, 0xFF32CD32, Orange, Red, Purple},
                new float[]{0.0f, 0.2f, 0.5f, 0.7f, 1f}
        );
        final IntegerValues colors = new IntegerValues();
        colorMap.lerpColorsForValues(colors, lidarData.yValues, min, max);

        final int[] colorItems = colors.getItemsArray();
        for (int i = 0, size = colors.size(); i < size; i++) {
            metadataProvider3D.metadata.add(new PointMetadata3D(colorItems[i]));
        }

        return metadataProvider3D;
    }
}
