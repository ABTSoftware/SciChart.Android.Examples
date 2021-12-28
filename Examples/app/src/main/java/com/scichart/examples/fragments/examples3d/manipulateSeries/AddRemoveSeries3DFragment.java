//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// AddRemoveSeries3DFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples3d.manipulateSeries;

import static com.scichart.charting.visuals.axes.AutoRange.Always;

import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.scichart.charting3d.model.RenderableSeries3DCollection;
import com.scichart.charting3d.model.dataSeries.xyz.XyzDataSeries3D;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.axes.NumericAxis3D;
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.PointMetadataProvider3D;
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.PointMetadataProvider3D.PointMetadata3D;
import com.scichart.charting3d.visuals.renderableSeries.scatter.ScatterRenderableSeries3D;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.databinding.ExampleAddRemoveSeries3dFragmentBinding;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.List;
import java.util.Locale;
import java.util.Random;

public class AddRemoveSeries3DFragment extends ExampleBaseFragment<ExampleAddRemoveSeries3dFragmentBinding> {
    private static final int MAX_SERIES_AMOUNT = 15;
    private static final int DATA_POINTS_COUNT = 15;

    private final Random random = new Random();

    @NonNull
    @Override
    protected ExampleAddRemoveSeries3dFragmentBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ExampleAddRemoveSeries3dFragmentBinding.inflate(inflater);
    }

    @Override
    protected void initExample(ExampleAddRemoveSeries3dFragmentBinding binding) {
        final NumericAxis3D xAxis = sciChart3DBuilder.newNumericAxis3D().withAutoRangeMode(Always).withGrowBy(.1, .1).build();
        final NumericAxis3D yAxis = sciChart3DBuilder.newNumericAxis3D().withAutoRangeMode(Always).withGrowBy(.1, .1).build();
        final NumericAxis3D zAxis = sciChart3DBuilder.newNumericAxis3D().withAutoRangeMode(Always).withGrowBy(.1, .1).build();

        final SciChartSurface3D surface3d = binding.surface3d;
        UpdateSuspender.using(surface3d, () -> {
            surface3d.setXAxis(xAxis);
            surface3d.setYAxis(yAxis);
            surface3d.setZAxis(zAxis);

            surface3d.getChartModifiers().add(sciChart3DBuilder
                    .newModifierGroupWithDefaultModifiers()
                    .withLegendModifier().withShowSeriesMarkers(false).build()
                    .build());
        });

        binding.addSeries.setOnClickListener(v -> addSeries());
        binding.removeSeries.setOnClickListener(v -> removeSeries());
        binding.reset.setOnClickListener(v -> binding.surface3d.getRenderableSeries().clear());
    }

    private void addSeries() {
        final SciChartSurface3D surface3d = binding.surface3d;
        UpdateSuspender.using(surface3d, () -> {
            final RenderableSeries3DCollection renderableSeries = surface3d.getRenderableSeries();
            final int count = renderableSeries.size();
            if (count >= MAX_SERIES_AMOUNT) return;

            final DataManager dataManager = DataManager.getInstance();
            final PointMetadataProvider3D pointMetadataProvider3D = new PointMetadataProvider3D();
            final List<PointMetadata3D> metadata = pointMetadataProvider3D.metadata;

            final XyzDataSeries3D<Double, Double, Double> ds = new XyzDataSeries3D<>(Double.class, Double.class, Double.class);
            ds.setSeriesName(String.format(Locale.getDefault(), "Series #%d", count));
            for (int i = 0; i < DATA_POINTS_COUNT; i++) {
                final double x = dataManager.getGaussianRandomNumber(5, 1.5);
                final double y = dataManager.getGaussianRandomNumber(5, 1.5);
                final double z = dataManager.getGaussianRandomNumber(5, 1.5);
                ds.append(x, y, z);

                metadata.add(new PointMetadata3D(dataManager.getRandomColor(), dataManager.getRandomScale()));
            }

            final ScatterRenderableSeries3D rs = sciChart3DBuilder.newScatterSeries3D()
                    .withDataSeries(ds)
                    .withMetadataProvider(pointMetadataProvider3D)
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
        });
    }

    void removeSeries() {
        final SciChartSurface3D surface3d = binding.surface3d;
        UpdateSuspender.using(surface3d, () -> {
            final RenderableSeries3DCollection renderableSeries = surface3d.getRenderableSeries();
            if (!renderableSeries.isEmpty()) {
                renderableSeries.remove(0);
            }
        });
    }
}