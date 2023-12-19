//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2019. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// VitalSignsMonitorShowcaseFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.featuredApps.medicalCharts.vitalSignsMonitor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
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
import com.scichart.data.model.DoubleRange;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.databinding.ExampleVitalSignsMonitorFragmentBinding;
import com.scichart.examples.fragments.base.ShowcaseExampleBaseFragment;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class VitalSignsMonitorShowcaseFragment extends ShowcaseExampleBaseFragment<ExampleVitalSignsMonitorFragmentBinding> {
    private static final int FIFO_CAPACITY = 7850;

    private static final String ECG_ID = "ecgId";
    private static final String BLOOD_PRESSURE_ID = "bloodPressureId";
    private static final String BLOOD_VOLUME_ID = "bloodVolumeId";
    private static final String BLOOD_OXYGENATION_ID = "bloodOxygenationId";

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

    private final VitalSignsIndicatorsProvider indicatorsProvider = new VitalSignsIndicatorsProvider();

    private final EcgDataBatch dataBatch = new EcgDataBatch();

    @NonNull
    @Override
    protected ExampleVitalSignsMonitorFragmentBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ExampleVitalSignsMonitorFragmentBinding.inflate(inflater);
    }

    @Override
    protected void initExample(@NonNull ExampleVitalSignsMonitorFragmentBinding binding) {
        final DefaultVitalSignsDataProvider dataProvider = new DefaultVitalSignsDataProvider(requireContext());

        setUpChart(dataProvider);

        dataProvider.getData().buffer(50, TimeUnit.MILLISECONDS).doOnNext(ecgData -> {
            if (ecgData.isEmpty()) return;

            dataBatch.updateData(ecgData);

            UpdateSuspender.using(binding.surface, () -> {
                final DoubleValues xValues = dataBatch.xValues;

                ecgDataSeries.append(xValues, dataBatch.ecgHeartRateValuesA);
                ecgSweepDataSeries.append(xValues, dataBatch.ecgHeartRateValuesB);

                bloodPressureDataSeries.append(xValues, dataBatch.bloodPressureValuesA);
                bloodPressureSweepDataSeries.append(xValues, dataBatch.bloodPressureValuesB);

                bloodOxygenationDataSeries.append(xValues, dataBatch.bloodOxygenationA);
                bloodOxygenationSweepDataSeries.append(xValues, dataBatch.bloodOxygenationB);

                bloodVolumeDataSeries.append(xValues, dataBatch.bloodVolumeValuesA);
                bloodVolumeSweepDataSeries.append(xValues, dataBatch.bloodVolumeValuesB);

                final VitalSignsData lastVitalSignsData = dataBatch.lastVitalSignsData;
                final double xValue = lastVitalSignsData.xValue;

                lastEcgSweepDataSeries.append(xValue, lastVitalSignsData.ecgHeartRate);
                lastBloodPressureDataSeries.append(xValue, lastVitalSignsData.bloodPressure);
                lastBloodOxygenationSweepDataSeries.append(xValue, lastVitalSignsData.bloodOxygenation);
                lastBloodVolumeDataSeries.append(xValue, lastVitalSignsData.bloodVolume);
            });
        }).compose(bindToLifecycle()).subscribe();

        updateIndicators(0);
        Observable.interval(0, 1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .doOnNext(this::updateIndicators)
                .compose(bindToLifecycle()).subscribe();
    }

    private void setUpChart(DefaultVitalSignsDataProvider dataProvider) {
        final NumericAxis xAxis = sciChartBuilder.newNumericAxis()
                .withVisibleRange(0, 10)
                .withAutoRangeMode(AutoRange.Never)
                .withDrawMinorGridLines(false)
                .withDrawMajorBands(false)
                .withVisibility(View.GONE)
                .build();

        final Context context = requireContext();
        final int heartRateColor = ContextCompat.getColor(context, R.color.heart_rate_color);
        final int bloodPressureColor = ContextCompat.getColor(context, R.color.blood_pressure_color);
        final int bloodVolumeColor = ContextCompat.getColor(context, R.color.blood_volume_color);
        final int bloodOxygenation = ContextCompat.getColor(context, R.color.blood_oxygenation_color);

        final SciChartSurface surface = binding.surface;
        surface.setTheme(R.style.SciChart_NavyBlue);
        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(),
                    generateYAxis(ECG_ID, dataProvider.getEcgHeartRateRange()),
                    generateYAxis(BLOOD_PRESSURE_ID, dataProvider.getBloodPressureRange()),
                    generateYAxis(BLOOD_VOLUME_ID, dataProvider.getBloodVolumeRange()),
                    generateYAxis(BLOOD_OXYGENATION_ID, dataProvider.getBloodOxygenationRange())
            );

            Collections.addAll(surface.getRenderableSeries(),
                    generateLineSeries(ECG_ID, ecgDataSeries, heartRateColor),
                    generateLineSeries(ECG_ID, ecgSweepDataSeries, heartRateColor),
                    generateScatterForLastAppendedPoint(ECG_ID, lastEcgSweepDataSeries),

                    generateLineSeries(BLOOD_PRESSURE_ID, bloodPressureDataSeries, bloodPressureColor),
                    generateLineSeries(BLOOD_PRESSURE_ID, bloodPressureSweepDataSeries, bloodPressureColor),
                    generateScatterForLastAppendedPoint(BLOOD_PRESSURE_ID, lastBloodPressureDataSeries),

                    generateLineSeries(BLOOD_VOLUME_ID, bloodVolumeDataSeries, bloodVolumeColor),
                    generateLineSeries(BLOOD_VOLUME_ID, bloodVolumeSweepDataSeries, bloodVolumeColor),
                    generateScatterForLastAppendedPoint(BLOOD_VOLUME_ID, lastBloodVolumeDataSeries),

                    generateLineSeries(BLOOD_OXYGENATION_ID, bloodOxygenationDataSeries, bloodOxygenation),
                    generateLineSeries(BLOOD_OXYGENATION_ID, bloodOxygenationSweepDataSeries, bloodOxygenation),
                    generateScatterForLastAppendedPoint(BLOOD_OXYGENATION_ID, lastBloodOxygenationSweepDataSeries)
            );

            surface.setLayoutManager(new DefaultLayoutManager.Builder().setRightOuterAxesLayoutStrategy(new RightAlignedOuterVerticallyStackedYAxisLayoutStrategy()).build());
        });
    }

    private void updateIndicators(long time) {
        binding.heartRateIndicator.heartIcon.setVisibility(time % 2 == 0 ? View.VISIBLE : View.INVISIBLE);

        if (time % 5 == 0) {
            indicatorsProvider.update();
            binding.heartRateIndicator.bpmValueLabel.setText(indicatorsProvider.getBpmValue());

            binding.bloodPressureIndicator.bloodPressureValue.setText(indicatorsProvider.getBpValue());
            binding.bloodPressureIndicator.bloodPressureBar.setProgress(indicatorsProvider.getBpbValue());

            binding.bloodVolumeIndicator.bloodVolumeValueLabel.setText(indicatorsProvider.getBvValue());
            binding.bloodVolumeIndicator.svBar1.setProgress(indicatorsProvider.getBvBar1Value());
            binding.bloodVolumeIndicator.svBar2.setProgress(indicatorsProvider.getBvBar2Value());

            binding.bloodOxygenationIndicator.spoValueLabel.setText(indicatorsProvider.getSpoValue());
            binding.bloodOxygenationIndicator.spoClockLabel.setText(indicatorsProvider.getSpoClockValue());
        }
    }

    private NumericAxis generateYAxis(String id, DoubleRange visibleRange) {
        return sciChartBuilder.newNumericAxis()
                .withAxisId(id)
                .withVisibility(View.GONE)
                .withVisibleRange(visibleRange)
                .withAutoRangeMode(AutoRange.Never)
                .withDrawMajorBands(false)
                .withDrawMinorGridLines(false)
                .withDrawMajorGridLines(false)
                .build();
    }

    private IRenderableSeries generateLineSeries(String yAxisId, IDataSeries<?, ?> ds, @ColorInt Integer color) {
        return sciChartBuilder.newLineSeries()
                .withDataSeries(ds)
                .withYAxisId(yAxisId)
                .withStrokeStyle(color)
                .withPaletteProvider(new DimTracePaletteProvider())
                .build();
    }

    private IRenderableSeries generateScatterForLastAppendedPoint(String yAxisId, IDataSeries<?, ?> ds) {
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
        ds.setAcceptsUnsortedData(true);
        return ds;
    }

    private static class RightAlignedOuterVerticallyStackedYAxisLayoutStrategy extends VerticalAxisLayoutStrategy {
        @Override
        public void measureAxes(int availableWidth, int availableHeight, ChartLayoutState chartLayoutState) {
            for (int i = 0, size = axes.size(); i < size; i++) {
                final IAxis axis = axes.get(i);
                axis.updateAxisMeasurements();

                chartLayoutState.rightOuterAreaSize = Math.max(getRequiredAxisSize(axis.getAxisLayoutState()), chartLayoutState.rightOuterAreaSize);
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

            for (int i = 0; i < size; i++) {
                final double faction = i / (double)size;
                final float opacity = (float) (startOpacity + faction * diffOpacity);

                colorsArray[i] = ColorUtil.argb(defaultColor, opacity);
            }
        }
    }
}
