//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CustomTooltipsWithModifiersFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.modifiers.CursorModifier;
import com.scichart.charting.modifiers.IChartModifier;
import com.scichart.charting.modifiers.ModifierGroup;
import com.scichart.charting.modifiers.RolloverModifier;
import com.scichart.charting.modifiers.TooltipModifier;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.NumericAxis;
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

public class CustomTooltipsWithModifiersFragment extends ExampleBaseFragment {
    private static final int POINTS_COUNT = 200;
    private static final String ROLLOVER_MODIFIER_NAME = "RolloverModifier";
    private static final String CURSOR_MODIFIER_NAME = "CursorModifier";
    private static final String TOOLTIP_MODIFIER_NAME = "TooltipModifier";

    private static class RadioButtonCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
        private final IChartModifier chartModifier;

        public RadioButtonCheckedChangeListener(IChartModifier chartModifier) {
            this.chartModifier = chartModifier;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            this.chartModifier.setIsEnabled(isChecked);
        }
    }

    @BindView(R.id.chart)
    SciChartSurface surface;

    private RolloverModifier rolloverModifier;
    private CursorModifier cursorModifier;
    private TooltipModifier tooltipModifier;

    @Override
    protected int getLayoutId() {
        return R.layout.example_custom_tooltip_with_modifiers_fragment;
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

        final FastLineRenderableSeries lineRs1 = sciChartBuilder.newLineSeries().withDataSeries(ds1).withSeriesInfoProvider(new FirstCustomSeriesInfoProvider()).withStrokeStyle(0xff6495ed, 2).build();
        final FastLineRenderableSeries lineRs2 = sciChartBuilder.newLineSeries().withDataSeries(ds2).withSeriesInfoProvider(new SecondCustomSeriesInfoProvider()).withStrokeStyle(0xffe2460c, 2).build();

        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                Collections.addAll(surface.getXAxes(), xAxis);
                Collections.addAll(surface.getYAxes(), yAxis);
                Collections.addAll(surface.getRenderableSeries(), lineRs1, lineRs2);
                Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroup()
                        .withRolloverModifier().build()
                        .withCursorModifier().withIsEnabled(false).build()
                        .withTooltipModifier().withIsEnabled(false).build()
                        .build());

                ModifierGroup modifierGroup = (ModifierGroup) surface.getChartModifiers().get(0);

                rolloverModifier = (RolloverModifier) modifierGroup.getChildModifiers().get(0);
                cursorModifier = (CursorModifier) modifierGroup.getChildModifiers().get(1);
                tooltipModifier = (TooltipModifier) modifierGroup.getChildModifiers().get(2);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initializeRadioButtons();
    }

    private void initializeRadioButtons() {
        ((RadioButton) getActivity().findViewById(R.id.rollover)).setOnCheckedChangeListener(new RadioButtonCheckedChangeListener(rolloverModifier));
        ((RadioButton) getActivity().findViewById(R.id.cursor)).setOnCheckedChangeListener(new RadioButtonCheckedChangeListener(cursorModifier));
        ((RadioButton) getActivity().findViewById(R.id.tooltip)).setOnCheckedChangeListener(new RadioButtonCheckedChangeListener(tooltipModifier));
    }

    private static class FirstCustomSeriesInfoProvider extends DefaultXySeriesInfoProvider {
        @Override
        protected ISeriesTooltip getSeriesTooltipInternal(Context context, XySeriesInfo<?> seriesInfo, Class<?> modifierType) {
            if (modifierType == TooltipModifier.class) {
                return new FirstCustomXySeriesTooltip(context, seriesInfo, TOOLTIP_MODIFIER_NAME);
            } else if (modifierType == RolloverModifier.class) {
                return new FirstCustomXySeriesTooltip(context, seriesInfo, ROLLOVER_MODIFIER_NAME);
            } else if (modifierType == CursorModifier.class) {
                return new FirstCustomXySeriesTooltip(context, seriesInfo, CURSOR_MODIFIER_NAME);
            } else {
                return super.getSeriesTooltipInternal(context, seriesInfo, modifierType);
            }
        }

        private static class FirstCustomXySeriesTooltip extends XySeriesTooltip {
            private final String modifierName;

            public FirstCustomXySeriesTooltip(Context context, XySeriesInfo seriesInfo, String modifierName) {
                super(context, seriesInfo);
                this.modifierName = modifierName;
            }

            @Override
            protected void internalUpdate(XySeriesInfo seriesInfo) {
                final SpannableStringBuilder sb = new SpannableStringBuilder();

                sb.append("X: ").append(seriesInfo.getFormattedXValue()).append(StringUtil.NEW_LINE);
                sb.append("Y: ").append(seriesInfo.getFormattedYValue()).append(StringUtil.NEW_LINE);

                if (seriesInfo.seriesName != null) {
                    final int start = sb.length();

                    sb.append(seriesInfo.seriesName);
                    sb.setSpan(new ForegroundColorSpan(ColorUtil.White), start, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    sb.append(StringUtil.NEW_LINE);
                }
                sb.append(modifierName);
                setText(sb);

                // stroke 0xff4d81dd
                setSeriesColor(0xff6495ed);
            }
        }
    }

    private static class SecondCustomSeriesInfoProvider extends DefaultXySeriesInfoProvider {
        @Override
        protected ISeriesTooltip getSeriesTooltipInternal(Context context, XySeriesInfo<?> seriesInfo, Class<?> modifierType) {
            if (modifierType == TooltipModifier.class) {
                return new SecondCustomXySeriesTooltip(context, seriesInfo, TOOLTIP_MODIFIER_NAME);
            } else if (modifierType == RolloverModifier.class) {
                return new SecondCustomXySeriesTooltip(context, seriesInfo, ROLLOVER_MODIFIER_NAME);
            } else if (modifierType == CursorModifier.class) {
                return new SecondCustomXySeriesTooltip(context, seriesInfo, CURSOR_MODIFIER_NAME);
            } else {
                return super.getSeriesTooltipInternal(context, seriesInfo, modifierType);
            }
        }

        private static class SecondCustomXySeriesTooltip extends XySeriesTooltip {
            private final String modifierName;

            public SecondCustomXySeriesTooltip(Context context, XySeriesInfo seriesInfo, String modifierName) {
                super(context, seriesInfo);
                this.modifierName = modifierName;
            }

            @Override
            protected void internalUpdate(XySeriesInfo seriesInfo) {
                final SpannableStringBuilder sb = new SpannableStringBuilder();
                sb.append(modifierName).append(StringUtil.NEW_LINE);

                if (seriesInfo.seriesName != null) {
                    sb.append(seriesInfo.seriesName);
                    sb.setSpan(new ForegroundColorSpan(ColorUtil.Black), 0, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    sb.append(StringUtil.NEW_LINE);
                }

                sb.append("X: ").append(seriesInfo.getFormattedXValue());
                sb.append(" Y: ").append(seriesInfo.getFormattedYValue());

                setText(sb);

                // stroke 0xffff4500
                setSeriesColor(0xffe2460c);
            }
        }
    }
}