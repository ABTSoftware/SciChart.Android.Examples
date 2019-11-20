//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CustomizationRolloverModifierTooltipsFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.view.animation.DecelerateInterpolator;

import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.modifiers.RolloverModifier;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AxisInfo;
import com.scichart.charting.visuals.axes.AxisTooltip;
import com.scichart.charting.visuals.axes.DefaultAxisInfoProvider;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.axes.IAxisTooltip;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.hitTest.DefaultXySeriesInfoProvider;
import com.scichart.charting.visuals.renderableSeries.hitTest.XySeriesInfo;
import com.scichart.charting.visuals.renderableSeries.tooltips.ISeriesTooltip;
import com.scichart.charting.visuals.renderableSeries.tooltips.XySeriesTooltip;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.utility.StringUtil;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.data.RandomWalkGenerator;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;

import butterknife.BindView;

public class CustomizationRolloverModifierTooltipsFragment extends ExampleBaseFragment {
    private static final int POINTS_COUNT = 200;
    private static final String MODIFIER_NAME = "RolloverModifier";

    @BindView(R.id.chart)
    SciChartSurface surface;

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart_fragment;
    }

    @Override
    protected void initExample() {
        final IAxis xAxis = sciChartBuilder.newNumericAxis().withAxisInfoProvider(new CustomAxisInfoProvider()).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().build();

        final RandomWalkGenerator randomWalkGenerator = new RandomWalkGenerator();

        final DoubleSeries data1 = randomWalkGenerator.getRandomWalkSeries(POINTS_COUNT);
        randomWalkGenerator.reset();
        final DoubleSeries data2 = randomWalkGenerator.getRandomWalkSeries(POINTS_COUNT);

        final XyDataSeries<Double, Double> ds1 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Series #1").build();
        final XyDataSeries<Double, Double> ds2 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Series #2").build();

        ds1.append(data1.xValues, data1.yValues);
        ds2.append(data2.xValues, data2.yValues);

        final FastLineRenderableSeries lineRs1 = sciChartBuilder.newLineSeries().withDataSeries(ds1).withSeriesInfoProvider(new FirstCustomSeriesInfoProvider()).withStrokeStyle(0xff6495ed, 2, true).build();
        final FastLineRenderableSeries lineRs2 = sciChartBuilder.newLineSeries().withDataSeries(ds2).withSeriesInfoProvider(new SecondCustomSeriesInfoProvider()).withStrokeStyle(0xffe2460c, 2, true).build();

        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                Collections.addAll(surface.getXAxes(), xAxis);
                Collections.addAll(surface.getYAxes(), yAxis);
                Collections.addAll(surface.getRenderableSeries(), lineRs1, lineRs2);
                Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroup().withRolloverModifier().build().build());

                sciChartBuilder.newAnimator(lineRs1).withSweepTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(3000).withStartDelay(350).start();
                sciChartBuilder.newAnimator(lineRs2).withSweepTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(3000).withStartDelay(350).start();
            }
        });
    }

    private static class FirstCustomSeriesInfoProvider extends DefaultXySeriesInfoProvider {
        @Override
        protected ISeriesTooltip getSeriesTooltipInternal(Context context, XySeriesInfo<?> seriesInfo, Class<?> modifierType) {
            if (modifierType == RolloverModifier.class) {
                return new FirstCustomXySeriesTooltip(context, seriesInfo);
            } else {
                return super.getSeriesTooltipInternal(context, seriesInfo, modifierType);
            }
        }

        private static class FirstCustomXySeriesTooltip extends XySeriesTooltip {
            public FirstCustomXySeriesTooltip(Context context, XySeriesInfo seriesInfo) {
                super(context, seriesInfo);
            }

            @Override
            protected void internalUpdate(XySeriesInfo seriesInfo) {
                final SpannableStringBuilder sb = new SpannableStringBuilder();

                sb.append("X: ").append(seriesInfo.getFormattedXValue()).append(StringUtil.NEW_LINE);
                sb.append("Y: ").append(seriesInfo.getFormattedYValue()).append(StringUtil.NEW_LINE);

                if (seriesInfo.seriesName != null) {
                    sb.append(seriesInfo.seriesName).append(StringUtil.NEW_LINE);
                }
                sb.append(MODIFIER_NAME);
                setText(sb);

                setTooltipBackgroundColor(0xff6495ed);
                setTooltipStroke(0xff4d81dd);
                setTooltipTextColor(ColorUtil.White);
            }
        }
    }

    private static class SecondCustomSeriesInfoProvider extends DefaultXySeriesInfoProvider {
        @Override
        protected ISeriesTooltip getSeriesTooltipInternal(Context context, XySeriesInfo<?> seriesInfo, Class<?> modifierType) {
            if (modifierType == RolloverModifier.class) {
                return new SecondCustomXySeriesTooltip(context, seriesInfo);
            } else {
                return super.getSeriesTooltipInternal(context, seriesInfo, modifierType);
            }
        }

        private static class SecondCustomXySeriesTooltip extends XySeriesTooltip {
            public SecondCustomXySeriesTooltip(Context context, XySeriesInfo seriesInfo) {
                super(context, seriesInfo);
            }

            @Override
            protected void internalUpdate(XySeriesInfo seriesInfo) {
                final SpannableStringBuilder sb = new SpannableStringBuilder();
                sb.append(MODIFIER_NAME).append(StringUtil.NEW_LINE);

                if (seriesInfo.seriesName != null) {
                    sb.append(seriesInfo.seriesName).append(StringUtil.NEW_LINE);
                }
                sb.append("X: ").append(seriesInfo.getFormattedXValue());
                sb.append(" Y: ").append(seriesInfo.getFormattedYValue());

                setText(sb);

                setTooltipBackgroundColor(0xffe2460c);
                setTooltipStroke(0xffff4500);
                setTooltipTextColor(ColorUtil.White);
            }
        }
    }

    private static class CustomAxisInfoProvider extends DefaultAxisInfoProvider {
        @Override
        protected IAxisTooltip getAxisTooltipInternal(Context context, AxisInfo axisInfo, Class<?> modifierType) {
            if (modifierType == RolloverModifier.class) {
                return new CustomAxisTooltip(context, axisInfo);
            } else {
                return super.getAxisTooltipInternal(context, axisInfo, modifierType);
            }
        }

        private static class CustomAxisTooltip extends AxisTooltip {
            public CustomAxisTooltip(Context context, AxisInfo axisInfo) {
                super(context, axisInfo);
                setTooltipBackground(R.drawable.example_custom_axis_tooltip_background);
            }

            @Override
            protected boolean updateInternal(AxisInfo axisInfo) {
                final SpannableStringBuilder sb = new SpannableStringBuilder();
                sb.append("Axis ID: ").append(axisInfo.axisId).append(StringUtil.NEW_LINE);
                sb.append("Value: ").append(axisInfo.axisFormattedDataValue);

                setText(sb);

                return true;
            }
        }
    }
}