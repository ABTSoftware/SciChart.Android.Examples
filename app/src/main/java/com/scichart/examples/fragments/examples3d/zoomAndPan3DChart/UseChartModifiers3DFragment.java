//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// UseChartModifiers3DFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples3d.zoomAndPan3DChart;

import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.scichart.charting3d.common.math.Vector3;
import com.scichart.charting3d.model.dataSeries.xyz.XyzDataSeries3D;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.axes.NumericAxis3D;
import com.scichart.charting3d.visuals.camera.ICameraController;
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.PointMetadataProvider3D;
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.PointMetadataProvider3D.PointMetadata3D;
import com.scichart.charting3d.visuals.renderableSeries.scatter.ScatterRenderableSeries3D;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.databinding.ExampleChart3dModifiersFragmentBinding;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.List;

public class UseChartModifiers3DFragment extends ExampleBaseFragment<ExampleChart3dModifiersFragmentBinding>{

    @NonNull
    @Override
    protected ExampleChart3dModifiersFragmentBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ExampleChart3dModifiersFragmentBinding.inflate(inflater);
    }

    @Override
    protected void initExample(@NonNull ExampleChart3dModifiersFragmentBinding binding) {
        final DataManager dataManager = DataManager.getInstance();

        final NumericAxis3D xAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(.1, .1).build();
        final NumericAxis3D yAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(.1, .1).build();
        final NumericAxis3D zAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(.1, .1).build();

        final XyzDataSeries3D<Double, Double, Double> xyzDataSeries3D = new XyzDataSeries3D<>(Double.class, Double.class, Double.class);
        final PointMetadataProvider3D pointMetadataProvider3D = new PointMetadataProvider3D();
        final List<PointMetadata3D> metadata = pointMetadataProvider3D.metadata;

        final int count = 25;
        for (double x = 0; x < count; x++) {
            final int color = dataManager.getRandomColor();
            for (double z = 1; z < count; z++) {
                final double y = Math.pow(z, 0.3);

                xyzDataSeries3D.append(x, y, z);
                metadata.add(new PointMetadata3D(color, 2));
            }
        }

        final ScatterRenderableSeries3D rs = sciChart3DBuilder.newScatterSeries3D()
                .withDataSeries(xyzDataSeries3D)
                .withPointMarker(sciChart3DBuilder.newSpherePointMarker3D().withSize(2f).build())
                .withMetadataProvider(pointMetadataProvider3D)
                .build();

        final SciChartSurface3D surface3d = binding.surface3d;
        UpdateSuspender.using(surface3d, () -> {
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
        });

        final ICameraController camera = surface3d.getCamera();
        binding.rotateHorizontal.setOnClickListener(v -> {
            final float orbitalYaw = camera.getOrbitalYaw();
            if (orbitalYaw < 360) {
                camera.setOrbitalYaw(orbitalYaw + 90);
            } else {
                camera.setOrbitalYaw(360 - orbitalYaw);
            }
        });

        binding.rotateVertical.setOnClickListener(v -> {
            final float orbitalPitch = camera.getOrbitalPitch();
            if (orbitalPitch < 89) {
                camera.setOrbitalPitch(orbitalPitch + 90);
            } else {
                camera.setOrbitalPitch(-90);
            }
        });
    }
}
