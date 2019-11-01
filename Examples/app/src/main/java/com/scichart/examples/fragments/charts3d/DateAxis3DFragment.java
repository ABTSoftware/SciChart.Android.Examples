//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2018. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// DateAxis3DFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.charts3d;

import com.scichart.charting3d.model.dataSeries.waterfall.WaterfallDataSeries3D;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.axes.DateAxis3D;
import com.scichart.charting3d.visuals.axes.NumericAxis3D;
import com.scichart.charting3d.visuals.renderableSeries.data.GradientColorPalette;
import com.scichart.charting3d.visuals.renderableSeries.waterfall.WaterfallRenderableSeries3D;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.utility.DateIntervalUtil;
import com.scichart.examples.R;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;

import static com.scichart.drawing.utility.ColorUtil.Blue;
import static com.scichart.drawing.utility.ColorUtil.DarkGreen;
import static com.scichart.drawing.utility.ColorUtil.GreenYellow;
import static com.scichart.drawing.utility.ColorUtil.Orange;
import static com.scichart.drawing.utility.ColorUtil.Red;
import static com.scichart.drawing.utility.ColorUtil.Yellow;

public class DateAxis3DFragment extends ExampleBaseFragment {

    @BindView(R.id.chart3d)
    SciChartSurface3D surface3d;

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart3d_fragment;
    }

    @Override
    protected void initExample() {
        final DateAxis3D xAxis = sciChart3DBuilder.newDateAxis3D().withSubDayTextFormatting("HH:mm").withMaxAutoTicks(8).build();
        final NumericAxis3D yAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(0, .1).build();
        final DateAxis3D zAxis = sciChart3DBuilder.newDateAxis3D().withTextFormatting("dd MMM").withMaxAutoTicks(5).build();

        final Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(2019, Calendar.MAY, 1);

        final Date date = calendar.getTime();

        final int daysCount = 7;
        final int measurementsCount = 24;
        
        final WaterfallDataSeries3D<Date, Double, Date> ds = new WaterfallDataSeries3D<>(Date.class, Double.class, Date.class, measurementsCount, daysCount);

        ds.setStartX(date);
        ds.setStepX(new Date(DateIntervalUtil.fromMinutes(30)));

        ds.setStartZ(date);
        ds.setStepZ(new Date(DateIntervalUtil.fromDays(1)));

        for (int z = 0; z < daysCount; z++) {
            final double[] temperatures = TEMPERATURES[z];
            for (int x = 0; x < measurementsCount; x++) {
                ds.updateYAt(x, z, temperatures[x]);
            }
        }

        final int[] fillColors = new int[] {Red, Orange, Yellow, GreenYellow, DarkGreen};
        final float[] fillStops = new float[] {0, .25f, .5f, .75f, 1};

        final GradientColorPalette gradientFillColorPalette = new GradientColorPalette(fillColors, fillStops);

        final WaterfallRenderableSeries3D rs = sciChart3DBuilder.newWaterfallSeries3D()
                .withDataSeries(ds)
                .withStroke(Blue)
                .withStrokeThicknes(1f)
                .withSliceThickness(2f)
                .withYColorMapping(gradientFillColorPalette)
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

    private static final double[][] TEMPERATURES = new double[][]{
            // day 1
            {
                    8, 8, 7, 7, 6, 6, 6, 6,
                    6, 6, 6, 7, 7, 7, 8, 9,
                    9, 10, 10, 10, 10, 10, 9, 9
            },
            // day 2
            {
                    9, 7, 7, 7, 6, 6, 6, 6,
                    7, 7, 8, 9, 9, 12, 15, 16,
                    16, 16, 17, 16, 15, 13, 12, 11,
            },
            // day 3
            {
                    11, 10, 9, 11, 7, 7, 7, 9,
                    11, 13, 15, 16, 17, 18, 17, 18,
                    19, 19, 18, 10, 10, 11, 10, 10
            },
            // day 4
            {
                    11, 10, 11, 10, 11, 10, 10, 11,
                    11, 13, 13, 13, 15, 15, 15, 16,
                    17, 18, 17, 17, 15, 13, 12, 11
            },
            // day 5
            {
                    13, 14, 12, 12, 11, 12, 12, 12,
                    13, 15, 17, 18, 20, 21, 21, 22,
                    22, 21, 20, 19, 17, 16, 15, 16
            },
            // day 6
            {
                    16, 16, 16, 15, 14, 14, 14, 12,
                    13, 13, 14, 14, 13, 15, 15, 15,
                    15, 15, 14, 15, 15, 14, 14, 14
            },
            // day 7
            {
                    14, 15, 14, 13, 14, 13, 13, 14,
                    14, 16, 18, 17, 16, 18, 20, 19,
                    16, 16, 16, 16, 15, 14, 13, 12
            }
    };
}
