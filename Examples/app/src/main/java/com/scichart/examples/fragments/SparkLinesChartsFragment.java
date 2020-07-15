//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2020. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SparkLinesChartsFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.extensions.builders.SciChartBuilder;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SparkLinesChartsFragment extends ExampleBaseFragment {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.example_spark_lines_fragment;
    }

    @Override
    protected void initExample() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        final SparkLineItemModel[] dataSet = new SparkLineItemModel[100];
        for (int i = 0; i < dataSet.length; i++) {
            final RandomWalkGenerator generator = new RandomWalkGenerator();
            dataSet[i] = getRandomItem(i, generator.getRandomWalkSeries(50));
        }

        SparkLinesAdapter adapter = new SparkLinesAdapter(dataSet);
        recyclerView.setAdapter(adapter);
    }

    private SparkLineItemModel getRandomItem(int index, DoubleSeries data){
        final XyDataSeries<Double, Double> dataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).build();
        dataSeries.append(data.xValues, data.yValues);

        return new SparkLineItemModel(dataSeries, String.format(Locale.getDefault(),"Item #%d", index));
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
            this.itemValue = (yValues.get(size - 1)/yValues.get(size - 2)) - 1;
        }
    }
    public static class SparkLinesItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.chart)
        SciChartSurface chart;

        @BindView(R.id.itemName)
        TextView itemName;

        @BindView(R.id.itemValue)
        TextView itemValue;

        private final DecimalFormat format = new DecimalFormat("0.##%");

        public SparkLinesItemViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

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

            Collections.addAll(chart.getXAxes(), xAxis);
            Collections.addAll(chart.getYAxes(), yAxis);
            Collections.addAll(chart.getRenderableSeries(), rs);

            chart.setRenderableSeriesAreaBorderStyle(sciChartBuilder.newPen().withColor(ColorUtil.Transparent).build());
        }

        public void bindItemModel(SparkLineItemModel itemModel) {
            itemName.setText(itemModel.itemName);

            setValue(itemModel.itemValue);

            final IRenderableSeries renderableSeries = chart.getRenderableSeries().get(0);

            renderableSeries.setDataSeries(itemModel.dataSeries);
        }

        private void setValue(double value){
            final String formattedValue = this.format.format(value);

            final StringBuilder sb = new StringBuilder();

            if(value > 0){
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
