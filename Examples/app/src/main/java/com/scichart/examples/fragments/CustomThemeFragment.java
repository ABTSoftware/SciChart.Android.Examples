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


import com.scichart.charting.model.dataSeries.IOhlcDataSeries;
import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AxisAlignment;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.data.model.DoubleRange;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.PriceSeries;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;

import butterknife.Bind;

public class CustomThemeFragment extends ExampleBaseFragment {
    @Bind(R.id.chart)
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
        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                // set theme id from styles
                surface.setTheme(R.style.SciChart_BerryBlue);

                final IAxis xBottomAxis = sciChartBuilder.newNumericAxis()
                        .withGrowBy(new DoubleRange(0.1d, 0.1d))
                        .withAxisAlignment(AxisAlignment.Bottom)
                        .withAxisTitle("Axis Bottom Title")
                        .build();

                final IAxis yRightAxis = sciChartBuilder.newNumericAxis()
                        .withGrowBy(0.1d, 0.1d)
                        .withAxisAlignment(AxisAlignment.Right)
                        .withDrawMajorBands(true)
                        .withAxisTitle("Axis Right Title")
                        .withAxisId("PrimaryAxisId")
                        .withTextFormatting("#.0")
                        .build();

                final IAxis yLeftAxis = sciChartBuilder.newNumericAxis()
                        .withGrowBy(0, 3d)
                        .withAxisAlignment(AxisAlignment.Left)
                        .withAxisTitle("Axis Left Title")
                        .withAxisId("SecondaryAxisId")
                        .withTextFormatting("###E+0")
                        .build();

                final DataManager dataManager = DataManager.getInstance();
                final PriceSeries priceBars = dataManager.getPriceDataIndu(getActivity());
                final IXyDataSeries<Double, Double> mountainSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Mountain Series").build();
                final IXyDataSeries<Double, Double> lineSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Line Series").build();
                final IXyDataSeries<Double, Long> columnSeries = sciChartBuilder.newXyDataSeries(Double.class, Long.class).withSeriesName("Column Series").build();
                final IOhlcDataSeries<Double, Double> candlestickSeries = sciChartBuilder.newOhlcDataSeries(Double.class, Double.class).withSeriesName("Candlestick Series").build();

                mountainSeries.append(priceBars.getIndexesAsDouble(), dataManager.offset(priceBars.getLowData(), -1000));
                candlestickSeries.append(priceBars.getIndexesAsDouble(), priceBars.getOpenData(), priceBars.getHighData(), priceBars.getLowData(), priceBars.getCloseData());
                lineSeries.append(priceBars.getIndexesAsDouble(), dataManager.computeMovingAverage(priceBars.getCloseData(), 50));
                columnSeries.append(priceBars.getIndexesAsDouble(), priceBars.getVolumeData());

                final IRenderableSeries mountainRenderableSeries = sciChartBuilder.newMountainSeries()
                        .withDataSeries(mountainSeries)
                        .withYAxisId("PrimaryAxisId")
                        .build();

                final IRenderableSeries lineRenderableSeries = sciChartBuilder.newLineSeries()
                        .withDataSeries(lineSeries)
                        .withYAxisId("PrimaryAxisId")
                        .build();

                final IRenderableSeries columnRenderableSeries = sciChartBuilder.newColumnSeries()
                        .withDataSeries(columnSeries)
                        .withYAxisId("SecondaryAxisId")
                        .build();

                final IRenderableSeries candlestickRenderableSeries = sciChartBuilder.newCandlestickSeries()
                        .withDataSeries(candlestickSeries)
                        .withYAxisId("PrimaryAxisId")
                        .build();


                surface.getChartModifiers().add(sciChartBuilder.newModifierGroupWithDefaultModifiers()
                        .withLegendModifier().withShowCheckBoxes(false).build()
                        .build());

                Collections.addAll(surface.getXAxes(), xBottomAxis);
                Collections.addAll(surface.getYAxes(), yRightAxis, yLeftAxis);
                Collections.addAll(surface.getRenderableSeries(), mountainRenderableSeries, columnRenderableSeries, candlestickRenderableSeries, lineRenderableSeries);
            }
        });
    }
}
