//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2019. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// EcgShowcaseFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.showcase.ecg;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.scichart.charting.layoutManagers.ChartLayoutState;
import com.scichart.charting.layoutManagers.DefaultLayoutManager;
import com.scichart.charting.layoutManagers.VerticalAxisLayoutStrategy;
import com.scichart.charting.model.dataSeries.IDataSeries;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.AxisLayoutState;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker;
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.XyRenderableSeriesBase;
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IStrokePaletteProvider;
import com.scichart.charting.visuals.renderableSeries.paletteProviders.PaletteProviderBase;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.model.DoubleValues;
import com.scichart.core.model.IntegerValues;
import com.scichart.drawing.common.PenStyle;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.components.StepProgressBar;
import com.scichart.examples.fragments.showcase.ShowcaseExampleBaseFragment;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class EcgShowcaseFragment extends ShowcaseExampleBaseFragment {
    private static final int FIFO_CAPACITY = 7850;

    private final XyDataSeries<Double, Double> ecgDataSeries = newDataSeries(FIFO_CAPACITY);
    private final XyDataSeries<Double, Double> ecgSweepDataSeries = newDataSeries(FIFO_CAPACITY);
    private final XyDataSeries<Double, Double> bloodPressureDataSeries = newDataSeries(FIFO_CAPACITY);
    private final XyDataSeries<Double, Double> bloodPressureSweepDataSeries = newDataSeries(FIFO_CAPACITY);
    private final XyDataSeries<Double, Double> bloodVolumeDataSeries = newDataSeries(FIFO_CAPACITY);
    private final XyDataSeries<Double, Double> bloodVolumeSweepDataSeries = newDataSeries(FIFO_CAPACITY);
    private final XyDataSeries<Double, Double> bloodOxygenationDataSeries = newDataSeries(FIFO_CAPACITY);
    private final XyDataSeries<Double, Double> bloodOxygenationSweepDataSeries = newDataSeries(FIFO_CAPACITY);

    private final XyDataSeries<Double, Double> lastEcgSweepDataSeries = newDataSeries(1);
    private final XyDataSeries<Double, Double> lastBloodPressureDataSeries = newDataSeries(1);
    private final XyDataSeries<Double, Double> lastBloodVolumeDataSeries = newDataSeries(1);
    private final XyDataSeries<Double, Double> lastBloodOxygenationSweepDataSeries = newDataSeries(1);

    private final EcgIndicatorsProvider indicatorsProvider = new EcgIndicatorsProvider();

    @BindView(R.id.chart)
    SciChartSurface chart;

    @BindView(R.id.heartIcon)
    ImageView heartIcon;

    @BindView(R.id.bpmValueLabel)
    TextView bpmValue;

    @BindView(R.id.bloodPressureValue)
    TextView bpValue;

    @BindView(R.id.bloodPressureBar)
    StepProgressBar bpBar;

    @BindView(R.id.spoValueLabel)
    TextView spoValue;

    @BindView(R.id.spoClockLabel)
    TextView spoClockValue;

    @BindView(R.id.bloodVolumeValueLabel)
    TextView bvValue;

    @BindView(R.id.svBar1)
    StepProgressBar svBar1;

    @BindView(R.id.svBar2)
    StepProgressBar svBar2;

    private final EcgDataBatch dataBatch = new EcgDataBatch();

    @Override
    protected int getLayoutId() {
        return R.layout.example_ecg_fragment;
    }

    @Override
    protected void initExample() {
        setUpChart();

        final DefaultEcgDataProvider dataProvider = new DefaultEcgDataProvider(getActivity());

        dataProvider.getData().buffer(50, TimeUnit.MILLISECONDS).doOnNext(ecgData -> {
            if(ecgData.isEmpty()) return;

            dataBatch.updateData(ecgData);

            UpdateSuspender.using(chart, () -> {
                final DoubleValues xValues = dataBatch.xValues;

                ecgDataSeries.append(xValues, dataBatch.ecgHeartRateValuesA);
                ecgSweepDataSeries.append(xValues, dataBatch.ecgHeartRateValuesB);

                bloodPressureDataSeries.append(xValues, dataBatch.bloodPressureValuesA);
                bloodPressureSweepDataSeries.append(xValues, dataBatch.bloodPressureValuesB);

                bloodOxygenationDataSeries.append(xValues, dataBatch.bloodOxygenationA);
                bloodOxygenationSweepDataSeries.append(xValues, dataBatch.bloodOxygenationB);

                bloodVolumeDataSeries.append(xValues, dataBatch.bloodVolumeValuesA);
                bloodVolumeSweepDataSeries.append(xValues, dataBatch.bloodVolumeValuesB);

                final EcgData lastEcgData = dataBatch.lastEcgData;
                final double xValue = lastEcgData.xValue;

                lastEcgSweepDataSeries.append(xValue, lastEcgData.ecgHeartRate);
                lastBloodPressureDataSeries.append(xValue, lastEcgData.bloodPressure);
                lastBloodOxygenationSweepDataSeries.append(xValue, lastEcgData.bloodOxygenation);
                lastBloodVolumeDataSeries.append(xValue, lastEcgData.bloodVolume);
            });
        }).compose(bindToLifecycle()).subscribe();

        updateIndicators(0);
        Observable.interval(0, 1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .doOnNext(this::updateIndicators)
                .compose(bindToLifecycle()).subscribe();
    }

    private void updateIndicators(long time) {
        heartIcon.setVisibility(time % 2 == 0 ? View.VISIBLE : View.INVISIBLE);

        if(time % 5 == 0) {
            indicatorsProvider.update();

            bpmValue.setText(indicatorsProvider.getBpmValue());

            bpValue.setText(indicatorsProvider.getBpValue());
            bpBar.setProgress(indicatorsProvider.getBpbValue());

            bvValue.setText(indicatorsProvider.getBvValue());
            svBar1.setProgress(indicatorsProvider.getBvBar1Value());
            svBar2.setProgress(indicatorsProvider.getBvBar2Value());

            spoValue.setText(indicatorsProvider.getSpoValue());
            spoClockValue.setText(indicatorsProvider.getSpoClockValue());
        }
    }

    private void setUpChart() {
        final NumericAxis xAxis = sciChartBuilder.newNumericAxis()
                .withVisibleRange(0, 10)
                .withAutoRangeMode(AutoRange.Never)
                .withDrawMinorGridLines(false)
                .withDrawMajorBands(false)
                .withVisibility(View.GONE)
                .build();

        final String ecgId = "ecgId";
        final String bloodPressureId = "bloodPressureId";
        final String bloodVolumeId = "bloodVolumeId";
        final String bloodOxygenationId = "bloodOxygenationId";

        final NumericAxis yAxisEcg = generateYAxis(ecgId);
        final NumericAxis yAxisPressure = generateYAxis(bloodPressureId);
        final NumericAxis yAxisVolume = generateYAxis(bloodVolumeId);
        final NumericAxis yAxisOxygenation = generateYAxis(bloodOxygenationId);

        final Context context = getActivity();

        final int heartRateColor = ContextCompat.getColor(context, R.color.heart_rate_color);
        final int bloodPressureColor = ContextCompat.getColor(context, R.color.blood_pressure_color);
        final int bloodVolumeColor = ContextCompat.getColor(context, R.color.blood_volume_color);
        final int bloodOxygenation = ContextCompat.getColor(context, R.color.blood_oxygenation_color);

        UpdateSuspender.using(chart, () -> {
            Collections.addAll(chart.getXAxes(), xAxis);
            Collections.addAll(chart.getYAxes(), yAxisEcg, yAxisPressure, yAxisVolume, yAxisOxygenation);

            Collections.addAll(chart.getRenderableSeries(),
                    generateLineSeries(ecgId, ecgDataSeries, sciChartBuilder.newPen().withColor(heartRateColor).withThickness(1f).build()),
                    generateLineSeries(ecgId, ecgSweepDataSeries, sciChartBuilder.newPen().withColor(heartRateColor).withThickness(1f).build()),
                    generateScatterForLastAppendedPoint(ecgId, lastEcgSweepDataSeries),

                    generateLineSeries(bloodPressureId, bloodPressureDataSeries, sciChartBuilder.newPen().withColor(bloodPressureColor).withThickness(1f).build()),
                    generateLineSeries(bloodPressureId, bloodPressureSweepDataSeries, sciChartBuilder.newPen().withColor(bloodPressureColor).withThickness(1f).build()),
                    generateScatterForLastAppendedPoint(bloodPressureId, lastBloodPressureDataSeries),

                    generateLineSeries(bloodVolumeId, bloodVolumeDataSeries, sciChartBuilder.newPen().withColor(bloodVolumeColor).withThickness(1f).build()),
                    generateLineSeries(bloodVolumeId, bloodVolumeSweepDataSeries, sciChartBuilder.newPen().withColor(bloodVolumeColor).withThickness(1f).build()),
                    generateScatterForLastAppendedPoint(bloodVolumeId, lastBloodVolumeDataSeries),

                    generateLineSeries(bloodOxygenationId, bloodOxygenationDataSeries, sciChartBuilder.newPen().withColor(bloodOxygenation).withThickness(1f).build()),
                    generateLineSeries(bloodOxygenationId, bloodOxygenationSweepDataSeries, sciChartBuilder.newPen().withColor(bloodOxygenation).withThickness(1f).build()),
                    generateScatterForLastAppendedPoint(bloodOxygenationId, lastBloodOxygenationSweepDataSeries)
                    );

            chart.setLayoutManager(new DefaultLayoutManager.Builder().setRightOuterAxesLayoutStrategy(new RightAlignedOuterVerticallyStackedYAxisLayoutStrategy()).build());

        });
    }

    private NumericAxis generateYAxis(String id) {
        return sciChartBuilder.newNumericAxis().withAxisId(id).withVisibility(View.GONE).withGrowBy(0.05, 0.05).withAutoRangeMode(AutoRange.Always).withDrawMajorBands(false).withDrawMinorGridLines(false).withDrawMajorGridLines(false).build();
    }

    private IRenderableSeries generateLineSeries(String yAxisId, IDataSeries ds, PenStyle strokeStyle) {
        return sciChartBuilder.newLineSeries().withDataSeries(ds).withYAxisId(yAxisId).withStrokeStyle(strokeStyle).withPaletteProvider(new DimTracePaletteProvider()).build();
    }

    private IRenderableSeries generateScatterForLastAppendedPoint(String yAxisId, IDataSeries ds) {
        final EllipsePointMarker pm = sciChartBuilder.newPointMarker(new EllipsePointMarker())
                .withSize(4)
                .withFill(ColorUtil.White)
                .withStroke(ColorUtil.White, 1f)
                .build();

        return sciChartBuilder.newScatterSeries()
                .withDataSeries(ds)
                .withYAxisId(yAxisId)
                .withPointMarker(pm)
                .build();
    }

    private static XyDataSeries<Double, Double> newDataSeries(int fifoCapacity) {
        final XyDataSeries<Double, Double> ds = new XyDataSeries<>(Double.class, Double.class);
        ds.setFifoCapacity(fifoCapacity);
        return ds;
    }

    private static class EcgDataBatch {
        private final DoubleValues xValues = new DoubleValues();

        private final DoubleValues ecgHeartRateValuesA = new DoubleValues();
        private final DoubleValues bloodPressureValuesA = new DoubleValues();
        private final DoubleValues bloodVolumeValuesA = new DoubleValues();
        private final DoubleValues bloodOxygenationA = new DoubleValues();

        private final DoubleValues ecgHeartRateValuesB = new DoubleValues();
        private final DoubleValues bloodPressureValuesB = new DoubleValues();
        private final DoubleValues bloodVolumeValuesB = new DoubleValues();
        private final DoubleValues bloodOxygenationB = new DoubleValues();

        EcgData lastEcgData;

        final void updateData(List<EcgData> ecgDataList) {
            xValues.clear();
            ecgHeartRateValuesA.clear();
            ecgHeartRateValuesB.clear();
            bloodPressureValuesA.clear();
            bloodPressureValuesB.clear();
            bloodVolumeValuesA.clear();
            bloodVolumeValuesB.clear();
            bloodOxygenationA.clear();
            bloodOxygenationB.clear();

            final int size = ecgDataList.size();
            for (int i = 0; i < size; i++) {
                final EcgData ecgData = ecgDataList.get(i);

                xValues.add(ecgData.xValue);

                if (ecgData.isATrace) {
                    ecgHeartRateValuesA.add(ecgData.ecgHeartRate);
                    bloodPressureValuesA.add(ecgData.bloodPressure);
                    bloodVolumeValuesA.add(ecgData.bloodVolume);
                    bloodOxygenationA.add(ecgData.bloodOxygenation);

                    ecgHeartRateValuesB.add(Double.NaN);
                    bloodPressureValuesB.add(Double.NaN);
                    bloodVolumeValuesB.add(Double.NaN);
                    bloodOxygenationB.add(Double.NaN);
                } else {
                    ecgHeartRateValuesB.add(ecgData.ecgHeartRate);
                    bloodPressureValuesB.add(ecgData.bloodPressure);
                    bloodVolumeValuesB.add(ecgData.bloodVolume);
                    bloodOxygenationB.add(ecgData.bloodOxygenation);

                    ecgHeartRateValuesA.add(Double.NaN);
                    bloodPressureValuesA.add(Double.NaN);
                    bloodVolumeValuesA.add(Double.NaN);
                    bloodOxygenationA.add(Double.NaN);
                }

            }

            lastEcgData = ecgDataList.get(size - 1);
        }
    }

    private static class RightAlignedOuterVerticallyStackedYAxisLayoutStrategy extends VerticalAxisLayoutStrategy {
        @Override
        public void measureAxes(int availableWidth, int availableHeight, ChartLayoutState chartLayoutState) {
            for (int i = 0, size = axes.size(); i < size; i++) {
                final IAxis axis = axes.get(i);

                axis.updateAxisMeasurements();

                final AxisLayoutState axisLayoutState = axis.getAxisLayoutState();

                chartLayoutState.rightOuterAreaSize = Math.max(getRequiredAxisSize(axisLayoutState), chartLayoutState.rightOuterAreaSize);
            }
        }

        @Override
        public void layoutAxes(int left, int top, int right, int bottom) {
            final int size = axes.size();
            final int height = bottom - top;

            final int axisHeight = height / size;

            int topPlacement = top;

            for (int i = 0; i < size; i++) {
                final IAxis axis = axes.get(i);

                final AxisLayoutState axisLayoutState = axis.getAxisLayoutState();

                final int bottomPlacement = Math.round(topPlacement + axisHeight);

                axis.layoutArea(left, topPlacement, left + getRequiredAxisSize(axisLayoutState), bottomPlacement);

                topPlacement = bottomPlacement;
            }
        }
    }

    private static class DimTracePaletteProvider extends PaletteProviderBase<XyRenderableSeriesBase> implements IStrokePaletteProvider {
        private final IntegerValues colors = new IntegerValues();

        private final double startOpacity;
        private final double diffOpacity;

        public DimTracePaletteProvider() {
            super(XyRenderableSeriesBase.class);

            this.startOpacity = 0.2;
            this.diffOpacity = 1 - startOpacity;
        }

        @Override
        public IntegerValues getStrokeColors() {
            return colors;
        }

        @Override
        public void update() {
            final int defaultColor = renderableSeries.getStrokeStyle().getColor();
            final int size = renderableSeries.getCurrentRenderPassData().pointsCount();

            colors.setSize(size);

            final int[] colorsArray = colors.getItemsArray();

            final double doubleSize = (double)size;
            for (int i = 0; i < size; i++) {
                final double faction = i / doubleSize;

                final float opacity = (float) (startOpacity + faction * diffOpacity);

                colorsArray[i] = ColorUtil.argb(defaultColor, opacity);
            }
        }
    }
}
