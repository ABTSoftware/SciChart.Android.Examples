//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ThemeProviderFragment.java is part of SCICHART®, High Performance Scientific Charts
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

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.IOhlcDataSeries;
import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.modifiers.CursorModifier;
import com.scichart.charting.modifiers.ModifierGroup;
import com.scichart.charting.modifiers.PinchZoomModifier;
import com.scichart.charting.modifiers.ZoomPanModifier;
import com.scichart.charting.themes.ThemeManager;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.AxisAlignment;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.FastCandlestickRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastColumnRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastMountainRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.R;
import com.scichart.examples.components.SpinnerStringAdapter;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.PriceSeries;
import com.scichart.examples.databinding.ExampleThemeProviderChartFragmentBinding;
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.examples.utils.BillionsLabelProvider;
import com.scichart.examples.utils.ItemSelectedListenerBase;
import com.scichart.examples.utils.ThousandsLabelProvider;
import com.scichart.examples.utils.ViewSettingsUtil;
import com.scichart.examples.utils.interpolator.ElasticOutInterpolator;
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget;
import com.scichart.examples.utils.widgetgeneration.Widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ThemeProviderFragment extends ExampleBaseFragment<ExampleThemeProviderChartFragmentBinding> {

    private final static int BLACK_STEEL = 0;
    private final static int BRIGHT_SPARK = 1;
    private final static int CHROME = 2;
    private final static int ELECTRIC = 3;
    private final static int EXPRESSION_DARK = 4;
    private final static int EXPRESSION_LIGHT = 5;
    private final static int OSCILLOSCOPE = 6;
    private final static int SCI_CHART_V4_DARK = 7;
    private final static int BERRY_BLUE = 8;
    private final static int SCI_CHART_NAVY_BLUE = 9;

    private CursorModifier cursorModifier;
    private ModifierGroup zoomingModifiers;

    @NonNull
    @Override
    protected ExampleThemeProviderChartFragmentBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ExampleThemeProviderChartFragmentBinding.inflate(inflater);
    }

    @Override
    protected void initExample(ExampleThemeProviderChartFragmentBinding binding) {
        final Spinner themeSelector = binding.themeSelector;
        themeSelector.setAdapter(new SpinnerStringAdapter(getActivity(), R.array.style_list));
        themeSelector.setSelection(9);
        themeSelector.setOnItemSelectedListener(new ItemSelectedListenerBase() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setTheme(position);
            }
        });

        final IAxis xBottomAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1d, 0.1d).withVisibleRange(150, 180).build();

        final IAxis yRightAxis = sciChartBuilder.newNumericAxis()
                .withGrowBy(0.1d, 0.1d)
                .withAxisAlignment(AxisAlignment.Right)
                .withAutoRangeMode(AutoRange.Always)
                .withAxisId("PrimaryAxisId")
                .withDrawMajorTicks(false)
                .withDrawMinorTicks(false)
                .withLabelProvider(new ThousandsLabelProvider())
                .build();

        final IAxis yLeftAxis = sciChartBuilder.newNumericAxis()
                .withGrowBy(0, 3d)
                .withAxisAlignment(AxisAlignment.Left)
                .withAutoRangeMode(AutoRange.Always)
                .withAxisId("SecondaryAxisId")
                .withDrawMajorTicks(false)
                .withDrawMinorTicks(false)
                .withLabelProvider(new BillionsLabelProvider())
                .build();

        final DataManager dataManager = DataManager.getInstance();
        final PriceSeries priceBars = dataManager.getPriceDataIndu(getActivity());

        final IXyDataSeries<Double, Double> mountainDataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Mountain Series").build();
        final IXyDataSeries<Double, Double> lineDataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Line Series").build();
        final IXyDataSeries<Double, Long> columnDataSeries = sciChartBuilder.newXyDataSeries(Double.class, Long.class).withSeriesName("Column Series").build();
        final IOhlcDataSeries<Double, Double> candlestickDataSeries = sciChartBuilder.newOhlcDataSeries(Double.class, Double.class).withSeriesName("Candlestick Series").build();

        mountainDataSeries.append(priceBars.getIndexesAsDouble(), dataManager.offset(priceBars.getLowData(), -1000));
        candlestickDataSeries.append(priceBars.getIndexesAsDouble(), priceBars.getOpenData(), priceBars.getHighData(), priceBars.getLowData(), priceBars.getCloseData());
        lineDataSeries.append(priceBars.getIndexesAsDouble(), dataManager.computeMovingAverage(priceBars.getCloseData(), 50));
        columnDataSeries.append(priceBars.getIndexesAsDouble(), priceBars.getVolumeData());

        final FastMountainRenderableSeries mountainSeries = sciChartBuilder.newMountainSeries().withDataSeries(mountainDataSeries).withYAxisId("PrimaryAxisId").build();
        final FastLineRenderableSeries lineSeries = sciChartBuilder.newLineSeries().withDataSeries(lineDataSeries).withYAxisId("PrimaryAxisId").build();
        final FastColumnRenderableSeries columnSeries = sciChartBuilder.newColumnSeries().withDataSeries(columnDataSeries).withYAxisId("SecondaryAxisId").build();
        final FastCandlestickRenderableSeries candlestickSeries = sciChartBuilder.newCandlestickSeries().withDataSeries(candlestickDataSeries).withYAxisId("PrimaryAxisId").build();

        PinchZoomModifier pinchZoomModifier = new PinchZoomModifier();
        pinchZoomModifier.setReceiveHandledEvents(true);

        ZoomPanModifier zoomPanModifier = new ZoomPanModifier();
        zoomPanModifier.setReceiveHandledEvents(true);

        zoomingModifiers = new ModifierGroup();
        Collections.addAll(zoomingModifiers.getChildModifiers(), pinchZoomModifier, zoomPanModifier);
        zoomingModifiers.setIsEnabled(false);

        cursorModifier = new CursorModifier();
        final ModifierGroup modifiers = sciChartBuilder.newModifierGroup()
                .withLegendModifier().withShowCheckBoxes(false).build()
                .withModifier(cursorModifier)
                .withModifier(zoomingModifiers)
                .withZoomExtentsModifier().build()
                .build();

        final SciChartSurface surface = binding.surface;
        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getChartModifiers(), modifiers);
            Collections.addAll(surface.getXAxes(), xBottomAxis);
            Collections.addAll(surface.getYAxes(), yRightAxis, yLeftAxis);
            Collections.addAll(surface.getRenderableSeries(), mountainSeries, candlestickSeries, lineSeries, columnSeries);

            sciChartBuilder.newAnimator(mountainSeries).withScaleTransformation(10500d).withInterpolator(new ElasticOutInterpolator()).withDuration(3000).withStartDelay(350).start();
            sciChartBuilder.newAnimator(candlestickSeries).withScaleTransformation(11700d).withInterpolator(new ElasticOutInterpolator()).withDuration(3000).withStartDelay(350).start();
            sciChartBuilder.newAnimator(lineSeries).withScaleTransformation(12250d).withInterpolator(new ElasticOutInterpolator()).withDuration(3000).withStartDelay(350).start();
            sciChartBuilder.newAnimator(columnSeries).withScaleTransformation(10500d).withInterpolator(new ElasticOutInterpolator()).withDuration(3000).withStartDelay(350).start();
        });
    }

    private void setTheme(int position) {
        int themeId;
        switch (position) {
            case BLACK_STEEL:
                themeId = R.style.SciChart_BlackSteel;
                break;
            case BRIGHT_SPARK:
                themeId = R.style.SciChart_Bright_Spark;
                break;
            case CHROME:
                themeId = R.style.SciChart_ChromeStyle;
                break;
            case ELECTRIC:
                themeId = R.style.SciChart_ElectricStyle;
                break;
            case EXPRESSION_DARK:
                themeId = R.style.SciChart_ExpressionDarkStyle;
                break;
            case EXPRESSION_LIGHT:
                themeId = R.style.SciChart_ExpressionLightStyle;
                break;
            case OSCILLOSCOPE:
                themeId = R.style.SciChart_OscilloscopeStyle;
                break;
            case SCI_CHART_V4_DARK:
                themeId = R.style.SciChart_SciChartv4DarkStyle;
                break;
            case BERRY_BLUE:
                themeId = R.style.SciChart_BerryBlue;
                break;
            case SCI_CHART_NAVY_BLUE:
                themeId = R.style.SciChart_NavyBlue;
                break;
            default:
                themeId = ThemeManager.DEFAULT_THEME;
                break;
        }

        binding.surface.setTheme(themeId);
    }

    @NonNull
    @Override
    public List<Widget> getToolbarItems() {
        return new ArrayList<Widget>() {{
            add(new ImageViewWidget.Builder().setId(R.drawable.example_toolbar_settings).setListener(v -> openSettingsDialog()).build());
        }};
    }

    private void openSettingsDialog() {
        final Dialog dialog = ViewSettingsUtil.createSettingsPopup(getActivity(), R.layout.example_theme_provider_popup_layout);

        ViewSettingsUtil.setUpRadioButton(dialog, R.id.cursor_modifier_radio_button, cursorModifier);
        ViewSettingsUtil.setUpRadioButton(dialog, R.id.zoom_modifier_radio_button, zoomingModifiers);

        dialog.show();
    }
}