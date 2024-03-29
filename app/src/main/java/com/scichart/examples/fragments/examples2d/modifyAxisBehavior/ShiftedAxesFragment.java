//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ShiftedAxesFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.modifyAxisBehavior;

import androidx.annotation.NonNull;

import com.scichart.charting.layoutManagers.DefaultLayoutManager;
import com.scichart.charting.layoutManagers.ILayoutManager;
import com.scichart.charting.layoutManagers.LeftAlignmentInnerAxisLayoutStrategy;
import com.scichart.charting.layoutManagers.TopAlignmentInnerAxisLayoutStrategy;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AxisAlignment;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.core.IServiceContainer;
import com.scichart.core.common.Size;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.R;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;

import java.util.Collections;

public class ShiftedAxesFragment extends ExampleSingleChartBaseFragment {

    public boolean showDefaultModifiersInToolbar() {
        return false;
    }

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        surface.setTheme(R.style.SciChart_NavyBlue);

        final IAxis xAxis = sciChartBuilder.newNumericAxis().withAxisAlignment(AxisAlignment.Top).withMajorTickLineStyle(0xFFFFFFFF, 2f, true).withTextFormatting("0.00").withDrawMinorTicks(false).withIsCenterAxis(true).withGrowBy(0.1, 0.1).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withAxisAlignment(AxisAlignment.Left).withMajorTickLineStyle(0xFFFFFFFF, 2f, true).withTextFormatting("0.00").withDrawMinorTicks(false).withIsCenterAxis(true).withGrowBy(0.1, 0.1).build();

        final DoubleSeries butterflyCurve = DataManager.getInstance().getButterflyCurve(20000);
        final XyDataSeries<Double, Double> dataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withAcceptsUnsortedData().build();
        dataSeries.append(butterflyCurve.xValues, butterflyCurve.yValues);

        final FastLineRenderableSeries rSeries = sciChartBuilder.newLineSeries().withDataSeries(dataSeries).build();

        UpdateSuspender.using(surface, () -> {
            surface.setLayoutManager(new CenterLayoutManager(xAxis, yAxis));

            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getRenderableSeries(), rSeries);
            Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());

            sciChartBuilder.newAnimator(rSeries).withSweepTransformation().withDuration(20000).withStartDelay(350).start();
        });
    }

    private static final class CenterLayoutManager implements ILayoutManager {
        private final DefaultLayoutManager defaultLayoutManager;
        private boolean isFirstLayout;

        CenterLayoutManager(IAxis xAxis, IAxis yAxis) {
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
            isFirstLayout = true;
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
            if (isFirstLayout) {
                defaultLayoutManager.onLayoutChart(width, height);
                isFirstLayout = false;
            }

            return defaultLayoutManager.onLayoutChart(width, height);
        }
    }

    private static class CenteredTopAlignmentInnerAxisLayoutStrategy extends TopAlignmentInnerAxisLayoutStrategy {
        private final IAxis yAxis;

        CenteredTopAlignmentInnerAxisLayoutStrategy(IAxis yAxis) {
            this.yAxis = yAxis;
        }

        @Override
        public void layoutAxes(int left, int top, int right, int bottom) {
            // find the coordinate of 0 on the Y Axis in pixels
            // place the stack of the top-aligned X Axes at this coordinate
            final float topCoord = yAxis.getCurrentCoordinateCalculator().getCoordinate(0);
            layoutFromTopToBottom(left, (int) topCoord, right, axes);
        }
    }

    private static class CenteredLeftAlignmentInnerAxisLayoutStrategy extends LeftAlignmentInnerAxisLayoutStrategy {
        private final IAxis xAxis;

        CenteredLeftAlignmentInnerAxisLayoutStrategy(IAxis xAxis) {
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