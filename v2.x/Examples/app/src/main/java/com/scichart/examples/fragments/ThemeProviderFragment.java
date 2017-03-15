//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ThemeProviderFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;

import android.app.Dialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

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
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.R;
import com.scichart.examples.components.SpinnerStringAdapter;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.data.PriceSeries;
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.examples.utils.BillionsLabelProvider;
import com.scichart.examples.utils.ItemSelectedListenerBase;
import com.scichart.examples.utils.ThousandsLabelProvider;
import com.scichart.examples.utils.ViewSettingsUtil;
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget;
import com.scichart.examples.utils.widgetgeneration.Widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;

public class ThemeProviderFragment extends ExampleBaseFragment {

    private final static int BLACK_STEEL = 0;
    private final static int BRIGHT_SPARK = 1;
    private final static int CHROME = 2;
    private final static int ELECTRIC = 3;
    private final static int EXPRESSION_DARK = 4;
    private final static int EXPRESSION_LIGHT = 5;
    private final static int OSCILLOSCOPE = 6;
    private final static int SCI_CHART_V4_DARK = 7;
    private final static int BERRY_BLUE = 8;

    @Bind(R.id.chart)
    SciChartSurface surface;

    @Bind(R.id.themeSelector)
    Spinner themeSelector;

    private CursorModifier cursorModifier;
    private ModifierGroup zoomingModifiers;

    @Override
    protected void initExample() {
        themeSelector.setAdapter(new SpinnerStringAdapter(getActivity(), R.array.style_list));
        themeSelector.setSelection(7);
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

        final IRenderableSeries mountainRenderableSeries = sciChartBuilder.newMountainSeries().withDataSeries(mountainDataSeries).withYAxisId("PrimaryAxisId").build();
        final IRenderableSeries lineRenderableSeries = sciChartBuilder.newLineSeries().withDataSeries(lineDataSeries).withYAxisId("PrimaryAxisId").build();
        final IRenderableSeries columnRenderableSeries = sciChartBuilder.newColumnSeries().withDataSeries(columnDataSeries).withYAxisId("SecondaryAxisId").build();
        final IRenderableSeries candlestickRenderableSeries = sciChartBuilder.newCandlestickSeries().withDataSeries(candlestickDataSeries).withYAxisId("PrimaryAxisId").build();

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

        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                Collections.addAll(surface.getChartModifiers(), modifiers);
                Collections.addAll(surface.getXAxes(), xBottomAxis);
                Collections.addAll(surface.getYAxes(), yRightAxis, yLeftAxis);
                Collections.addAll(surface.getRenderableSeries(), mountainRenderableSeries, candlestickRenderableSeries, lineRenderableSeries, columnRenderableSeries);
            }
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
            default:
                themeId = ThemeManager.DEFAULT_THEME;
                break;
        }

        surface.setTheme(themeId);
    }

    @Override
    public List<Widget> getToolbarItems() {
        return new ArrayList<Widget>() {
            {
                add(new ImageViewWidget.Builder().setId(R.drawable.example_toolbar_settings).setListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openSettingsDialog();
                    }
                }).build());
            }
        };
    }

    private void openSettingsDialog() {
        final Dialog dialog = ViewSettingsUtil.createSettingsPopup(getActivity(), R.layout.example_theme_provider_popup_layout);

        ViewSettingsUtil.setUpRadioButton(dialog, R.id.cursor_modifier_radio_button, cursorModifier);
        ViewSettingsUtil.setUpRadioButton(dialog, R.id.zoom_modifier_radio_button, zoomingModifiers);

        dialog.show();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.example_theme_provider_chart_fragment;
    }
}