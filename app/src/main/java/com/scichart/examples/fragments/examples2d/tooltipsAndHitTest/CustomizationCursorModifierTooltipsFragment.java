//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CustomizationCursorModifierTooltipsFragment.java is part of SCICHART®, High Performance Scientific Charts
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
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.modifiers.CursorModifier;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.hitTest.DefaultXySeriesInfoProvider;
import com.scichart.charting.visuals.renderableSeries.hitTest.XySeriesInfo;
import com.scichart.charting.visuals.renderableSeries.tooltips.ISeriesTooltip;
import com.scichart.charting.visuals.renderableSeries.tooltips.XySeriesTooltip;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.drawing.common.SolidPenStyle;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.data.RandomWalkGenerator;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;
import com.scichart.examples.utils.Constant;
import com.scichart.examples.utils.interpolator.DefaultInterpolator;

import java.util.Collections;

public class CustomizationCursorModifierTooltipsFragment extends ExampleSingleChartBaseFragment {
    private static final int POINTS_COUNT = 200;

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        final IAxis xAxis = sciChartBuilder.newNumericAxis().build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().build();

        final RandomWalkGenerator randomWalkGenerator = new RandomWalkGenerator();

        final DoubleSeries data1 = randomWalkGenerator.getRandomWalkSeries(POINTS_COUNT);
        randomWalkGenerator.reset();
        final DoubleSeries data2 = randomWalkGenerator.getRandomWalkSeries(POINTS_COUNT);

        final XyDataSeries<Double, Double> ds1 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Series #1").build();
        final XyDataSeries<Double, Double> ds2 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Series #2").build();

        ds1.append(data1.xValues, data1.yValues);
        ds2.append(data2.xValues, data2.yValues);

        final FastLineRenderableSeries lineRs1 = sciChartBuilder.newLineSeries().withDataSeries(ds1).withSeriesInfoProvider(new CustomSeriesInfoProvider()).withStrokeStyle(0xffae418d, 2, true).build();
        final FastLineRenderableSeries lineRs2 = sciChartBuilder.newLineSeries().withDataSeries(ds2).withSeriesInfoProvider(new CustomSeriesInfoProvider()).withStrokeStyle(0xff68bcae, 2, true).build();

        final CursorModifier cursorModifier = new CursorModifier(R.layout.example_custom_cursor_modifier_tooltip_container);

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getRenderableSeries(), lineRs1, lineRs2);
            Collections.addAll(surface.getChartModifiers(), cursorModifier);

            final DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
            final float thickness = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, displayMetrics);
            new SolidPenStyle(0xAA47bde6, false, thickness, null).initPaint(cursorModifier.getCrosshairPaint());

            sciChartBuilder.newAnimator(lineRs1).withSweepTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
            sciChartBuilder.newAnimator(lineRs2).withSweepTransformation().withInterpolator(DefaultInterpolator.getInterpolator()).withDuration(Constant.ANIMATION_DURATION).withStartDelay(Constant.ANIMATION_START_DELAY).start();
        });
    }

    private static class CustomSeriesInfoProvider extends DefaultXySeriesInfoProvider {
        @Override
        protected ISeriesTooltip getSeriesTooltipInternal(Context context, XySeriesInfo<?> seriesInfo, Class<?> modifierType) {
            if (modifierType == CursorModifier.class) {
                return new CustomXySeriesTooltip(context, seriesInfo);
            } else {
                return super.getSeriesTooltipInternal(context, seriesInfo, modifierType);
            }
        }

        private static class CustomXySeriesTooltip extends XySeriesTooltip {
            public CustomXySeriesTooltip(Context context, XySeriesInfo<?> seriesInfo) {
                super(context, seriesInfo);

                final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
                final int padding = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, displayMetrics));
                this.setPadding(padding, padding, padding, padding);
            }

            @Override
            protected void internalUpdate(XySeriesInfo seriesInfo) {
                final SpannableStringBuilder sb = new SpannableStringBuilder();
                if (seriesInfo.seriesName != null) {
                    sb.append(seriesInfo.seriesName).append(" - ");
                }

                sb.append("X: ").append(seriesInfo.getFormattedXValue());
                sb.append(" Y: ").append(seriesInfo.getFormattedYValue());
                setText(sb);

                setTooltipBackgroundColor(0xff4781ed);
                setTooltipStroke(0xff4781ed);
                setTooltipTextColor(ColorUtil.White);
            }
        }
    }
}