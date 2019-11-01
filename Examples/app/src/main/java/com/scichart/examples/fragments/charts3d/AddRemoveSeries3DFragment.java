//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2018. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// AddRemoveSeries3DFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.charts3d;

import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting3d.model.RenderableSeries3DCollection;
import com.scichart.charting3d.model.dataSeries.xyz.XyzDataSeries3D;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.axes.NumericAxis3D;
import com.scichart.charting3d.visuals.camera.Camera3D;
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.PointMetadataProvider3D;
import com.scichart.charting3d.visuals.renderableSeries.scatter.ScatterRenderableSeries3D;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;

public class AddRemoveSeries3DFragment extends ExampleBaseFragment {
    private static final int MAX_SERIES_AMOUNT = 15;
    private static final int DATA_POINTS_COUNT = 15;

    private final Random random = new Random();

    @BindView(R.id.chart3d)
    SciChartSurface3D surface3d;

    @Override
    protected int getLayoutId() {
        return R.layout.example_add_remove_series_3d_fragment;
    }

    @Override
    protected void initExample() {
        final Camera3D camera = sciChart3DBuilder.newCamera3D().build();

        final NumericAxis3D xAxis = sciChart3DBuilder.newNumericAxis3D().withAutoRangeMode(AutoRange.Always).withGrowBy(.1, .1).build();
        final NumericAxis3D yAxis = sciChart3DBuilder.newNumericAxis3D().withAutoRangeMode(AutoRange.Always).withGrowBy(.1, .1).build();
        final NumericAxis3D zAxis = sciChart3DBuilder.newNumericAxis3D().withAutoRangeMode(AutoRange.Always).withGrowBy(.1, .1).build();

        UpdateSuspender.using(surface3d, new Runnable() {
            @Override
            public void run() {
                surface3d.setCamera(camera);

                surface3d.setXAxis(xAxis);
                surface3d.setYAxis(yAxis);
                surface3d.setZAxis(zAxis);

                surface3d.getChartModifiers().add(sciChart3DBuilder
                        .newModifierGroupWithDefaultModifiers()
                        .withLegendModifier().withShowSeriesMarkers(false).build()
                        .build());
            }
        });
    }


    @OnClick(R.id.addSeries)
    void onAddSeries() {
        UpdateSuspender.using(surface3d, new Runnable() {
            @Override
            public void run() {
                final RenderableSeries3DCollection renderableSeries = surface3d.getRenderableSeries();
                if(renderableSeries.size() >= MAX_SERIES_AMOUNT)
                    return;

                final XyzDataSeries3D<Double, Double, Double> ds = new XyzDataSeries3D<>(Double.class, Double.class, Double.class);
                final PointMetadataProvider3D metadataProvider = new PointMetadataProvider3D();

                final DataManager dataManager = DataManager.getInstance();
                for (int i = 0; i < DATA_POINTS_COUNT; i++) {
                    final double x = dataManager.getGaussianRandomNumber(5, 1.5);
                    final double y = dataManager.getGaussianRandomNumber(5, 1.5);
                    final double z = dataManager.getGaussianRandomNumber(5, 1.5);

                    final int color = dataManager.getRandomColor();
                    final float scale = dataManager.getRandomScale();

                    metadataProvider.metadata.add(new PointMetadataProvider3D.PointMetadata3D(color, scale));
                    ds.append(x, y, z);
                }

                final ScatterRenderableSeries3D rs = sciChart3DBuilder.newScatterSeries3D()
                        .withDataSeries(ds)
                        .withMetadataProvider(metadataProvider)
                        .build();

                final int randValue = random.nextInt(6);
                switch (randValue) {
                    case 0:
                        rs.setPointMarker(sciChart3DBuilder.newCubePointMarker3D().build());
                        break;
                    case 1:
                        rs.setPointMarker(sciChart3DBuilder.newEllipsePointMarker3D().build());
                        break;
                    case 2:
                        rs.setPointMarker(sciChart3DBuilder.newPyramidPointMarker3D().build());
                        break;
                    case 3:
                        rs.setPointMarker(sciChart3DBuilder.newQuadPointMarker3D().build());
                        break;
                    case 4:
                        rs.setPointMarker(sciChart3DBuilder.newSpherePointMarker3D().build());
                        break;
                    case 5:
                        rs.setPointMarker(sciChart3DBuilder.newTrianglePointMarker3D().build());
                        break;
                }


                renderableSeries.add(rs);

                final int index = renderableSeries.indexOf(rs);
                ds.setSeriesName(String.format(Locale.getDefault(), "Series #%d", index));
            }
        });
    }

    @OnClick(R.id.removeSeries)
    void onRemoveSeries() {
        UpdateSuspender.using(surface3d, new Runnable() {
            @Override
            public void run() {
                final RenderableSeries3DCollection renderableSeries = surface3d.getRenderableSeries();
                if (!renderableSeries.isEmpty()) {
                    renderableSeries.remove(0);
                }
            }
        });
    }

    @OnClick(R.id.reset)
    void onReset() {
        UpdateSuspender.using(surface3d, new Runnable() {
            @Override
            public void run() {
                surface3d.getRenderableSeries().clear();
            }
        });
    }
}
