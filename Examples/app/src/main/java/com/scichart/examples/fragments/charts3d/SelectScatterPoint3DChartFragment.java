//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2019. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SelectScatterPoint3DChartFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.charts3d;

import com.scichart.charting3d.model.dataSeries.xyz.XyzDataSeries3D;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.axes.NumericAxis3D;
import com.scichart.charting3d.visuals.camera.Camera3D;
import com.scichart.charting3d.visuals.pointMarkers.EllipsePointMarker3D;
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.DefaultSelectableMetadataProvider3D;
import com.scichart.charting3d.visuals.renderableSeries.scatter.ScatterRenderableSeries3D;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import butterknife.BindView;

public class SelectScatterPoint3DChartFragment extends ExampleBaseFragment {
    @BindView(R.id.chart3d)
    SciChartSurface3D surface3d;

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart3d_fragment;
    }

    @Override
    protected void initExample() {
        final Camera3D camera = sciChart3DBuilder.newCamera3D().build();

        final NumericAxis3D xAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(.1, .1).build();
        final NumericAxis3D yAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(.1, .1).build();
        final NumericAxis3D zAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(.1, .1).build();

        final DataManager dataManager = DataManager.getInstance();
        final XyzDataSeries3D<Double, Double, Double> dataSeries = new XyzDataSeries3D<>(Double.class, Double.class, Double.class);
        for (int i = 0; i < 250; i++) {
            final double x = dataManager.getGaussianRandomNumber(5, 1.5);
            final double y = dataManager.getGaussianRandomNumber(5, 1.5);
            final double z = dataManager.getGaussianRandomNumber(5, 1.5);

            dataSeries.append(x, y, z);
        }

        final EllipsePointMarker3D pointMarker3D = sciChart3DBuilder.newEllipsePointMarker3D()
                .withFill(ColorUtil.LimeGreen)
                .withSize(5f)
                .build();

        final ScatterRenderableSeries3D rs = sciChart3DBuilder.newScatterSeries3D()
                .withDataSeries(dataSeries)
                .withPointMarker(pointMarker3D)
                //set ISelectableMetadataProvider3D because we're going to use vertex selection for this series
                .withMetadataProvider(new DefaultSelectableMetadataProvider3D())
                .build();

        UpdateSuspender.using(surface3d, new Runnable() {
            @Override
            public void run() {
                surface3d.setCamera(camera);

                surface3d.setXAxis(xAxis);
                surface3d.setYAxis(yAxis);
                surface3d.setZAxis(zAxis);

                surface3d.getRenderableSeries().add(rs);

                surface3d.getChartModifiers().add(sciChart3DBuilder.newModifierGroup()
                        .withPinchZoomModifier3D().build()
                        .withOrbitModifier3D().withReceiveHandledEvents(true).build()
                        .withZoomExtentsModifier3D().build()
                        .withVertexSelectionModifier().withReceiveHandledEvents(true).build()
                        .build());
            }
        });
    }
}
