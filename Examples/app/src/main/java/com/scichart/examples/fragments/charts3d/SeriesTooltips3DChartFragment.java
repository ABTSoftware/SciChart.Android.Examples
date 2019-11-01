//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2019. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SeriesTooltips3DChartFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.charts3d;

import com.scichart.charting3d.model.dataSeries.xyz.XyzDataSeries3D;
import com.scichart.charting3d.modifiers.CrosshairMode;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.axes.NumericAxis3D;
import com.scichart.charting3d.visuals.camera.Camera3D;
import com.scichart.charting3d.visuals.pointMarkers.SpherePointMarker3D;
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.PointMetadataProvider3D;
import com.scichart.charting3d.visuals.renderableSeries.pointLine.PointLineRenderableSeries3D;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import butterknife.BindView;

public class SeriesTooltips3DChartFragment extends ExampleBaseFragment {
    private static final int SEGMENTS_COUNT = 25;

    private static final double Y_ANGLE = Math.toRadians(-65);
    private static final double COS_Y_ANGLE = Math.cos(Y_ANGLE);
    private static final double SIN_Y_ANGLE = Math.sin(Y_ANGLE);

    private final int blueColor = ColorUtil.argb(0xFF, 0x00, 0x84, 0xCF);
    private final int redColor = ColorUtil.argb(0xFF, 0xEE, 0x11, 0x10);

    @BindView(R.id.chart3d)
    SciChartSurface3D surface3d;

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart3d_with_modifier_tip_fragment;
    }

    @Override
    protected void initExample() {
        final Camera3D camera = sciChart3DBuilder.newCamera3D()
                .withPosition(-160, 190, -520)
                .withTarget(-45, 150, 0)
                .build();

        final NumericAxis3D xAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(.2, .2).withMaxAutoTicks(5).build();
        final NumericAxis3D yAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(.1, .1).build();
        final NumericAxis3D zAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(.2, .2).build();

        final PointMetadataProvider3D metadataProvider3D = new PointMetadataProvider3D();
        final XyzDataSeries3D<Double, Double, Double> dataSeries = new XyzDataSeries3D<>(Double.class, Double.class, Double.class);

        final double rotationAngle = 360 / 45d;

        double currentAngle = 0d;
        for (int i = -SEGMENTS_COUNT; i < SEGMENTS_COUNT + 1; i++) {
            appendPoint(dataSeries, -4, i, currentAngle);
            appendPoint(dataSeries, 4, i, currentAngle);

            metadataProvider3D.metadata.add(new PointMetadataProvider3D.PointMetadata3D(blueColor));
            metadataProvider3D.metadata.add(new PointMetadataProvider3D.PointMetadata3D(redColor));

            currentAngle = (currentAngle + rotationAngle) % 360;
        }

        final SpherePointMarker3D pointMarker3D = sciChart3DBuilder.newSpherePointMarker3D()
                .withSize(8f)
                .build();

        final PointLineRenderableSeries3D rs = sciChart3DBuilder.newPointLinesSeries3D()
                .withDataSeries(dataSeries)
                .withMetadataProvider(metadataProvider3D)
                .withPointMarker(pointMarker3D)
                .withIsLineStrips(false)
                .withStrokeThicknes(4f)
                .build();

        UpdateSuspender.using(surface3d, new Runnable() {
            @Override
            public void run() {
                surface3d.getWorldDimensions().assign(600, 300, 180);

                surface3d.setCamera(camera);

                surface3d.setXAxis(xAxis);
                surface3d.setYAxis(yAxis);
                surface3d.setZAxis(zAxis);

                surface3d.getRenderableSeries().add(rs);

                surface3d.getChartModifiers().add(sciChart3DBuilder.newModifierGroup()
                        .withPinchZoomModifier3D().build()
                        .withOrbitModifier3D().withReceiveHandledEvents(true).withExecuteOnPointerCount(2).build()
                        .withZoomExtentsModifier3D().build()
                        .withTooltipModifier()
                            .withReceiveHandledEvents(true)
                            .withCrosshairMode(CrosshairMode.Lines)
                            .withCrosshairPlanesFill(0x33FF6600)
                            .withExecuteOnPointerCount(1)
                            .build()
                        .build());
            }
        });
    }

    private void appendPoint(XyzDataSeries3D<Double, Double, Double> ds, double x, double y, double currentAngle) {
        final double radAngle = Math.toRadians(currentAngle);

        final double temp = x * Math.cos(radAngle);

        final double xValue = temp * COS_Y_ANGLE - y * SIN_Y_ANGLE;
        final double yValue = temp * SIN_Y_ANGLE + y * COS_Y_ANGLE;
        final double zValue = x * Math.sin(radAngle);

        ds.append(xValue, yValue, zValue);
    }
}
