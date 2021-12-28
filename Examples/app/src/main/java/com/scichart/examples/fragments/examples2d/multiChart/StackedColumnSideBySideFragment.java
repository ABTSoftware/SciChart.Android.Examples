//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// StackedColumnSideBySideFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.multiChart;

import android.view.Gravity;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.numerics.labelProviders.FormatterLabelProviderBase;
import com.scichart.charting.numerics.labelProviders.LabelFormatterBase;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.renderableSeries.HorizontallyStackedColumnsCollection;
import com.scichart.charting.visuals.renderableSeries.StackedColumnRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;

import java.util.ArrayList;
import java.util.Collections;

public class StackedColumnSideBySideFragment extends ExampleSingleChartBaseFragment {

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        final ArrayList<double[]> seriesData = new ArrayList<>();
        seriesData.add(new double[]{1.269, 1.330, 1.356, 1.304});
        seriesData.add(new double[]{1.004, 1.173, Double.NaN, 1.656});
        seriesData.add(new double[]{0.282, 0.310, Double.NaN, 0.439});
        seriesData.add(new double[]{0.214, 0.243, Double.NaN, 0.313});
        seriesData.add(new double[]{0.176, 0.201, Double.NaN, 0.261});
        seriesData.add(new double[]{0.146, 0.184, 0.196, 0.276});
        seriesData.add(new double[]{0.123, 0.152, 0.177, 0.264});
        seriesData.add(new double[]{0.130, 0.156, 0.166, 0.234});
        seriesData.add(new double[]{0.147, 0.139, 0.142, 0.109});
        seriesData.add(new double[]{0.126, 0.127, 0.127, 0.094});
        seriesData.add(new double[]{2.466, 2.829, 3.005, 4.306});

        final String[] countries = {"China", "India", "USA", "Indonesia", "Brazil", "Pakistan", "Nigeria", "Bangladesh", "Russia", "Japan", "Rest Of The World", "Total"};
        final int[] fills = {0xff3399ff, 0xff014358, 0xff1f8a71, 0xffbdd63b, 0xffffe00b, 0xfff27421, 0xffbb0000, 0xff550033, 0xff339933, 0xff00aba9, 0xff560068};
        final int[] strokes = {0xff2D68BC, 0xff013547, 0xff1B5D46, 0xff7E952B, 0xffAA8F0B, 0xffA95419, 0xff840000, 0xff370018, 0xff2D732D, 0xff006C6A, 0xff3D0049};

        final IAxis xAxis = sciChartBuilder.newNumericAxis().withAutoTicks(false).withMajorDelta(1d).withMinorDelta(0.5).withLabelProvider(new YearsLabelProvider()).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withAutoRangeMode(AutoRange.Always).withAxisTitle("billions of People").withGrowBy(0, 0.1).build();

        UpdateSuspender.using(surface, () -> {
            final HorizontallyStackedColumnsCollection columnsCollection = new HorizontallyStackedColumnsCollection();
            for (int i = 0, size = seriesData.size(); i < size; i++) {
                final double[] points = seriesData.get(i);
                final IXyDataSeries<Double, Double> dataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName(countries[i]).build();
                for (int j = 0; j < points.length; j++) {
                    dataSeries.append((double) j, points[j]);
                }

                final StackedColumnRenderableSeries rSeries = sciChartBuilder.newStackedColumn()
                        .withDataSeries(dataSeries)
                        .withFillColor(fills[i])
                        .withStrokeStyle(strokes[i], 0f)
                        .build();
                sciChartBuilder.newAnimator(rSeries).withWaveTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(3000).withStartDelay(350).start();

                columnsCollection.add(rSeries);
            }

            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getRenderableSeries(), columnsCollection);

            Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroup()
                    .withLegendModifier().withPosition(Gravity.TOP | Gravity.START, 10).build()
                    .withTooltipModifier().build()
                    .build());
        });
    }

    private static class YearsLabelFormatter<T extends NumericAxis> extends LabelFormatterBase<T> {
        private final String[] _xLabels = {"2000", "2010", "2014", "2050"};

        @Override
        public void update(NumericAxis axis) { }

        @Override
        public CharSequence formatLabel(double dataValue) {
            int i = (int) dataValue;
            String result = "";
            if (i >= 0 && i < 4) {
                result = _xLabels[i];
            }
            return result;
        }

        @Override
        public CharSequence formatCursorLabel(double dataValue) {
            int i = (int) dataValue;
            String result;
            if (i >= 0 && i < 4) {
                result = _xLabels[i];
            } else if (i < 0) {
                result = _xLabels[0];
            } else {
                result = _xLabels[3];
            }
            return result;
        }
    }

    private static class YearsLabelProvider extends FormatterLabelProviderBase<NumericAxis> {
        public YearsLabelProvider() {
            super(NumericAxis.class, new YearsLabelFormatter<>());
        }
    }
}