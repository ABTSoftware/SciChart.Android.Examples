//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2018. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// LogarithmicAxis3DFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.charts3d;

import com.scichart.charting.visuals.axes.ScientificNotation;
import com.scichart.charting3d.model.dataSeries.xyz.XyzDataSeries3D;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.axes.LogarithmicNumericAxis3D;
import com.scichart.charting3d.visuals.axes.NumericAxis3D;
import com.scichart.charting3d.visuals.pointMarkers.SpherePointMarker3D;
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.PointMetadataProvider3D;
import com.scichart.charting3d.visuals.renderableSeries.pointLine.PointLineRenderableSeries3D;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import butterknife.BindView;

public class LogarithmicAxis3DFragment extends ExampleBaseFragment {
    @BindView(R.id.chart3d)
    SciChartSurface3D surface3d;

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart3d_fragment;
    }

    @Override
    protected void initExample() {
        final LogarithmicNumericAxis3D xAxis = sciChart3DBuilder.newLogarithmicNumericAxis3D()
                .withGrowBy(.1, .1)
                .withDrawMajorBands(false)
                .withTextFormatting("#.#e+0")
                .withCursorTextFormating("0.0")
                .withScientificNotation(ScientificNotation.LogarithmicBase)
                .build();

        final LogarithmicNumericAxis3D yAxis = sciChart3DBuilder.newLogarithmicNumericAxis3D()
                .withGrowBy(.1, .1)
                .withDrawMajorBands(false)
                .withTextFormatting("#.000")
                .withCursorTextFormating("0.0")
                .withScientificNotation(ScientificNotation.None)
                .build();

        final NumericAxis3D zAxis = sciChart3DBuilder.newNumericAxis3D()
                .withGrowBy(.5, .5)
                .build();

        final int count = 100;

        final DataManager dataManager = DataManager.getInstance();
        final DoubleSeries data = dataManager.getExponentialCurve(1.8, count);

        final XyzDataSeries3D<Double, Double, Double> dataSeries = new XyzDataSeries3D<>(Double.class, Double.class, Double.class);
        final PointMetadataProvider3D metadataProvider3D = new PointMetadataProvider3D();

        for (int i = 0; i < count; i++) {
            final double x = data.xValues.get(i);
            final double y = data.yValues.get(i);
            final double z = dataManager.getGaussianRandomNumber(15, 1.5);

            final int color = dataManager.getRandomColor();
            final float scale = dataManager.getRandomScale();

            dataSeries.append(x, y, z);
            metadataProvider3D.metadata.add(new PointMetadataProvider3D.PointMetadata3D(color, scale));
        }

        final SpherePointMarker3D pointMarker3D = sciChart3DBuilder.newSpherePointMarker3D()
                .withSize(5f)
                .build();

        final PointLineRenderableSeries3D rs = sciChart3DBuilder.newPointLinesSeries3D()
                .withDataSeries(dataSeries)
                .withStrokeThicknes(2)
                .withPointMarker(pointMarker3D)
                .withMetadataProvider(metadataProvider3D)
                .build();

        UpdateSuspender.using(surface3d, new Runnable() {
            @Override
            public void run() {
                surface3d.setXAxis(xAxis);
                surface3d.setYAxis(yAxis);
                surface3d.setZAxis(zAxis);

                surface3d.getRenderableSeries().add(rs);

                surface3d.getChartModifiers().add(sciChart3DBuilder.newModifierGroupWithDefaultModifiers().build());
            }
        });
    }
}
