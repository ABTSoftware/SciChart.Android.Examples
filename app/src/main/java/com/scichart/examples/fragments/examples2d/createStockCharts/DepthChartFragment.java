//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2022. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CreateLargeTradesStockChartFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.createStockCharts;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.OhlcDataSeries;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.modifiers.ChartModifierBase;
import com.scichart.charting.modifiers.PinchZoomModifier;
import com.scichart.charting.modifiers.ZoomExtentsModifier;
import com.scichart.charting.modifiers.ZoomPanModifier;
import com.scichart.charting.visuals.annotations.AnnotationCoordinateMode;
import com.scichart.charting.visuals.annotations.AnnotationLabel;
import com.scichart.charting.visuals.annotations.BoxAnnotation;
import com.scichart.charting.visuals.annotations.HorizontalAnchorPoint;
import com.scichart.charting.visuals.annotations.HorizontalLineAnnotation;
import com.scichart.charting.visuals.annotations.LabelPlacement;
import com.scichart.charting.visuals.annotations.LineAnnotation;
import com.scichart.charting.visuals.annotations.TextAnnotation;
import com.scichart.charting.visuals.annotations.VerticalLineAnnotation;
import com.scichart.charting.visuals.axes.AxisAlignment;
import com.scichart.charting.visuals.axes.DateAxis;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.renderableSeries.FastCandlestickRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.hitTest.HitTestInfo;
import com.scichart.core.IServiceContainer;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.utility.touch.ModifierTouchEventArgs;
import com.scichart.data.model.ISciList;
import com.scichart.drawing.common.FontStyle;
import com.scichart.drawing.common.SolidBrushStyle;
import com.scichart.drawing.common.SolidPenStyle;
import com.scichart.examples.R;
import com.scichart.examples.databinding.ExampleDepthChartsFragmentBinding;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DepthChartFragment extends ExampleBaseFragment<ExampleDepthChartsFragmentBinding> {


    private FastLineRenderableSeries askLineSeries;
    private XyDataSeries<Double, Double> askDataSeries;

    private FastLineRenderableSeries bidLineSeries;
    private XyDataSeries<Double, Double> bidDataSeries;

    private Map<Integer, Double> askMapList;
    private Map<Integer, Double> askMapSumList = new LinkedHashMap();

    private Map<Integer, Double> bidsMapList = new HashMap();
    private Map<Integer, Double> bidsMapSumList = new LinkedHashMap();

    private OhlcDataSeries<Date, Double> ohlcDataSeries;

    ScheduledExecutorService exec;

    @NonNull
    @Override
    protected ExampleDepthChartsFragmentBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ExampleDepthChartsFragmentBinding.inflate(inflater);
    }

    @Override
    protected void initExample(@NonNull ExampleDepthChartsFragmentBinding binding) {
        binding.depthChartSurface.setTheme(R.style.SciChart_NavyBlue);
        binding.ohlcChartSurface.setTheme(R.style.SciChart_NavyBlue);

        askMapList = readCsv("data/depth_chart/asks_initial_data.csv");

        bidsMapList = new TreeMap<Integer, Double>(
                new Comparator<Integer>() {
                    @Override
                    public int compare(Integer o1, Integer o2) {
                        return o2.compareTo(o1);
                    }
                }
        );
        bidsMapList.putAll(Objects.requireNonNull(readCsv("data/depth_chart/bids_initial_data.csv")));

        configData();
    }

    private Map<Integer, Double> readCsv(String fileName) {

        try {
            InputStreamReader inputStream = new InputStreamReader(Objects.requireNonNull(getContext()).getAssets().open(fileName));
            BufferedReader reader = new BufferedReader(inputStream);

            StringBuilder total = new StringBuilder();
            String line;

            Map<Integer, Double> map = new HashMap();

            while ((line = reader.readLine()) != null) {
                total.append(line).append('\n');
                String[] data = line.split(",");
                map.put(Integer.valueOf(data[0]), Double.valueOf(data[1]));
            }

            return map;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void configData() {

        askMapList = new TreeMap(askMapList);

        Double sum = 0.0;
        for (Map.Entry<Integer, Double> entry : askMapList.entrySet()) {
            sum += entry.getValue();
            askMapSumList.put(entry.getKey(), sum);
        }

        Double sum2 = 0.0;
        for (Map.Entry<Integer, Double> entry : bidsMapList.entrySet()) {
            sum2 += entry.getValue();
            bidsMapSumList.put(entry.getKey(), sum2);
        }

        bidsMapSumList = new TreeMap(bidsMapSumList);

        configDepthChart();
        configOhlcChart();

        exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                updateChart();

            }
        }, 0, 1, TimeUnit.SECONDS);

    }

    private void configOhlcChart() {
        try {
            InputStreamReader inputStream = new InputStreamReader(getContext().getAssets().open("data/depth_chart/ohlc_data.csv"));
            BufferedReader reader = new BufferedReader(inputStream);

            StringBuilder total = new StringBuilder();
            String line;

            Map<Date, List<Double>> map = new HashMap<Date, List<Double>>();

            while ((line = reader.readLine()) != null) {
                total.append(line).append('\n');
                String[] data = line.split(",");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                Date date = sdf.parse(data[0]);
                List<Double> mapValue = Arrays.asList(
                        Double.valueOf(data[1]) != null ? Double.valueOf(data[1]) : -1.0,
                        Double.valueOf(data[2]) != null ? Double.valueOf(data[2]) : -1.0,
                        Double.valueOf(data[3]) != null ? Double.valueOf(data[3]) : -1.0,
                        Double.valueOf(data[4]) != null ? Double.valueOf(data[4]) : -1.0
                );
                map.put(date, mapValue);
            }

            ohlcDataSeries = sciChartBuilder.newOhlcDataSeries(Date.class, Double.class).build();
            for (Map.Entry<Date, List<Double>> entry : map.entrySet()) {
                ohlcDataSeries.setAcceptsUnsortedData(true);

                ohlcDataSeries.append(
                        entry.getKey(),
                        entry.getValue().get(0),
                        entry.getValue().get(1),
                        entry.getValue().get(2),
                        entry.getValue().get(3)
                );
            }

            FastCandlestickRenderableSeries ohlcSeries = new FastCandlestickRenderableSeries();
            ohlcSeries.setDataSeries(ohlcDataSeries);
            ohlcSeries.setFillDownBrushStyle(new SolidBrushStyle(Color.RED));
            ohlcSeries.setFillUpBrushStyle(new SolidBrushStyle(Color.GREEN));

            ohlcSeries.setStrokeDownStyle(new SolidPenStyle(Color.RED, true, 3f, null));
            ohlcSeries.setStrokeUpStyle(new SolidPenStyle(Color.GREEN, true, 3f, null));

            DateAxis dateAxis = new DateAxis(getContext());
//            dateAxis.setVisibleRange(new DateRange(
//                    new Date(1657623600000L),
//                    new Date(1660305120000L)
//            ));


            UpdateSuspender.using(binding.ohlcChartSurface, () -> {
                Collections.addAll(binding.ohlcChartSurface.getXAxes(), dateAxis);
                Collections.addAll(binding.ohlcChartSurface.getYAxes(), new NumericAxis(getContext()));

                Collections.addAll(binding.ohlcChartSurface.getRenderableSeries(), ohlcSeries);
                Collections.addAll(
                        binding.ohlcChartSurface.getChartModifiers(),
                        new PinchZoomModifier(),
                        new ZoomPanModifier(),
                        new ZoomExtentsModifier()
                );
            });
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void configDepthChart() {
        askLineSeries = new FastLineRenderableSeries();
        askDataSeries = new XyDataSeries(Double.class, Double.class);

        for (Map.Entry<Integer, Double> entry : askMapSumList.entrySet()) {
            askDataSeries.setAcceptsUnsortedData(true);
            askDataSeries.append(Double.valueOf(entry.getKey()), entry.getValue());
        }
        askLineSeries.setDataSeries(askDataSeries);
        askLineSeries.getDataSeries().setAcceptsUnsortedData(true);
        askLineSeries.setStrokeStyle(new SolidPenStyle(Color.RED, true, 3f, null));

        bidLineSeries = new FastLineRenderableSeries();
        bidDataSeries = new XyDataSeries(Double.class, Double.class);
        for (Map.Entry<Integer, Double> entry : bidsMapSumList.entrySet()) {
            bidDataSeries.setAcceptsUnsortedData(true);
            bidDataSeries.append(Double.valueOf(entry.getKey()), entry.getValue());
        }
        bidLineSeries.setDataSeries(bidDataSeries);
        bidLineSeries.getDataSeries().setAcceptsUnsortedData(true);
        bidLineSeries.setStrokeStyle(new SolidPenStyle(Color.GREEN, true, 3f, null));

        NumericAxis xAxis = new NumericAxis(getContext());
        NumericAxis yAxis = new NumericAxis(getContext());

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            yAxis.setAxisAlignment(AxisAlignment.Bottom);
            xAxis.setAxisAlignment(AxisAlignment.Left);
            xAxis.setFlipCoordinates(true);
        }

        DepthChartRollover depthChartRollver = new DepthChartRollover(
                requireContext(),
                bidLineSeries,
                askLineSeries
        );

        UpdateSuspender.using(binding.depthChartSurface, () -> {
            Collections.addAll(binding.depthChartSurface.getXAxes(), xAxis);
            Collections.addAll(binding.depthChartSurface.getYAxes(), yAxis);

            Collections.addAll(binding.depthChartSurface.getRenderableSeries(), askLineSeries, bidLineSeries);
//            Collections.addAll(binding.depthChartSurface.getRenderableSeries(), bidLineSeries);
            Collections.addAll(binding.depthChartSurface.getChartModifiers(), depthChartRollver
            );
        });
    }

    private void updateChart() {
        for (Map.Entry<Integer, Double> entry : askMapList.entrySet()) {
            Double random = Math.random() / 200;
            if (Math.floorMod((int) (Math.random() * 100), (int) 2.0) == 0) {
                if (entry.getValue() - random < 0) {
                    askMapList.put(entry.getKey(), entry.getValue());
                } else {
                    askMapList.put(entry.getKey(), entry.getValue() - random);
                }
            } else {
                askMapList.put(entry.getKey(), entry.getValue() + random);
            }
        }

        Double sum = 0.0;
        for (Map.Entry<Integer, Double> entry : askMapList.entrySet()) {
            sum += entry.getValue();
            askMapSumList.put(entry.getKey(), sum);
        }

        for (Map.Entry<Integer, Double> entry : bidsMapList.entrySet()) {
            Double random = Math.random() / 200;
            if (Math.floorMod((int) (Math.random() * 100), (int) 2.0) == 0) {
                if (entry.getValue() - random < 0) {
                    bidsMapList.put(entry.getKey(), entry.getValue());
                } else {
                    bidsMapList.put(entry.getKey(), entry.getValue() - random);
                }
            } else {
                bidsMapList.put(entry.getKey(), entry.getValue() + random);
            }
        }

        Double sum2 = 0.0;
        for (Map.Entry<Integer, Double> entry : bidsMapList.entrySet()) {
            sum2 += entry.getValue();
            bidsMapSumList.put(entry.getKey(), sum2);
        }

        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UpdateSuspender.using(binding.depthChartSurface, () -> {

                    Integer[] askKeys = askMapSumList.keySet().toArray(new Integer[0]);

                    for (int i = 0; i < askKeys.length; i++) {
                        askDataSeries.updateXyAt(
                                i,
                                Double.valueOf(askKeys[i]),
                                askMapSumList.get(askKeys[i])
                        );
                    }

                    Integer[] bidsKeys = bidsMapSumList.keySet().toArray(new Integer[0]);

                    for (int i = 0; i < bidsKeys.length; i++) {
                        bidDataSeries.updateXyAt(
                                i,
                                Double.valueOf(bidsKeys[i]),
                                bidsMapSumList.get(bidsKeys[i])
                        );
                    }
                });
//                UpdateSuspender.using(binding.ohlcChartSurface, () -> {
//
//                    double close = 0.0;
//                    if (Math.floorMod((int) (Math.random() * 100), (int) 2.0) == 0) {
//                        close = 23692.04 + Math.random() * 100;
//                    } else {
//                        close = 23692.04 - Math.random() * 100;
//                    }
//
//                    ohlcDataSeries.update(
//                            ohlcDataSeries.getCount() - 1,
//                            23590.04,
//                            23695.04,
//                            23480.04, close);
//                });
            }
        });
    }

    class DepthChartRollover extends ChartModifierBase {

        Context context;
        int crosshairStrokeThickness = 2;
        double midPoint = 0;

        IRenderableSeries buySeries;
        IRenderableSeries sellSeries;
        VerticalLineAnnotation xBuyLineAnnotation;
        LineAnnotation yBuyLineAnnotation;
        VerticalLineAnnotation xSellLineAnnotation;
        LineAnnotation ySellLineAnnotation;
        VerticalLineAnnotation midLine;
        TextAnnotation buyLabel;
        TextAnnotation sellLabel;


        public DepthChartRollover(Context  context, IRenderableSeries buySeries, IRenderableSeries sellSeries) {
            this.context = context;
            this.buySeries = buySeries;
            this.sellSeries = sellSeries;

        }

        @Override
        public void attachTo(IServiceContainer services) {
            super.attachTo(services);
            xBuyLineAnnotation = createVerticalLineAnnotation(Color.GREEN);
            xSellLineAnnotation = createVerticalLineAnnotation(Color.RED);

            yBuyLineAnnotation = createHorizontalLineAnnotation(Color.GREEN);
            ySellLineAnnotation = createHorizontalLineAnnotation(Color.RED);

            buyLabel = createTextAnnotation();
            buyLabel.setHorizontalAnchorPoint(HorizontalAnchorPoint.Right);
            sellLabel = createTextAnnotation();
            sellLabel.setHorizontalAnchorPoint(HorizontalAnchorPoint.Left);

            this.getParentSurface().getAnnotations().add(xBuyLineAnnotation);
            this.getParentSurface().getAnnotations().add(xSellLineAnnotation);

            this.getParentSurface().getAnnotations().add(yBuyLineAnnotation);
            this.getParentSurface().getAnnotations().add(ySellLineAnnotation);

            this.getParentSurface().getAnnotations().add(buyLabel);
            this.getParentSurface().getAnnotations().add(sellLabel);

            createMidLine();
        }

        @Override
        public void detach() {
            super.detach();

        }

        @Override
        public void onTouch(ModifierTouchEventArgs args) {
            HitTestInfo buyHitTestInfo = new HitTestInfo();
            HitTestInfo sellHitTestInfo = new HitTestInfo();
            super.onTouch(args);
            switch (args.e.getAction()){
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    xBuyLineAnnotation.setIsHidden(false);
                    xSellLineAnnotation.setIsHidden(false);
                    yBuyLineAnnotation.setIsHidden(false);
                    ySellLineAnnotation.setIsHidden(false);
                    buyLabel.setIsHidden(false);
                    sellLabel.setIsHidden(false);

                    midLine.setIsHidden(false);
                    buySeries.verticalSliceHitTest(buyHitTestInfo, args.e.getX(), args.e.getY());
                    sellSeries.verticalSliceHitTest(sellHitTestInfo, args.e.getX(), args.e.getY());
                    if(buyHitTestInfo.isHit){
                        double buyValue = (double) ((XyDataSeries) buySeries.getDataSeries()).getXValues().get(buyHitTestInfo.dataSeriesIndex);
                        xBuyLineAnnotation.setX1(buyValue);

                        double sellValue = midPoint + (midPoint - buyValue);
                        xSellLineAnnotation.setX1(sellValue);

                        // Horizontal buy line
                        double buyYValue = (double) ((XyDataSeries) buySeries.getDataSeries()).getYValues().get(buyHitTestInfo.dataSeriesIndex);
                        yBuyLineAnnotation.setX1(buyValue);
                        yBuyLineAnnotation.setX2(midPoint);

                        yBuyLineAnnotation.setY1(buyYValue);
                        yBuyLineAnnotation.setY2(buyYValue);

                        // Horizontal sell line
                        int index = ((XyDataSeries) sellSeries.getDataSeries()).getXValues().indexOf(sellValue);
                        double sellYValue = (double) ((XyDataSeries) sellSeries.getDataSeries()).getYValues().get(index);
                        ySellLineAnnotation.setX1(midPoint);
                        ySellLineAnnotation.setX2(sellValue);

                        ySellLineAnnotation.setY1(sellYValue);
                        ySellLineAnnotation.setY2(sellYValue);

                        // Text Annotation
                        buyLabel.setX1(midPoint);
                        buyLabel.setY1(buyYValue);
                        buyLabel.setText(String.format("%1.2f", buyYValue));

                        sellLabel.setX1(midPoint);
                        sellLabel.setY1(sellYValue);
                        sellLabel.setText(String.format("%1.2f", sellYValue));

                    }
                    if(sellHitTestInfo.isHit){
                        double sellValue = (double) ((XyDataSeries) sellSeries.getDataSeries()).getXValues().get(sellHitTestInfo.dataSeriesIndex);
                        xSellLineAnnotation.setX1(sellValue);

                        double buyValue = midPoint - (sellValue - midPoint);
                        xBuyLineAnnotation.setX1(buyValue);

                        // Horizontal line
                        double sellYValue = (double) ((XyDataSeries) sellSeries.getDataSeries()).getYValues().get(sellHitTestInfo.dataSeriesIndex);
                        ySellLineAnnotation.setX1(midPoint);
                        ySellLineAnnotation.setX2(sellValue);

                        ySellLineAnnotation.setY1(sellYValue);
                        ySellLineAnnotation.setY2(sellYValue);

                        // Horizontal sell line
                        int index = ((XyDataSeries) buySeries.getDataSeries()).getXValues().indexOf(buyValue);
                        double buyYValue = (double) ((XyDataSeries) buySeries.getDataSeries()).getYValues().get(index);
                        yBuyLineAnnotation.setX1(buyValue);
                        yBuyLineAnnotation.setX2(midPoint);

                        yBuyLineAnnotation.setY1(buyYValue);
                        yBuyLineAnnotation.setY2(buyYValue);

                        // Text Annotation
                        buyLabel.setX1(midPoint);
                        buyLabel.setY1(buyYValue);
                        buyLabel.setText(String.format("%1.2f", buyYValue));

                        sellLabel.setX1(midPoint);
                        sellLabel.setY1(sellYValue);
                        sellLabel.setText(String.format("%1.2f", sellYValue));
                    }
                    args.isHandled = true;
                    break;
                case MotionEvent.ACTION_UP:
                    xBuyLineAnnotation.setIsHidden(true);
                    xSellLineAnnotation.setIsHidden(true);
                    yBuyLineAnnotation.setIsHidden(true);
                    ySellLineAnnotation.setIsHidden(true);
                    midLine.setIsHidden(true);
                    buyLabel.setIsHidden(true);
                    sellLabel.setIsHidden(true);
                    args.isHandled = true;
                    break;
                default:
                    break;
            }
        }



        private VerticalLineAnnotation createVerticalLineAnnotation(
                int lineColor
        ){
            VerticalLineAnnotation lineAnnotation = new VerticalLineAnnotation(context);
            lineAnnotation.setStroke(new SolidPenStyle(lineColor, true, this.crosshairStrokeThickness, null));
            lineAnnotation.setCoordinateMode(AnnotationCoordinateMode.RelativeY);
            lineAnnotation.setIsHidden(true);

            AnnotationLabel annotationLabel = new AnnotationLabel(context);
            annotationLabel.setLabelPlacement(LabelPlacement.Axis);
            lineAnnotation.annotationLabels.add(annotationLabel);
            return lineAnnotation;
        }

        private LineAnnotation createHorizontalLineAnnotation(
                int lineColor
        ){
            LineAnnotation lineAnnotation = new LineAnnotation(context);
            lineAnnotation.setStroke(new SolidPenStyle(lineColor, true, 5, null));
            lineAnnotation.setIsHidden(true);

            return lineAnnotation;
        }

        private TextAnnotation createTextAnnotation(){
            TextAnnotation textAnnotation = new TextAnnotation(context);
            textAnnotation.setFontStyle(new FontStyle(20, Color.WHITE));
            textAnnotation.setPadding(10, 0,10,0);
            return textAnnotation;
        }


        private void createMidLine(){
            ISciList buyValues = ((XyDataSeries) buySeries.getDataSeries()).getXValues();
            double buyPoint = (double) buyValues.get(buyValues.size() - 1);

            ISciList sellValues = ((XyDataSeries) sellSeries.getDataSeries()).getXValues();
            double sellPoint = (double) sellValues.get(0);

            midPoint = (buyPoint + sellPoint) / 2;

            midLine = new VerticalLineAnnotation(context);
            midLine.setStroke(new SolidPenStyle(Color.WHITE, true, 3, null));
            midLine.setCoordinateMode(AnnotationCoordinateMode.RelativeY);
            midLine.setIsHidden(true);
            midLine.setX1((int) midPoint);

            this.getParentSurface().getAnnotations().add(midLine);

        }
    }
}
