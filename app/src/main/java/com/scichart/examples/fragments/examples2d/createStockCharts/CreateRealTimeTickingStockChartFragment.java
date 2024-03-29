//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CreateRealTimeTickingStockChartFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.createStockCharts;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.scichart.charting.Direction2D;
import com.scichart.charting.model.dataSeries.IOhlcDataSeries;
import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.annotations.AnnotationCoordinateMode;
import com.scichart.charting.visuals.annotations.AxisMarkerAnnotation;
import com.scichart.charting.visuals.annotations.BoxAnnotation;
import com.scichart.charting.visuals.annotations.VerticalLineAnnotation;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.CategoryDateAxis;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastMountainRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastOhlcRenderableSeries;
import com.scichart.core.annotations.Orientation;
import com.scichart.core.common.Action1;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.data.model.DoubleRange;
import com.scichart.data.model.IRange;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.data.IMarketDataService;
import com.scichart.examples.data.MarketDataService;
import com.scichart.examples.data.MovingAverage;
import com.scichart.examples.data.PriceBar;
import com.scichart.examples.data.PriceSeries;
import com.scichart.examples.databinding.ExampleRealTimeTickingStockChartFragmentBinding;
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget;
import com.scichart.examples.utils.widgetgeneration.Widget;
import com.scichart.extensions.builders.SciChartBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CreateRealTimeTickingStockChartFragment extends ExampleBaseFragment<ExampleRealTimeTickingStockChartFragmentBinding> {

    private static final int SECONDS_IN_FIVE_MINUTES = 5 * 60;
    public static final int DEFAULT_POINT_COUNT = 150;
    public static final int SMA_SERIES_COLOR = 0xFFFFA500;
    public static final int STROKE_UP_COLOR = 0xFF00AA00;
    public static final int STROKE_DOWN_COLOR = 0xFFFF0000;
    public static final float STROKE_THICKNESS = 1.5f;

    private final IOhlcDataSeries<Date, Double> ohlcDataSeries = sciChartBuilder.newOhlcDataSeries(Date.class, Double.class).withSeriesName("Price Series").build();
    private final IXyDataSeries<Date, Double> xyDataSeries = sciChartBuilder.newXyDataSeries(Date.class, Double.class).withSeriesName("50-Period SMA").build();

    private final AxisMarkerAnnotation smaAxisMarker = sciChartBuilder.newAxisMarkerAnnotation().withY1(0d).withBackgroundColor(SMA_SERIES_COLOR).build();
    private final AxisMarkerAnnotation ohlcAxisMarker = sciChartBuilder.newAxisMarkerAnnotation().withY1(0d).withBackgroundColor(STROKE_UP_COLOR).build();

    private IMarketDataService marketDataService;
    private final MovingAverage sma50 = new MovingAverage(50);
    private PriceBar lastPrice;

    private OverviewPrototype overviewPrototype;

    @NonNull
    @Override
    public List<Widget> getToolbarItems() {
        return new ArrayList<Widget>() {
            {
                add(new ImageViewWidget.Builder().setId(R.drawable.example_toolbar_play).setListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        marketDataService.subscribePriceUpdate(onNewPrice());
                    }
                }).build());
                add(new ImageViewWidget.Builder().setId(R.drawable.example_toolbar_pause).setListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        marketDataService.clearSubscriptions();
                    }
                }).build());
            }
        };
    }

    @NonNull
    @Override
    protected ExampleRealTimeTickingStockChartFragmentBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ExampleRealTimeTickingStockChartFragmentBinding.inflate(inflater);
    }

    @Override
    protected void initExample(@NonNull ExampleRealTimeTickingStockChartFragmentBinding binding) {
        binding.surface.setTheme(R.style.SciChart_NavyBlue);
        binding.overview.setTheme(R.style.SciChart_NavyBlue);

        // Market data service simulates live ticks. We want to load the chart with 150 historical bars then later do real-time ticking as new data comes in
        this.marketDataService = new MarketDataService(new Date(2000, 8, 1, 12, 0, 0), 5, 20);
        initChart();
    }

    private void initChart() {
        initializeMainChart(binding.surface);
        overviewPrototype = new OverviewPrototype(binding.surface, binding.overview);
    }

    private void initializeMainChart(final SciChartSurface surface) {
        final CategoryDateAxis xAxis = sciChartBuilder.newCategoryDateAxis()
                .withBarTimeFrame(SECONDS_IN_FIVE_MINUTES)
                .withDrawMinorGridLines(false)
                .withGrowBy(0, 0.1)
                .build();
        final NumericAxis yAxis = sciChartBuilder.newNumericAxis().withAutoRangeMode(AutoRange.Always).build();

        final FastOhlcRenderableSeries ohlc = sciChartBuilder.newOhlcSeries()
                .withStrokeUp(STROKE_UP_COLOR, STROKE_THICKNESS)
                .withStrokeDown(STROKE_DOWN_COLOR, STROKE_THICKNESS)
                .withStrokeStyle(STROKE_UP_COLOR)
                .withDataSeries(ohlcDataSeries)
                .build();
        final FastLineRenderableSeries line = sciChartBuilder.newLineSeries().withStrokeStyle(SMA_SERIES_COLOR, STROKE_THICKNESS).withDataSeries(xyDataSeries).build();

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getRenderableSeries(), ohlc, line);
            Collections.addAll(surface.getAnnotations(), smaAxisMarker, ohlcAxisMarker);
            Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroup()
                    .withXAxisDragModifier().build()
                    .withZoomPanModifier().withReceiveHandledEvents(true).withXyDirection(Direction2D.XDirection).build()
                    .withZoomExtentsModifier().build()
                    .withLegendModifier().withOrientation(Orientation.HORIZONTAL).withPosition(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 20).withReceiveHandledEvents(true).build()
                    .build());
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull final Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putInt("count", ohlcDataSeries.getCount());

        IRange<?> range = binding.surface.getXAxes().get(0).getVisibleRange();
        savedInstanceState.putDouble("rangeMin", range.getMinAsDouble());
        savedInstanceState.putDouble("rangeMax", range.getMaxAsDouble());
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final SciChartSurface surface = binding.surface;
        UpdateSuspender.using(surface, () -> {
            int count = DEFAULT_POINT_COUNT;
            if (savedInstanceState != null) {
                count = savedInstanceState.getInt("count");

                double rangeMin = savedInstanceState.getDouble("rangeMin");
                double rangeMax = savedInstanceState.getDouble("rangeMax");

                surface.getXAxes().get(0).getVisibleRange().setMinMaxDouble(rangeMin, rangeMax);
            }
            PriceSeries prices = marketDataService.getHistoricalData(count);

            ohlcDataSeries.append(prices.getDateData(), prices.getOpenData(), prices.getHighData(), prices.getLowData(), prices.getCloseData());
            xyDataSeries.append(prices.getDateData(), getSmaCurrentValues(prices));

            overviewPrototype.getOverviewDataSeries().append(prices.getDateData(), prices.getCloseData());

            marketDataService.subscribePriceUpdate(onNewPrice());
        });
    }

    private List<Double> getSmaCurrentValues(PriceSeries prices) {
        List<Double> result = new ArrayList<>();
        List<Double> closeData = prices.getCloseData();

        for (int i = 0, size = closeData.size(); i < size; i++) {
            Double close = closeData.get(i);
            result.add(sma50.push(close).getCurrent());
        }

        return result;
    }

    @NonNull
    private synchronized Action1<PriceBar> onNewPrice() {
        return price -> {
            // Update the last price, or append?
            double smaLastValue;
            final IXyDataSeries<Date, Double> overviewDataSeries = overviewPrototype.getOverviewDataSeries();

            if (lastPrice != null && lastPrice.getDate() == price.getDate()) {
                ohlcDataSeries.update(ohlcDataSeries.getCount() - 1, price.getOpen(), price.getHigh(), price.getLow(), price.getClose());

                smaLastValue = sma50.update(price.getClose()).getCurrent();
                xyDataSeries.updateYAt(xyDataSeries.getCount() - 1, smaLastValue);

                overviewDataSeries.updateYAt(overviewDataSeries.getCount() - 1, price.getClose());
            } else {
                ohlcDataSeries.append(price.getDate(), price.getOpen(), price.getHigh(), price.getLow(), price.getClose());

                smaLastValue = sma50.push(price.getClose()).getCurrent();
                xyDataSeries.append(price.getDate(), smaLastValue);

                overviewDataSeries.append(price.getDate(), price.getClose());

                // If the latest appending point is inside the viewport (i.e. not off the edge of the screen)
                // then scroll the viewport 1 bar, to keep the latest bar at the same place
                final IRange<?> visibleRange = binding.surface.getXAxes().get(0).getVisibleRange();
                if (visibleRange.getMaxAsDouble() > ohlcDataSeries.getCount()) {
                    visibleRange.setMinMaxDouble(visibleRange.getMinAsDouble() + 1, visibleRange.getMaxAsDouble() + 1);
                }
            }

            requireActivity().runOnUiThread(() -> ohlcAxisMarker.setBackgroundColor(price.getClose() >= price.getOpen() ? STROKE_UP_COLOR : STROKE_DOWN_COLOR));

            smaAxisMarker.setY1(smaLastValue);
            ohlcAxisMarker.setY1(price.getClose());
            lastPrice = price;
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        marketDataService.stopGenerator();
    }

    private static class OverviewPrototype {
        private final SciChartBuilder builder = SciChartBuilder.instance();

        private final BoxAnnotation leftBox = generateBoxAnnotation(R.drawable.example_grayed_out_box_annotation_background);
        private final BoxAnnotation rightBox = generateBoxAnnotation(R.drawable.example_grayed_out_box_annotation_background);
        private final BoxAnnotation boxAnnotation = generateBoxAnnotation(0);
        private final VerticalLineAnnotation leftLineGrip = generateVerticalLine();
        private final VerticalLineAnnotation rightLineGrip = generateVerticalLine();

        private final IRange<Double> parentXAxisVisibleRange;
        private IRange<Double> overviewXAxisVisibleRange;
        private final IXyDataSeries<Date, Double> overviewDataSeries = builder.newXyDataSeries(Date.class, Double.class).withAcceptsUnsortedData().build();

        @SuppressWarnings("unchecked")
        OverviewPrototype(SciChartSurface parentSurface, SciChartSurface fakeOverviewSurface) {
            final IAxis parentXAxis = parentSurface.getXAxes().get(0);
            parentXAxisVisibleRange = parentXAxis.getVisibleRange();

            parentXAxis.setVisibleRangeChangeListener((axis, oldRange, newRange, isAnimating) -> {
                final double newMin = newRange.getMinAsDouble();
                final double newMax = newRange.getMaxAsDouble();

                if (!overviewXAxisVisibleRange.equals(new DoubleRange(0d, 10d))) {
                    parentXAxisVisibleRange.setMinMaxWithLimit(newMin, newMax, overviewXAxisVisibleRange);
                } else {
                    parentXAxisVisibleRange.setMinMax(newMin, newMax);
                }

                boxAnnotation.setX1(parentXAxisVisibleRange.getMin());
                boxAnnotation.setX2(parentXAxisVisibleRange.getMax());

                leftLineGrip.setX1(parentXAxisVisibleRange.getMin());
                leftBox.setX1(overviewXAxisVisibleRange.getMin());
                leftBox.setX2(parentXAxisVisibleRange.getMin());

                rightLineGrip.setX1(parentXAxisVisibleRange.getMax());
                rightBox.setX1(parentXAxisVisibleRange.getMax());
                rightBox.setX2(overviewXAxisVisibleRange.getMax());
            });

            initializeOverview(fakeOverviewSurface);

            overviewDataSeries.addObserver((dataSeries, dataSeriesUpdate) -> {
                rightBox.setX1(parentXAxisVisibleRange.getMax());
                rightBox.setX2(overviewXAxisVisibleRange.getMax());
            });
        }

        IXyDataSeries<Date, Double> getOverviewDataSeries() {
            return overviewDataSeries;
        }

        private void initializeOverview(final SciChartSurface surface) {
            surface.setRenderableSeriesAreaBorderStyle(null);

            final CategoryDateAxis xAxis = builder.newCategoryDateAxis()
                    .withBarTimeFrame(SECONDS_IN_FIVE_MINUTES)
                    .withAutoRangeMode(AutoRange.Always)
                    .withDrawMinorGridLines(false)
                    .withVisibility(View.GONE)
                    .withGrowBy(0, 0.1)
                    .build();
            overviewXAxisVisibleRange = xAxis.getVisibleRange();

            final NumericAxis yAxis = builder.newNumericAxis().withAutoRangeMode(AutoRange.Always).withVisibility(View.INVISIBLE).build();
            removeAxisGridLines(xAxis, yAxis);

            final FastMountainRenderableSeries mountain = builder.newMountainSeries().withDataSeries(overviewDataSeries).build();

            UpdateSuspender.using(surface, new Runnable() {
                @Override
                public synchronized void run() {
                    Collections.addAll(surface.getXAxes(), xAxis);
                    Collections.addAll(surface.getYAxes(), yAxis);
                    Collections.addAll(surface.getRenderableSeries(), mountain);
                    Collections.addAll(surface.getAnnotations(), boxAnnotation, leftBox, rightBox, leftLineGrip, rightLineGrip);
                }
            });
        }

        private BoxAnnotation generateBoxAnnotation(@DrawableRes int backgroundDrawable) {
            return builder.newBoxAnnotation()
                    .withBackgroundDrawableId(backgroundDrawable)
                    .withCoordinateMode(AnnotationCoordinateMode.RelativeY)
                    .withY1(0).withY2(1)
                    .build();
        }

        private VerticalLineAnnotation generateVerticalLine() {
            return builder.newVerticalLineAnnotation()
                    .withX1(0)
                    .withY1(0.3).withY2(0.7)
                    .withCoordinateMode(AnnotationCoordinateMode.RelativeY)
                    .withVerticalGravity(Gravity.CENTER_VERTICAL)
                    .withStroke(5, ColorUtil.Grey)
                    .build();
        }

        private void removeAxisGridLines(IAxis... axes) {
            for (IAxis axis : axes) {
                axis.setDrawMajorGridLines(false);
                axis.setDrawMajorTicks(false);
                axis.setDrawMajorBands(false);
                axis.setDrawMinorGridLines(false);
                axis.setDrawMinorTicks(false);
            }
        }
    }
}