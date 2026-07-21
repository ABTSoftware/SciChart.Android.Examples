//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2026. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// Chart3DTestFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples3d.aTest;

import android.view.LayoutInflater;
import androidx.annotation.NonNull;

import com.scichart.charting3d.model.dataSeries.xyz.XyzDataSeries3D;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.axes.NumericAxis3D;
import com.scichart.charting3d.visuals.camera.Camera3D;
import com.scichart.charting3d.visuals.pointMarkers.PixelPointMarker3D;
import com.scichart.charting3d.visuals.renderableSeries.pointLine.PointLineRenderableSeries3D;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.databinding.ExampleChart3dTestFragmentBinding;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

public class Chart3DTestFragment extends ExampleBaseFragment<ExampleChart3dTestFragmentBinding> {

    @NonNull
    @Override
    protected ExampleChart3dTestFragmentBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ExampleChart3dTestFragmentBinding.inflate(inflater);
    }

    @Override
    protected void initExample(@NonNull ExampleChart3dTestFragmentBinding binding) {
        final SciChartSurface3D surface3d = binding.surface3d;

        final Camera3D camera = sciChart3DBuilder.newCamera3D()
                .withZoomToFitOnAttach(false)
                .withPosition(-350, 100, -350)
                .withTarget(0, 50, 0)
                .build();

        final NumericAxis3D xAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(.1, .1).build();
        final NumericAxis3D yAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(.1, .1).build();
        final NumericAxis3D zAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(.1, .1).build();

        final XyzDataSeries3D<Double, Double, Double> xyzDataSeries3D = new XyzDataSeries3D<>(Double.class, Double.class, Double.class);
        for (int i = 0; i < 500; i++) {
            final double x = 10 * Math.sin(i * 0.1);
            final double y = i * 0.2;
            final double z = 10 * Math.cos(i * 0.1);

            xyzDataSeries3D.append(x, y, z);
        }

        final PixelPointMarker3D pointMarker3D = sciChart3DBuilder.newPixelPointMarker3D()
                .withFill(ColorUtil.Blue)
                .build();

        final PointLineRenderableSeries3D rs = sciChart3DBuilder.newPointLinesSeries3D()
                .withDataSeries(xyzDataSeries3D)
                .withStroke(ColorUtil.Cyan)
                .withStrokeThicknes(3f)
                .withIsAntialiased(true)
                .withIsLineStrips(true)
                .withPointMarker(pointMarker3D)
                .build();

        UpdateSuspender.using(surface3d, () -> {
            surface3d.setCamera(camera);

            surface3d.setXAxis(xAxis);
            surface3d.setYAxis(yAxis);
            surface3d.setZAxis(zAxis);

            surface3d.getRenderableSeries().add(rs);
            surface3d.getChartModifiers().add(sciChart3DBuilder.newModifierGroupWithDefaultModifiers().build());
        });

        binding.btnGoToSecond.setOnClickListener(v -> {
            final android.content.Intent intent = new android.content.Intent(getActivity(), Chart3DTest2Activity.class);
            startActivity(intent);
        });
    }
}
