//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SparkLinesChartsFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.multiChart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries;
import com.scichart.data.model.DoubleRange;
import com.scichart.data.model.ISciList;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.data.DoubleSeries;
import com.scichart.examples.data.RandomWalkGenerator;
import com.scichart.examples.databinding.ExampleSparkLinesFragmentBinding;
import com.scichart.examples.databinding.ExampleSparkLinesItemViewBinding;
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.extensions.builders.SciChartBuilder;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Locale;

public class SparkLinesChartsFragment extends ExampleBaseFragment<ExampleSparkLinesFragmentBinding> {
    @NonNull
    @Override
    protected ExampleSparkLinesFragmentBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ExampleSparkLinesFragmentBinding.inflate(inflater);
    }

    @Override
    protected void initExample(ExampleSparkLinesFragmentBinding binding) {
        final RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        final SparkLineItemModel[] dataSet = new SparkLineItemModel[100];
        for (int i = 0; i < dataSet.length; i++) {
            final RandomWalkGenerator generator = new RandomWalkGenerator();
            dataSet[i] = getRandomItem(i, generator.getRandomWalkSeries(50));
        }

        SparkLinesAdapter adapter = new SparkLinesAdapter(dataSet);
        recyclerView.setAdapter(adapter);
    }

    private SparkLineItemModel getRandomItem(int index, DoubleSeries data) {
        final XyDataSeries<Double, Double> dataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).build();
        dataSeries.append(data.xValues, data.yValues);

        return new SparkLineItemModel(dataSeries, String.format(Locale.getDefault(), "Item #%d", index));
    }

    public static class SparkLineItemModel {
        public final IXyDataSeries<Double, Double> dataSeries;
        public final String itemName;
        public final double itemValue;

        public SparkLineItemModel(IXyDataSeries<Double, Double> dataSeries, String itemName) {
            this.dataSeries = dataSeries;
            this.itemName = itemName;

            final ISciList<Double> yValues = dataSeries.getYValues();
            final int size = yValues.size();
            this.itemValue = (yValues.get(size - 1) / yValues.get(size - 2)) - 1;
        }
    }

    public static class SparkLinesItemViewHolder extends RecyclerView.ViewHolder {
        private final ExampleSparkLinesItemViewBinding binding;

        private final DecimalFormat format = new DecimalFormat("0.##%");

        public SparkLinesItemViewHolder(View itemView) {
            super(itemView);

            this.binding = ExampleSparkLinesItemViewBinding.bind(itemView);

            final SciChartBuilder sciChartBuilder = SciChartBuilder.instance();

            final NumericAxis xAxis = sciChartBuilder.newNumericAxis()
                    .withGrowBy(new DoubleRange(0.05d, 0.05d))
                    .withAutoRangeMode(AutoRange.Always)
                    .withDrawMajorBands(false)
                    .withDrawLabels(false)
                    .withDrawMinorTicks(false)
                    .withDrawMajorTicks(false)
                    .withDrawMajorGridLines(false)
                    .withDrawMinorGridLines(false)
                    .build();

            final NumericAxis yAxis = sciChartBuilder.newNumericAxis()
                    .withGrowBy(new DoubleRange(0.1d, 0.1d))
                    .withAutoRangeMode(AutoRange.Always)
                    .withDrawMajorBands(false)
                    .withDrawLabels(false)
                    .withDrawMinorTicks(false)
                    .withDrawMajorTicks(false)
                    .withDrawMajorGridLines(false)
                    .withDrawMinorGridLines(false)
                    .build();

            final IRenderableSeries rs = sciChartBuilder.newLineSeries().withStrokeStyle(ColorUtil.SteelBlue).build();

            final SciChartSurface surface = binding.surface;
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);
            Collections.addAll(surface.getRenderableSeries(), rs);

            surface.setRenderableSeriesAreaBorderStyle(sciChartBuilder.newPen().withColor(ColorUtil.Transparent).build());
        }

        public void bindItemModel(SparkLineItemModel itemModel) {
            binding.itemName.setText(itemModel.itemName);

            setValue(itemModel.itemValue);

            final IRenderableSeries renderableSeries = binding.surface.getRenderableSeries().get(0);
            renderableSeries.setDataSeries(itemModel.dataSeries);
        }

        private void setValue(double value) {
            final String formattedValue = this.format.format(value);
            final StringBuilder sb = new StringBuilder();

            final TextView itemValue = binding.itemValue;
            if (value > 0) {
                sb.append("\u21D1 ");
                itemValue.setTextColor(ColorUtil.Green);
            } else {
                sb.append("\u21D3 ");
                itemValue.setTextColor(ColorUtil.Red);
            }

            sb.append(formattedValue);
            itemValue.setText(sb);
        }
    }

    public static class SparkLinesAdapter extends RecyclerView.Adapter<SparkLinesItemViewHolder> {
        private final SparkLineItemModel[] dataSet;

        public SparkLinesAdapter(SparkLineItemModel[] dataSet) {
            this.dataSet = dataSet;
        }

        @NonNull
        @Override
        public SparkLinesItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_spark_lines_item_view, parent, false);

            return new SparkLinesItemViewHolder(v);
        }

        @Override
        public void onBindViewHolder(SparkLinesItemViewHolder holder, int position) {
            holder.bindItemModel(dataSet[position]);
        }

        @Override
        public int getItemCount() {
            return dataSet.length;
        }
    }
}
