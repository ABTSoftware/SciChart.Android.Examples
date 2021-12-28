//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CreateSparseImpulseSeries3DFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples3d.basicChartTypes;

import androidx.annotation.NonNull;

import com.scichart.charting3d.model.dataSeries.xyz.XyzDataSeries3D;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.axes.NumericAxis3D;
import com.scichart.charting3d.visuals.renderableSeries.impulse.ImpulseRenderableSeries3D;
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.PointMetadataProvider3D;
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.PointMetadataProvider3D.PointMetadata3D;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.fragments.base.ExampleSingleChart3DBaseFragment;

import java.util.List;

public class SparseImpulseSeries3DFragment extends ExampleSingleChart3DBaseFragment {
    private static final int COUNT = 15;

    @Override
    protected void initExample(@NonNull SciChartSurface3D surface3d) {
        final DataManager dataManager = DataManager.getInstance();
        final PointMetadataProvider3D metadataProvider = new PointMetadataProvider3D();
        final List<PointMetadata3D> metadata = metadataProvider.metadata;

        final XyzDataSeries3D<Double, Double, Double> ds = new XyzDataSeries3D<>(Double.class, Double.class, Double.class);
        for (double i = 1; i < COUNT; i++) {
            for (double j = 1; j <= COUNT; j++) {
                if (i != j && i % 2 == 0 && j % 2 == 0) {
                    final double y = dataManager.getGaussianRandomNumber(5, 1.5);
                    final int color = dataManager.getRandomColor();

                    ds.append(i, y, j);
                    metadata.add(new PointMetadata3D(color));
                }
            }
        }

        final NumericAxis3D xAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(.1, .1).build();
        final NumericAxis3D yAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(.1, .1).build();
        final NumericAxis3D zAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(.1, .1).build();

        final ImpulseRenderableSeries3D rs = sciChart3DBuilder.newImpulseSeries3D()
                .withDataSeries(ds)
                .withPointMarker(sciChart3DBuilder.newSpherePointMarker3D().withSize(10).build())
                .withMetadataProvider(metadataProvider)
                .build();

        UpdateSuspender.using(surface3d, () -> {
            surface3d.setXAxis(xAxis);
            surface3d.setYAxis(yAxis);
            surface3d.setZAxis(zAxis);
            surface3d.getRenderableSeries().add(rs);
            surface3d.getChartModifiers().add(sciChart3DBuilder.newModifierGroupWithDefaultModifiers().build());
        });
    }
}
