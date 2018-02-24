//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CustomThemeFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;

import com.daasuu.ei.Ease;
import com.daasuu.ei.EasingInterpolator;
import com.scichart.charting.model.dataSeries.IOhlcDataSeries;
import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.AxisAlignment;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.FastCandlestickRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastColumnRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastMountainRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.PriceSeries;
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.examples.utils.BillionsLabelProvider;
import com.scichart.examples.utils.ThousandsLabelProvider;

import java.util.Collections;

import butterknife.BindView;

public class CustomThemeFragment extends ExampleBaseFragment {
    @BindView(R.id.chart)
    SciChartSurface surface;

    @Override
    public boolean showDefaultModifiersInToolbar() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart_fragment;
    }

    @Override
    protected void initExample() {
        final IAxis xBottomAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1d, 0.1d).withVisibleRange(150, 180).build();

        final IAxis yRightAxis = sciChartBuilder.newNumericAxis()
                .withGrowBy(0.1d, 0.1d)
                .withAxisAlignment(AxisAlignment.Right)
                .withAutoRangeMode(AutoRange.Always)
                .withAxisId("PrimaryAxisId")
                .withDrawMajorTicks(false)
                .withDrawMinorTicks(false)
                .withLabelProvider(new ThousandsLabelProvider())
                .build();

        final IAxis yLeftAxis = sciChartBuilder.newNumericAxis()
                .withGrowBy(0, 3d)
                .withAxisAlignment(AxisAlignment.Left)
                .withAutoRangeMode(AutoRange.Always)
                .withAxisId("SecondaryAxisId")
                .withDrawMajorTicks(false)
                .withDrawMinorTicks(false)
                .withLabelProvider(new BillionsLabelProvider())
                .build();

        final DataManager dataManager = DataManager.getInstance();
        final PriceSeries priceBars = dataManager.getPriceDataIndu(getActivity());

        final IXyDataSeries<Double, Double> mountainDataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Mountain Series").build();
        final IXyDataSeries<Double, Double> lineDataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Line Series").build();
        final IXyDataSeries<Double, Long> columnDataSeries = sciChartBuilder.newXyDataSeries(Double.class, Long.class).withSeriesName("Column Series").build();
        final IOhlcDataSeries<Double, Double> candlestickDataSeries = sciChartBuilder.newOhlcDataSeries(Double.class, Double.class).withSeriesName("Candlestick Series").build();

        mountainDataSeries.append(priceBars.getIndexesAsDouble(), dataManager.offset(priceBars.getLowData(), -1000));
        candlestickDataSeries.append(priceBars.getIndexesAsDouble(), priceBars.getOpenData(), priceBars.getHighData(), priceBars.getLowData(), priceBars.getCloseData());
        lineDataSeries.append(priceBars.getIndexesAsDouble(), dataManager.computeMovingAverage(priceBars.getCloseData(), 50));
        columnDataSeries.append(priceBars.getIndexesAsDouble(), priceBars.getVolumeData());

        final FastMountainRenderableSeries mountainSeries = sciChartBuilder.newMountainSeries().withDataSeries(mountainDataSeries).withYAxisId("PrimaryAxisId").build();
        final FastLineRenderableSeries lineSeries = sciChartBuilder.newLineSeries().withDataSeries(lineDataSeries).withYAxisId("PrimaryAxisId").build();
        final FastColumnRenderableSeries columnSeries = sciChartBuilder.newColumnSeries().withDataSeries(columnDataSeries).withYAxisId("SecondaryAxisId").build();
        final FastCandlestickRenderableSeries candlestickSeries = sciChartBuilder.newCandlestickSeries().withDataSeries(candlestickDataSeries).withYAxisId("PrimaryAxisId").build();

        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                // set theme id from styles
                surface.setTheme(R.style.SciChart_BerryBlue);

                Collections.addAll(surface.getXAxes(), xBottomAxis);
                Collections.addAll(surface.getYAxes(), yRightAxis, yLeftAxis);
                Collections.addAll(surface.getRenderableSeries(), mountainSeries, columnSeries, candlestickSeries, lineSeries);
                Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().withLegendModifier().withShowCheckBoxes(false).build().build());

                sciChartBuilder.newAnimator(mountainSeries).withScaleTransformation(10500d).withInterpolator(new EasingInterpolator(Ease.ELASTIC_OUT)).withDuration(3000).withStartDelay(350).start();
                sciChartBuilder.newAnimator(candlestickSeries).withScaleTransformation(11700d).withInterpolator(new EasingInterpolator(Ease.ELASTIC_OUT)).withDuration(3000).withStartDelay(350).start();
                sciChartBuilder.newAnimator(lineSeries).withScaleTransformation(12250d).withInterpolator(new EasingInterpolator(Ease.ELASTIC_OUT)).withDuration(3000).withStartDelay(350).start();
                sciChartBuilder.newAnimator(columnSeries).withScaleTransformation(10500d).withInterpolator(new EasingInterpolator(Ease.ELASTIC_OUT)).withDuration(3000).withStartDelay(350).start();
            }
        });
    }
}