//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// LogarithmicAxis3DFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples3d.axis3D;

import androidx.annotation.NonNull;

import com.scichart.charting.visuals.axes.ScientificNotation;
import com.scichart.charting3d.model.dataSeries.xyz.XyzDataSeries3D;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.axes.LogarithmicNumericAxis3D;
import com.scichart.charting3d.visuals.axes.NumericAxis3D;
import com.scichart.charting3d.visuals.pointMarkers.SpherePointMarker3D;
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.PointMetadataProvider3D;
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.PointMetadataProvider3D.PointMetadata3D;
import com.scichart.charting3d.visuals.renderableSeries.pointLine.PointLineRenderableSeries3D;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.fragments.base.ExampleSingleChart3DBaseFragment;

public class LogarithmicAxis3DFragment extends ExampleSingleChart3DBaseFragment {

    @Override
    protected void initExample(@NonNull SciChartSurface3D surface3d) {
        final LogarithmicNumericAxis3D xAxis = sciChart3DBuilder.newLogarithmicNumericAxis3D()
                .withGrowBy(0.1, 0.1)
                .withDrawMajorBands(false)
                .withTextFormatting("#.#e+0")
                .withCursorTextFormating("0.0")
                .withScientificNotation(ScientificNotation.LogarithmicBase)
                .build();

        final LogarithmicNumericAxis3D yAxis = sciChart3DBuilder.newLogarithmicNumericAxis3D()
                .withGrowBy(0.1, 0.1)
                .withDrawMajorBands(false)
                .withTextFormatting("#.000")
                .withCursorTextFormating("0.0")
                .withScientificNotation(ScientificNotation.None)
                .build();

        final NumericAxis3D zAxis = sciChart3DBuilder.newNumericAxis3D()
                .withGrowBy(0.5, 0.5)
                .build();

        final int count = 100;

        final DataManager dataManager = DataManager.getInstance();
        final DoubleSeries data = dataManager.getExponentialCurve(1.8, count);

        final XyzDataSeries3D<Double, Double, Double> dataSeries = new XyzDataSeries3D<>(Double.class, Double.class, Double.class);
        final PointMetadataProvider3D pointMetaDataProvider = new PointMetadataProvider3D();

        for (int i = 0; i < count; i++) {
            final double x = data.xValues.get(i);
            final double y = data.yValues.get(i);
            final double z = dataManager.getGaussianRandomNumber(15, 1.5);
            dataSeries.append(x, y, z);

            pointMetaDataProvider.metadata.add(new PointMetadata3D(dataManager.getRandomColor(), dataManager.getRandomScale()));
        }

        final SpherePointMarker3D pointMarker3D = sciChart3DBuilder.newSpherePointMarker3D()
                .withSize(5f)
                .build();

        final PointLineRenderableSeries3D rs = sciChart3DBuilder.newPointLinesSeries3D()
                .withDataSeries(dataSeries)
                .withStrokeThicknes(2)
                .withPointMarker(pointMarker3D)
                .withMetadataProvider(pointMetaDataProvider)
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