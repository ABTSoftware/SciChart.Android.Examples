//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ShiftedAxesFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;

import com.scichart.charting.layoutManagers.DefaultLayoutManager;
import com.scichart.charting.layoutManagers.ILayoutManager;
import com.scichart.charting.layoutManagers.LeftAlignmentInnerAxisLayoutStrategy;
import com.scichart.charting.layoutManagers.TopAlignmentInnerAxisLayoutStrategy;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AxisAlignment;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.core.IServiceContainer;
import com.scichart.core.common.Size;
import com.scichart.drawing.common.PenStyle;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import butterknife.Bind;

public class ShiftedAxesFragment extends ExampleBaseFragment {
    @Bind(R.id.chart)
    SciChartSurface chart;

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart_fragment;
    }

    public boolean showDefaultModifiersInToolbar() {
        return false;
    }

    @Override
    protected void initExample() {
        final NumericAxis xAxis = sciChartBuilder.newNumericAxis().withAxisAlignment(AxisAlignment.Top).withIsCenterAxis(true).withGrowBy(0.1, 0.1).build();
        final NumericAxis yAxis = sciChartBuilder.newNumericAxis().withAxisAlignment(AxisAlignment.Left).withIsCenterAxis(true).withGrowBy(0.1, 0.1).build();

        PenStyle ticksPen = sciChartBuilder.newPen().withAntiAliasing(true).withThickness(2).build();
        xAxis.setMajorTickLineStyle(ticksPen);
        xAxis.setMinorTickLineStyle(ticksPen);
        yAxis.setMajorTickLineStyle(ticksPen);
        yAxis.setMinorTickLineStyle(ticksPen);

        final XyDataSeries<Double, Double> dataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withAcceptsUnsortedData().build();
        final DoubleSeries butterflyCurve = DataManager.getInstance().getButterflyCurve(20000);
        dataSeries.append(butterflyCurve.xValues, butterflyCurve.yValues);

        final FastLineRenderableSeries renderableSeries = sciChartBuilder.newLineSeries().withDataSeries(dataSeries).build();

        chart.setLayoutManager(new CenterLayoutManager(xAxis, yAxis));

        chart.getXAxes().add(xAxis);
        chart.getYAxes().add(yAxis);
        chart.getRenderableSeries().add(renderableSeries);

        chart.getChartModifiers().add(sciChartBuilder.newModifierGroupWithDefaultModifiers().build());
    }

    private static final class CenterLayoutManager implements ILayoutManager{
        private final DefaultLayoutManager defaultLayoutManager;
        private boolean isFirstlLayout;

        private CenterLayoutManager(IAxis xAxis, IAxis yAxis) {
            // need to override default inner layout strategies for bottom and right aligned axes
            // because xAxis has right axis alignment and yAxis has bottom axis alignment
            this.defaultLayoutManager = new DefaultLayoutManager.Builder()
                    .setLeftInnerAxesLayoutStrategy(new CenteredLeftAlignmentInnerAxisLayoutStrategy(xAxis))
                    .setTopInnerAxesLayoutStrategy(new CenteredTopAlignmentInnerAxisLayoutStrategy(yAxis))
                    .build();
        }

        @Override
        public void attachAxis(IAxis axis, boolean isXAxis) {
            defaultLayoutManager.attachAxis(axis, isXAxis);
        }

        @Override
        public void detachAxis(IAxis axis) {
            defaultLayoutManager.detachAxis(axis);
        }

        @Override
        public void onAxisPlacementChanged(IAxis axis, AxisAlignment oldAxisAlignment, boolean oldIsCenterAxis, AxisAlignment newAxisAlignment, boolean newIsCenterAxis) {
            defaultLayoutManager.onAxisPlacementChanged(axis, oldAxisAlignment, oldIsCenterAxis, newAxisAlignment, newIsCenterAxis);
        }

        @Override
        public void attachTo(IServiceContainer services) {
            defaultLayoutManager.attachTo(services);

            // need to perform 2 layout passes during first layout of chart
            isFirstlLayout = true;
        }

        @Override
        public void detach() {
            defaultLayoutManager.detach();
        }

        @Override
        public boolean isAttached() {
            return defaultLayoutManager.isAttached();
        }

        @Override
        public Size onLayoutChart(int width, int height) {
            // need to perform additional layout pass if it is a first layout pass
            // because we don't know correct size of axes during first layout pass
            if(isFirstlLayout){
                defaultLayoutManager.onLayoutChart(width, height);
                isFirstlLayout = false;
            }

            return defaultLayoutManager.onLayoutChart(width, height);
        }
    }

    private static class CenteredTopAlignmentInnerAxisLayoutStrategy extends TopAlignmentInnerAxisLayoutStrategy {
        private final IAxis yAxis;

        private CenteredTopAlignmentInnerAxisLayoutStrategy(IAxis yAxis) {
            this.yAxis = yAxis;
        }

        @Override
        public void layoutAxes(int left, int top, int right, int bottom) {
            // find the coordinate of 0 on the Y Axis in pixels
            // place the stack of the top-aligned X Axes at this coordinate
            final float topCoord = yAxis.getCurrentCoordinateCalculator().getCoordinate(0);
            layoutFromTopToBottom(left, (int)topCoord, right, axes);
        }
    }

    private static class CenteredLeftAlignmentInnerAxisLayoutStrategy extends LeftAlignmentInnerAxisLayoutStrategy {
        private final IAxis xAxis;

        private CenteredLeftAlignmentInnerAxisLayoutStrategy(IAxis xAxis) {
            this.xAxis = xAxis;
        }

        @Override
        public void layoutAxes(int left, int top, int right, int bottom) {
            // find the coordinate of 0 on the X Axis in pixels
            // place the stack of the left-aligned Y Axes at this coordinate
            final float leftCoord = xAxis.getCurrentCoordinateCalculator().getCoordinate(0);
            layoutFromLeftToRight((int) leftCoord, top, bottom, axes);
        }
    }
}
