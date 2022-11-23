//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CustomizationRolloverModifierTooltipsFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.tooltipsAndHitTest;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;

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
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;

import java.util.Collections;

public class CustomizationRolloverModifierTooltipsFragment extends ExampleSingleChartBaseFragment {
    private static final int POINTS_COUNT = 200;
    private static final String MODIFIER_NAME = "RolloverModifier";

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        final RandomWalkGenerator randomWalkGenerator = new RandomWalkGenerator();
        final DoubleSeries data1 = randomWalkGenerator.getRandomWalkSeries(POINTS_COUNT);
        randomWalkGenerator.reset();
        final DoubleSeries data2 = randomWalkGenerator.getRandomWalkSeries(POINTS_COUNT);

        final XyDataSeries<Double, Double> ds1 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Series #1").build();
        final XyDataSeries<Double, Double> ds2 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Series #2").build();

        ds1.append(data1.xValues, data1.yValues);
        ds2.append(data2.xValues, data2.yValues);

        final FastLineRenderableSeries lineRs1 = sciChartBuilder.newLineSeries().withDataSeries(ds1).withSeriesInfoProvider(new FirstCustomSeriesInfoProvider()).withStrokeStyle(0xffe97064, 2, true).build();
        final FastLineRenderableSeries lineRs2 = sciChartBuilder.newLineSeries().withDataSeries(ds2).withSeriesInfoProvider(new SecondCustomSeriesInfoProvider()).withStrokeStyle(0xff274b92, 2, true).build();

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), sciChartBuilder.newNumericAxis().withAxisInfoProvider(new CustomAxisInfoProvider()).build());
            Collections.addAll(surface.getYAxes(), sciChartBuilder.newNumericAxis().build());
            Collections.addAll(surface.getRenderableSeries(), lineRs1, lineRs2);
            Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroup().withRolloverModifier().build().build());

            sciChartBuilder.newAnimator(lineRs1).withSweepTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(3000).withStartDelay(350).start();
            sciChartBuilder.newAnimator(lineRs2).withSweepTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(3000).withStartDelay(350).start();
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
            public FirstCustomXySeriesTooltip(Context context, XySeriesInfo<?> seriesInfo) {
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

                setTooltipBackgroundColor(0xffe97064);
                setTooltipStroke(0xfff4840b);
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
            public SecondCustomXySeriesTooltip(Context context, XySeriesInfo<?> seriesInfo) {
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

                setTooltipBackgroundColor(0xff274b92);
                setTooltipStroke(0xff112cce);
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