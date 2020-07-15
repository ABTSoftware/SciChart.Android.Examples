//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2020. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// LidarPointCloudFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.showcase.lidarPointCloud;

import com.scichart.charting.visuals.renderableSeries.ColorMap;
import com.scichart.charting3d.model.dataSeries.grid.UniformGridDataSeries3D;
import com.scichart.charting3d.model.dataSeries.xyz.XyzDataSeries3D;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.axes.NumericAxis3D;
import com.scichart.charting3d.visuals.camera.Camera3D;
import com.scichart.charting3d.visuals.renderableSeries.data.DrawMeshAs;
import com.scichart.charting3d.visuals.renderableSeries.data.GradientColorPalette;
import com.scichart.charting3d.visuals.renderableSeries.data.MeshPaletteMode;
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.PointMetadataProvider3D;
import com.scichart.charting3d.visuals.renderableSeries.scatter.ScatterRenderableSeries3D;
import com.scichart.charting3d.visuals.renderableSeries.surfaceMesh.SurfaceMeshRenderableSeries3D;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.model.IntegerValues;
import com.scichart.examples.R;
import com.scichart.examples.data.AscData;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.fragments.base.ShowcaseExampleBaseFragment;

import java.util.Collections;
import java.util.concurrent.Callable;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static com.scichart.drawing.utility.ColorUtil.Orange;
import static com.scichart.drawing.utility.ColorUtil.Purple;
import static com.scichart.drawing.utility.ColorUtil.Red;

public class LidarPointCloudFragment extends ShowcaseExampleBaseFragment {
    @BindView(R.id.chart3d)
    SciChartSurface3D surface3d;

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart3d_fragment;
    }

    @Override
    protected void initExample() {
        final Camera3D camera3D = sciChart3DBuilder.newCamera3D().withPosition(800, 1000, 800).build();
        camera3D.setFarClip(10000);
        
        final double min = 0;
        final double max = 50;

        final NumericAxis3D xAxis = sciChart3DBuilder.newNumericAxis3D()
                .withAxisTitle("X Distance (metres)")
                .withTextFormatting("0m")
                .build();

        final NumericAxis3D yAxis = sciChart3DBuilder.newNumericAxis3D()
                .withAxisTitle("Height (metres)")
                .withTextFormatting("0m")
                .withVisibleRange(min, max)
                .build();

        final NumericAxis3D zAxis = sciChart3DBuilder.newNumericAxis3D()
                .withAxisTitle("Y Distance (metres)")
                .withTextFormatting("0m")
                .build();

        final ScatterRenderableSeries3D scatterSeries = sciChart3DBuilder.newScatterSeries3D()
                .withPointMarker(sciChart3DBuilder.newPixelPointMarker3D().build())
                .build();

        final int[] fillColors = new int[]{0xFF1e90FF, 0xFF32CD32, Orange, Red, Purple};
        final float[] fillStops = new float[] {0.0f, 0.2f, 0.5f, 0.7f, 1.0f};
        final SurfaceMeshRenderableSeries3D surfaceMeshSeries = sciChart3DBuilder.newSurfaceMeshSeries3D()
                .withDrawMeshAs(DrawMeshAs.SolidWithContours)
                .withMeshColorPalette(new GradientColorPalette(fillColors, fillStops))
                .withMeshPaletteMode(MeshPaletteMode.HeightMapInterpolated)
                .withContourStroke(0xFFF0FFFF)
                .withContourStrokeThickness(2)
                .withMinimum(min)
                .withMaximum(max)
                .withOpacity(0.5f)
                .build();

        // read and assign LIDAR to data series async
        getLidarData().doOnNext(lidarData -> {
            final PointMetadataProvider3D metadataProvider = createPointMetadataProvider(min, max, lidarData);

            final XyzDataSeries3D<Integer, Double, Integer> xyzDataSeries3D = new XyzDataSeries3D<>(Integer.class, Double.class, Integer.class);
            final UniformGridDataSeries3D<Integer, Double, Integer> gridDataSeries3D = new UniformGridDataSeries3D<>(Integer.class, Double.class, Integer.class, lidarData.numberColumns, lidarData.numberRows);

            xyzDataSeries3D.append(lidarData.xValues, lidarData.yValues, lidarData.zValues);

            gridDataSeries3D.setStepX(lidarData.cellSize);
            gridDataSeries3D.setStepZ(lidarData.cellSize);

            int index = 0;
            for (int z = 0; z < lidarData.numberRows; z++) {
                for (int x = 0; x < lidarData.numberColumns; x++) {
                    gridDataSeries3D.updateYAt(x, z, lidarData.yValues.get(index++));
                }
            }

            UpdateSuspender.using(surface3d, () -> {
                scatterSeries.setDataSeries(xyzDataSeries3D);
                scatterSeries.setMetadataProvider(metadataProvider);

                surfaceMeshSeries.setDataSeries(gridDataSeries3D);
            });
        }).subscribeOn(Schedulers.io()).compose(bindToLifecycle()).subscribe();

        UpdateSuspender.using(surface3d, new Runnable() {
            @Override
            public void run() {
                surface3d.getWorldDimensions().assign(1000, 200, 1000);

                surface3d.setCamera(camera3D);
                surface3d.setXAxis(xAxis);
                surface3d.setYAxis(yAxis);
                surface3d.setZAxis(zAxis);

                Collections.addAll(surface3d.getRenderableSeries(), scatterSeries, surfaceMeshSeries);

                surface3d.getChartModifiers().add(
                        sciChart3DBuilder.newModifierGroup()
                            .withOrbitModifier3D().build()
                            .withZoomExtentsModifier3D().withResetPosition(800, 1000, 800).withResetTarget(0, 25, 0).build()
                            .withPinchZoomModifier3D().build()
                        .build());
            }
        });
    }

    private Observable<AscData> getLidarData() {
        return Observable.fromCallable(new Callable<AscData>() {
            @Override
            public AscData call() throws Exception {
                return DataManager.getInstance().getLidarData(getActivity());
            }
        });
    }

    private PointMetadataProvider3D createPointMetadataProvider(double min, double max, AscData lidarData) {
        final PointMetadataProvider3D metadataProvider3D = new PointMetadataProvider3D();
        final ColorMap colorMap = new ColorMap(new int[]{0xFF1E90FF, 0xFF32CD32, Orange, Red, Purple}, new float[]{0.0f, 0.2f, 0.5f, 0.7f, 1f});
        final IntegerValues colors = new IntegerValues();
        colorMap.lerpColorsForValues(colors, lidarData.yValues, min, max);

        final int[] colorItems = colors.getItemsArray();
        for (int i = 0, size = colors.size(); i < size; i++) {
            metadataProvider3D.metadata.add(new PointMetadataProvider3D.PointMetadata3D(colorItems[i]));
        }
        return metadataProvider3D;
    }
}
