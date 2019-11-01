//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2018. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CreateSparseColumn3DFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.charts3d;

import com.scichart.charting3d.model.dataSeries.grid.UniformGridDataSeries3D;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.axes.NumericAxis3D;
import com.scichart.charting3d.visuals.camera.Camera3D;
import com.scichart.charting3d.visuals.renderableSeries.columns.ColumnRenderableSeries3D;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import butterknife.BindView;

public class CreateUniformColumn3DFragment extends ExampleBaseFragment {
    private static final int COUNT = 15;

    @BindView(R.id.chart3d)
    SciChartSurface3D surface3d;

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart3d_fragment;
    }

    @Override
    protected void initExample() {
        final UniformGridDataSeries3D<Double, Double, Double> ds = new UniformGridDataSeries3D<>(Double.class, Double.class, Double.class, COUNT, COUNT);

        for (int x = 0; x < COUNT; x++) {
            for (int z = 0; z < COUNT; z++) {
                final double y = Math.sin(x * .25) / ((z + 1) * 2);

                ds.updateYAt(x, z, y);
            }
        }

        final Camera3D camera = sciChart3DBuilder.newCamera3D().build();

        final NumericAxis3D xAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(.1, .1).build();
        final NumericAxis3D yAxis = sciChart3DBuilder.newNumericAxis3D().withVisibleRange(0, 0.5).withGrowBy(.1, .1).build();
        final NumericAxis3D zAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(.1, .1).build();


        final ColumnRenderableSeries3D rs = sciChart3DBuilder.newColumnSeries3D()
                .withFill(ColorUtil.DodgerBlue)
                .withDataSeries(ds)
                .build();

        UpdateSuspender.using(surface3d, new Runnable() {
            @Override
            public void run() {
                surface3d.setCamera(camera);

                surface3d.setXAxis(xAxis);
                surface3d.setYAxis(yAxis);
                surface3d.setZAxis(zAxis);

                surface3d.getRenderableSeries().add(rs);

                surface3d.getChartModifiers().add(sciChart3DBuilder.newModifierGroupWithDefaultModifiers().build());
            }
        });
    }
}
