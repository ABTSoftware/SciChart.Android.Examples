//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2019. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// UseChartModifiers3DFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.charts3d;

import com.scichart.charting3d.common.math.Vector3;
import com.scichart.charting3d.model.dataSeries.xyz.XyzDataSeries3D;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.axes.NumericAxis3D;
import com.scichart.charting3d.visuals.camera.Camera3D;
import com.scichart.charting3d.visuals.camera.ICameraController;
import com.scichart.charting3d.visuals.pointMarkers.SpherePointMarker3D;
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.PointMetadataProvider3D;
import com.scichart.charting3d.visuals.renderableSeries.scatter.ScatterRenderableSeries3D;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class UseChartModifiers3DFragment extends ExampleBaseFragment {
    @BindView(R.id.chart3d)
    SciChartSurface3D surface3d;

    @Override
    protected int getLayoutId() {
        return R.layout.example_chart3d_modifiers_fragment;
    }

    @Override
    protected void initExample() {
        final DataManager dataManager = DataManager.getInstance();

        final Camera3D camera = sciChart3DBuilder.newCamera3D().build();

        final NumericAxis3D xAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(.1, .1).build();
        final NumericAxis3D yAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(.1, .1).build();
        final NumericAxis3D zAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(.1, .1).build();

        final XyzDataSeries3D<Double, Double, Double> xyzDataSeries3D = new XyzDataSeries3D<>(Double.class, Double.class, Double.class);
        final PointMetadataProvider3D metadataProvider = new PointMetadataProvider3D();

        final List<PointMetadataProvider3D.PointMetadata3D> medatata = metadataProvider.metadata;
        final int count = 25;
        for (double x = 0; x < count; x++) {
            final int color = dataManager.getRandomColor();
            for (double z = 1; z < count; z++) {
                final double y = Math.pow(z, 0.3);

                xyzDataSeries3D.append(x, y, z);
                medatata.add(new PointMetadataProvider3D.PointMetadata3D(color, 2));
            }
        }

        final SpherePointMarker3D pointMarker = sciChart3DBuilder.newSpherePointMarker3D()
                .withSize(2f)
                .build();

        final ScatterRenderableSeries3D rs = sciChart3DBuilder.newScatterSeries3D()
                .withDataSeries(xyzDataSeries3D)
                .withPointMarker(pointMarker)
                .withMetadataProvider(metadataProvider)
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
                        .withOrbitModifier3D().build()
                        .withPinchZoomModifier3D().build()
                        .withZoomExtentsModifier3D()
                            .withAnimationDuration(500)
                            .withResetPosition(new Vector3(200, 200, 200))
                            .withResetTarget(new Vector3(0, 0, 0))
                            .build()
                        .build());
            }
        });
    }

    @OnClick(R.id.rotateHorizontal)
    public void onRotateHorizontal(){
        final ICameraController camera = surface3d.getCamera();
        final float orbitalYaw = camera.getOrbitalYaw();

        if(orbitalYaw < 360) {
            camera.setOrbitalYaw(orbitalYaw + 90);
        } else {
            camera.setOrbitalYaw(360 - orbitalYaw);
        }
    }

    @OnClick(R.id.rotateVertical)
    public void onRotateVertical(){
        final ICameraController camera = surface3d.getCamera();
        final float orbitalPitch = camera.getOrbitalPitch();

        if(orbitalPitch < 89) {
            camera.setOrbitalPitch(orbitalPitch + 90);
        } else {
            camera.setOrbitalPitch(-90);
        }
    }
}
