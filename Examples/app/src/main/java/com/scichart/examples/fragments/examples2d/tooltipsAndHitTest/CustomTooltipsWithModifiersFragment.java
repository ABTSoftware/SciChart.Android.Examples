//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CustomTooltipsWithModifiersFragment.java is part of SCICHART®, High Performance Scientific Charts
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
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
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
import com.scichart.examples.databinding.ExampleCustomTooltipWithModifiersFragmentBinding;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;

public class CustomTooltipsWithModifiersFragment extends ExampleBaseFragment<ExampleCustomTooltipWithModifiersFragmentBinding> {
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

    private RolloverModifier rolloverModifier;
    private CursorModifier cursorModifier;
    private TooltipModifier tooltipModifier;

    @NonNull
    @Override
    protected ExampleCustomTooltipWithModifiersFragmentBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ExampleCustomTooltipWithModifiersFragmentBinding.inflate(inflater);
    }

    @Override
    protected void initExample(ExampleCustomTooltipWithModifiersFragmentBinding binding) {
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

        final SciChartSurface surface = binding.surface;
        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), sciChartBuilder.newNumericAxis().build());
            Collections.addAll(surface.getYAxes(), sciChartBuilder.newNumericAxis().build());
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

            public FirstCustomXySeriesTooltip(Context context, XySeriesInfo<?> seriesInfo, String modifierName) {
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

            public SecondCustomXySeriesTooltip(Context context, XySeriesInfo<?> seriesInfo, String modifierName) {
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

                setSeriesColor(0xffe2460c);
            }
        }
    }
}