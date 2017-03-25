//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CustomizationCursorModifierTooltipsFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.customizationCursorModifierTooltips;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.modifiers.CursorModifier;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.NumericAxis;
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
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;

import butterknife.Bind;

public class CustomizationCursorModifierTooltipsFragment extends ExampleBaseFragment {
    private static final int POINTS_COUNT = 200;

    @Bind(R.id.chart)
    SciChartSurface surface;

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart_fragment;
    }

    @Override
    protected void initExample() {
        final NumericAxis xAxis = sciChartBuilder.newNumericAxis().build();
        final NumericAxis yAxis = sciChartBuilder.newNumericAxis().build();

        final RandomWalkGenerator randomWalkGenerator = new RandomWalkGenerator();

        final DoubleSeries data1 = randomWalkGenerator.getRandomWalkSeries(POINTS_COUNT);
        randomWalkGenerator.reset();
        final DoubleSeries data2 = randomWalkGenerator.getRandomWalkSeries(POINTS_COUNT);

        final XyDataSeries<Double, Double> ds1 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Series #1").build();
        final XyDataSeries<Double, Double> ds2 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Series #2").build();

        ds1.append(data1.xValues, data1.yValues);
        ds2.append(data2.xValues, data2.yValues);

        final FastLineRenderableSeries lineRs1 = sciChartBuilder.newLineSeries().withDataSeries(ds1).withSeriesInfoProvider(new CustomSeriesInfoProvider()).withStrokeStyle(0xff6495ed, 2).build();
        final FastLineRenderableSeries lineRs2 = sciChartBuilder.newLineSeries().withDataSeries(ds2).withSeriesInfoProvider(new CustomSeriesInfoProvider()).withStrokeStyle(0xffe2460c, 2).build();

        final CursorModifier cursorModifier = new CursorModifier(R.layout.example_custom_cursor_modifier_tooltip_container);

        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                Collections.addAll(surface.getXAxes(), xAxis);
                Collections.addAll(surface.getYAxes(), yAxis);
                Collections.addAll(surface.getRenderableSeries(), lineRs1, lineRs2);
                Collections.addAll(surface.getChartModifiers(), cursorModifier);

                final DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
                final float thickness = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, displayMetrics);
                new SolidPenStyle(0xAAFFA500, false, thickness, null).initPaint(cursorModifier.getCrosshairPaint());
            }
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
            public CustomXySeriesTooltip(Context context, XySeriesInfo seriesInfo) {
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

                setTooltipBackgroundColor(0xff6495ed);
                setTooltipStroke(0xff4d81dd);
                setTooltipTextColor(ColorUtil.White);
            }
        }
    }
}