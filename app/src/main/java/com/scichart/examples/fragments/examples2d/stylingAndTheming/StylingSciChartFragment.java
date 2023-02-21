//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CustomThemeFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.stylingAndTheming;

import android.graphics.Color;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.IOhlcDataSeries;
import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.AxisAlignment;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker;
import com.scichart.charting.visuals.renderableSeries.FastCandlestickRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastColumnRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastMountainRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.drawing.common.SolidBrushStyle;
import com.scichart.drawing.common.SolidPenStyle;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.PriceSeries;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;
import com.scichart.examples.utils.BillionsLabelProvider;
import com.scichart.examples.utils.Constant;
import com.scichart.examples.utils.ThousandsLabelProvider;
import com.scichart.examples.utils.interpolator.DefaultInterpolator;
import com.scichart.examples.utils.interpolator.ElasticOutInterpolator;

import java.util.Collections;

public class StylingSciChartFragment extends ExampleSingleChartBaseFragment {

    @Override
    public boolean showDefaultModifiersInToolbar() {
        return false;
    }

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        // surface background. If you set color for chart background than it is color only for axes area
        surface.setBackgroundColor(ColorUtil.Orange);
        // chart area (viewport) background fill color
        surface.setRenderableSeriesAreaFillStyle(new SolidBrushStyle(0xFFFFB6C1));
        // chart area border color and thickness
        surface.setRenderableSeriesAreaBorderStyle(new SolidPenStyle(0xFF4682b4, false, 2f, null));

        // Create the XAxis
        final IAxis xAxis = sciChartBuilder.newNumericAxis()
                .withGrowBy(0.1d, 0.1d)
                .withVisibleRange(150, 180)
                // Brushes and styles for the XAxis, vertical gridlines, vertical tick marks, vertical axis bands and xAxis labels
                .withAxisBandsFill(0x55ff6655)
                .withMajorGridLineStyle(ColorUtil.Green, 1f)
                .withMinorGridLineStyle(sciChartBuilder.newPen().withColor(ColorUtil.Yellow).withThickness(0.5f).withStrokeDashArray(new float[]{10.0f, 3.0f, 10.0f, 3.0f}).build())
                .withTickLabelStyle(sciChartBuilder.newFont().withTextColor(ColorUtil.Purple).withTextSize(14f).build())
                .withDrawMajorTicks(true)
                .withDrawMinorTicks(true)
                .withDrawMajorGridLines(true)
                .withDrawMinorGridLines(true)
                .withDrawLabels(true)
                .withMajorTickLineLength(5)
                .withMajorTickLineStyle(ColorUtil.Green, 1f)
                .withMinorTickLineLength(2)
                .withMinorTickLineStyle(sciChartBuilder.newPen().withColor(ColorUtil.Yellow).withThickness(0.5f).withStrokeDashArray(new float[]{10.0f, 3.0f, 10.0f, 3.0f}).build())
                .build();

        // Create the Right YAxis
        final IAxis yRightAxis = sciChartBuilder.newNumericAxis()
                .withGrowBy(0.1d, 0.1d)
                .withAxisAlignment(AxisAlignment.Right)
                .withAutoRangeMode(AutoRange.Always)
                .withAxisId("PrimaryAxisId")
                // Brushes and styles for the Right YAxis, horizontal gridlines, horizontal tick marks, horizontal axis bands and right yAxis labels
                .withAxisBandsFill(0x55ff6655)
                .withMajorGridLineStyle(ColorUtil.Green, 1f)
                .withMinorGridLineStyle(sciChartBuilder.newPen().withColor(ColorUtil.Yellow).withThickness(0.5f).withStrokeDashArray(new float[]{10.0f, 3.0f, 10.0f, 3.0f}).build())
                .withLabelProvider(new ThousandsLabelProvider()) // see LabelProvider API documentation for more info
                .withTickLabelStyle(sciChartBuilder.newFont().withTextSize(14).withTextColor(ColorUtil.Green).build())
                .withDrawMajorTicks(true)
                .withDrawMinorTicks(true)
                .withDrawLabels(true)
                .withDrawMajorGridLines(true)
                .withDrawMinorGridLines(true)
                .withMajorTickLineLength(3)
                .withMajorTickLineStyle(ColorUtil.Purple, 1f)
                .withMinorTickLineLength(2)
                .withMinorTickLineStyle(ColorUtil.Red, 0.5f)
                .build();

        final IAxis yLeftAxis = sciChartBuilder.newNumericAxis()
                .withGrowBy(0, 3d)
                .withAxisAlignment(AxisAlignment.Left)
                .withAutoRangeMode(AutoRange.Always)
                .withAxisId("SecondaryAxisId")
                // Brushes and styles for the Left YAxis, horizontal gridlines, horizontal tick marks, horizontal axis bands and left yaxis labels
                .withDrawMajorBands(false)
                .withDrawMajorGridLines(false)
                .withDrawMinorGridLines(false)
                .withDrawMajorTicks(true)
                .withDrawMinorTicks(true)
                .withDrawLabels(true)
                .withLabelProvider(new BillionsLabelProvider()) // See LabelProvider API documentation
                .withTickLabelStyle(sciChartBuilder.newFont().withTextSize(12).withTextColor(ColorUtil.Purple).build())
                .withMajorTickLineLength(3)
                .withMajorTickLineStyle(ColorUtil.Black, 1f)
                .withMinorTickLineLength(2)
                .withMinorTickLineStyle(ColorUtil.Black, 0.5f)
                .build();

        // Create and populate data series
        final DataManager dataManager = DataManager.getInstance();
        final PriceSeries priceData = dataManager.getPriceDataIndu(getActivity());

        final IXyDataSeries<Double, Double> mountainDataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Mountain Series").build();
        final IXyDataSeries<Double, Double> lineDataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Line Series").build();
        final IXyDataSeries<Double, Long> columnDataSeries = sciChartBuilder.newXyDataSeries(Double.class, Long.class).withSeriesName("Column Series").build();
        final IOhlcDataSeries<Double, Double> candlestickDataSeries = sciChartBuilder.newOhlcDataSeries(Double.class, Double.class).withSeriesName("Candlestick Series").build();

        mountainDataSeries.append(priceData.getIndexesAsDouble(), dataManager.offset(priceData.getLowData(), -1000));
        candlestickDataSeries.append(priceData.getIndexesAsDouble(), priceData.getOpenData(), priceData.getHighData(), priceData.getLowData(), priceData.getCloseData());
        lineDataSeries.append(priceData.getIndexesAsDouble(), dataManager.computeMovingAverage(priceData.getCloseData(), 50));
        columnDataSeries.append(priceData.getIndexesAsDouble(), priceData.getVolumeData());

        final FastMountainRenderableSeries mountainSeries = sciChartBuilder.newMountainSeries()
                .withDataSeries(mountainDataSeries)
                .withYAxisId("PrimaryAxisId")
                // mountain series area fill
                .withAreaFillColor(0xA000D0D0)
                // mountain series line (just on top of mountain). If set to nil, there will be no line
                .withStrokeStyle(0xFF00D0D0, 2f)
                // setting to true gives jagged mountains. set to false if you want regular mountain chart
                .withIsDigitalLine(true)
                .build();

        final FastLineRenderableSeries lineSeries = sciChartBuilder.newLineSeries()
                .withDataSeries(lineDataSeries)
                .withYAxisId("PrimaryAxisId")
                // line series color and thickness
                .withStrokeStyle(0xFF0000FF, 3f)
                // setting to true gives jagged line. set to false if you want regular line chart
                .withIsDigitalLine(false)
                // one of the options for point markers.
                // point marers at data points. set to nil if you don't need them
                .withPointMarker(sciChartBuilder.newPointMarker(new EllipsePointMarker()).withSize(7, 7).build())
                .build();

        final FastColumnRenderableSeries columnSeries = sciChartBuilder.newColumnSeries()
                .withDataSeries(columnDataSeries)
                .withYAxisId("SecondaryAxisId")
                // column series fill color
                .withFillColor(0xE0D030D0)
                // column series outline color and width. It is set to nil to disable outline
                .withStrokeStyle(Color.TRANSPARENT)
                .build();

        final FastCandlestickRenderableSeries candlestickSeries = sciChartBuilder.newCandlestickSeries()
                .withDataSeries(candlestickDataSeries)
                .withYAxisId("PrimaryAxisId")
                // candlestick series has separate color for data where close is higher that open value (up) and oposite when close is lower than open (down)
                // candlestick stroke color and thicknes for "up" data
                .withStrokeUp(0xFF00FF00)
                // candlestick fill color for "up" data
                .withFillUpColor(0x7000FF00)
                // candlestick stroke color and thicknes for "down" data
                .withStrokeDown(0xFFFF0000)
                // candlestick fill color for "down" data
                .withFillDownColor(0xFFFF0000)
                .build();

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yRightAxis, yLeftAxis);
            Collections.addAll(surface.getRenderableSeries(), mountainSeries, columnSeries, candlestickSeries, lineSeries);
            Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());

            sciChartBuilder.newAnimator(mountainSeries).withSweepTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
            sciChartBuilder.newAnimator(candlestickSeries).withScaleTransformation(11700d).withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
            sciChartBuilder.newAnimator(lineSeries).withSweepTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
            sciChartBuilder.newAnimator(columnSeries).withWaveTransformation(10500d).withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
        });
    }
}