//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// StackedColumnSideBySideFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;

import android.view.Gravity;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.numerics.labelProviders.FormatterLabelProviderBase;
import com.scichart.charting.numerics.labelProviders.ILabelFormatter;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.renderableSeries.HorizontallyStackedColumnsCollection;
import com.scichart.charting.visuals.renderableSeries.StackedColumnRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.utility.ComparableUtil;
import com.scichart.examples.R;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;

import butterknife.Bind;

public class StackedColumnSideBySideFragment extends ExampleBaseFragment {
    @Bind(R.id.chart)
    SciChartSurface surface;

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart_fragment;
    }

    @Override
    protected void initExample() {
        final double[] china = new double[]{1.269, 1.330, 1.356, 1.304};
        final double[] india = new double[]{1.004, 1.173, 1.236, 1.656};
        final double[] usa = new double[]{0.282, 0.310, 0.319, 0.439};
        final double[] indonesia = new double[]{0.214, 0.243, 0.254, 0.313};
        final double[] brazil = new double[]{0.176, 0.201, 0.203, 0.261};
        final double[] pakistan = new double[]{0.146, 0.184, 0.196, 0.276};
        final double[] nigeria = new double[]{0.123, 0.152, 0.177, 0.264};
        final double[] bangladesh = new double[]{0.130, 0.156, 0.166, 0.234};
        final double[] russia = new double[]{0.147, 0.139, 0.142, 0.109};
        final double[] japan = new double[]{0.126, 0.127, 0.127, 0.094};
        final double[] restOfTheWorld = new double[]{2.466, 2.829, 3.005, 4.306};

        final IXyDataSeries<Double, Double> chinaDataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("China").build();
        final IXyDataSeries<Double, Double> indiaDataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("India").build();
        final IXyDataSeries<Double, Double> usaDataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("USA").build();
        final IXyDataSeries<Double, Double> indonesiaDataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Indonesia").build();
        final IXyDataSeries<Double, Double> brazilDataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Brazil").build();
        final IXyDataSeries<Double, Double> pakistanDataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Pakistan").build();
        final IXyDataSeries<Double, Double> nigeriaDataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Nigeria").build();
        final IXyDataSeries<Double, Double> bangladeshDataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Bangladesh").build();
        final IXyDataSeries<Double, Double> russiaDataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Russia").build();
        final IXyDataSeries<Double, Double> japanDataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Japan").build();
        final IXyDataSeries<Double, Double> restOfTheWorldDataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Rest Of The World").build();
        final IXyDataSeries<Double, Double> totalDataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Total").build();

        for (int i = 0; i < 4; i++) {
            double xValue = i;
            chinaDataSeries.append(xValue, china[i]);
            if (i != 2) {
                indiaDataSeries.append(xValue, india[i]);
                usaDataSeries.append(xValue, usa[i]);
                indonesiaDataSeries.append(xValue, indonesia[i]);
                brazilDataSeries.append(xValue, brazil[i]);
            } else {
                indiaDataSeries.append(xValue, Double.NaN);
                usaDataSeries.append(xValue, Double.NaN);
                indonesiaDataSeries.append(xValue, Double.NaN);
                brazilDataSeries.append(xValue, Double.NaN);
            }
            pakistanDataSeries.append(xValue, pakistan[i]);
            nigeriaDataSeries.append(xValue, nigeria[i]);
            bangladeshDataSeries.append(xValue, bangladesh[i]);
            russiaDataSeries.append(xValue, russia[i]);
            japanDataSeries.append(xValue, japan[i]);
            restOfTheWorldDataSeries.append(xValue, restOfTheWorld[i]);
            totalDataSeries.append(xValue, china[i] + india[i] + usa[i] + indonesia[i] + brazil[i] + pakistan[i] + nigeria[i] + bangladesh[i] + russia[i] + japan[i] + restOfTheWorld[i]);
        }

        final IAxis xAxis = sciChartBuilder.newNumericAxis().withAutoTicks(false).withMajorDelta(1d).withMinorDelta(0.5).withDrawMajorBands(true).withLabelProvider(new YearsLabelProvider()).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withAutoRangeMode(AutoRange.Always).withAxisTitle("billions of People").withGrowBy(0, 0.1).withDrawMajorBands(true).build();

        final StackedColumnRenderableSeries s1 = sciChartBuilder.newStackedColumn().withDataSeries(chinaDataSeries).withFillColor(0xff3399ff).withStrokeStyle(0xff2D68BC, 0f).build();
        final StackedColumnRenderableSeries s2 = sciChartBuilder.newStackedColumn().withDataSeries(indiaDataSeries).withFillColor(0xff014358).withStrokeStyle(0xff013547, 0f).build();
        final StackedColumnRenderableSeries s3 = sciChartBuilder.newStackedColumn().withDataSeries(usaDataSeries).withFillColor(0xff1f8a71).withStrokeStyle(0xff1B5D46, 0f).build();
        final StackedColumnRenderableSeries s4 = sciChartBuilder.newStackedColumn().withDataSeries(indonesiaDataSeries).withFillColor(0xffbdd63b).withStrokeStyle(0xff7E952B, 0f).build();
        final StackedColumnRenderableSeries s5 = sciChartBuilder.newStackedColumn().withDataSeries(brazilDataSeries).withFillColor(0xffffe00b).withStrokeStyle(0xffAA8F0B, 0f).build();
        final StackedColumnRenderableSeries s6 = sciChartBuilder.newStackedColumn().withDataSeries(pakistanDataSeries).withFillColor(0xfff27421).withStrokeStyle(0xffA95419, 0f).build();
        final StackedColumnRenderableSeries s7 = sciChartBuilder.newStackedColumn().withDataSeries(nigeriaDataSeries).withFillColor(0xffbb0000).withStrokeStyle(0xff840000, 0f).build();
        final StackedColumnRenderableSeries s8 = sciChartBuilder.newStackedColumn().withDataSeries(bangladeshDataSeries).withFillColor(0xff550033).withStrokeStyle(0xff370018, 0f).build();
        final StackedColumnRenderableSeries s9 = sciChartBuilder.newStackedColumn().withDataSeries(russiaDataSeries).withFillColor(0xff339933).withStrokeStyle(0xff2D732D, 0f).build();
        final StackedColumnRenderableSeries s10 = sciChartBuilder.newStackedColumn().withDataSeries(japanDataSeries).withFillColor(0xff00aba9).withStrokeStyle(0xff006C6A, 0f).build();
        final StackedColumnRenderableSeries s11 = sciChartBuilder.newStackedColumn().withDataSeries(restOfTheWorldDataSeries).withFillColor(0xff560068).withStrokeStyle(0xff3D0049, 0f).build();

        final HorizontallyStackedColumnsCollection columnsCollection = new HorizontallyStackedColumnsCollection();
        columnsCollection.add(s1);
        columnsCollection.add(s2);
        columnsCollection.add(s3);
        columnsCollection.add(s4);
        columnsCollection.add(s5);
        columnsCollection.add(s6);
        columnsCollection.add(s7);
        columnsCollection.add(s8);
        columnsCollection.add(s9);
        columnsCollection.add(s10);
        columnsCollection.add(s11);

        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                Collections.addAll(surface.getRenderableSeries(), columnsCollection);
                Collections.addAll(surface.getXAxes(), xAxis);
                Collections.addAll(surface.getYAxes(), yAxis);
                Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroup()
                        .withLegendModifier().withPosition(Gravity.TOP | Gravity.LEFT, 10).build()
                        .withTooltipModifier().build()
                        .build());
            }
        });
    }

    private static class YearsLabelFormatter implements ILabelFormatter<NumericAxis> {
        private final String[] _xLabels = {"2000", "2010", "2014", "2050"};

        @Override
        public void update(NumericAxis axis) {
        }

        @Override
        public CharSequence formatLabel(Comparable dataValue) {

            int i = (int) ComparableUtil.toDouble(dataValue);
            String result = "";
            if (i >= 0 && i < 4) {
                result = _xLabels[i];
            }
            return result;
        }

        @Override
        public CharSequence formatCursorLabel(Comparable dataValue) {
            int i = (int) ComparableUtil.toDouble(dataValue);
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
            super(NumericAxis.class, new YearsLabelFormatter());
        }
    }
}