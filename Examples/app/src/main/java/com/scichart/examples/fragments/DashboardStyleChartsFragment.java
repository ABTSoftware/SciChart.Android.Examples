//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// DashboardStyleChartsFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.HorizontallyStackedColumnsCollection;
import com.scichart.charting.visuals.renderableSeries.StackedColumnRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.StackedMountainRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.StackedSeriesCollectionBase;
import com.scichart.charting.visuals.renderableSeries.VerticallyStackedColumnsCollection;
import com.scichart.charting.visuals.renderableSeries.VerticallyStackedMountainsCollection;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.R;
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.extensions.builders.SciChartBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;

public class DashboardStyleChartsFragment extends ExampleBaseFragment {

    @Bind(R.id.tabLayout)
    TabLayout tabLayout;

    @Bind(R.id.viewpager)
    ViewPager viewPager;

    private final List<ChartTypeModel> chartTypesSource = new ArrayList<>();
    private static int[] seriesColors = new int[DashboardDataHelper.seriesColors.length];

    @Override
    protected int getLayoutId() {
        return R.layout.example_dashboard_style_chart_fragment;
    }

    @Override
    protected void initExample() {
        for (int i = 0; i < DashboardDataHelper.seriesColors.length; i++) {
            int color = getResources().getColor(DashboardDataHelper.seriesColors[i]);
            seriesColors[i] = color;
        }

        chartTypesSource.add(ChartTypeModelFactory.newHorizontallyStackedColumns(sciChartBuilder));
        chartTypesSource.add(ChartTypeModelFactory.newVerticallyStackedColumns(sciChartBuilder, false));
        chartTypesSource.add(ChartTypeModelFactory.newVerticallyStackedColumns(sciChartBuilder, true));
        chartTypesSource.add(ChartTypeModelFactory.newVerticallyStackedMountains(sciChartBuilder, false));
        chartTypesSource.add(ChartTypeModelFactory.newVerticallyStackedMountains(sciChartBuilder, true));

        //this line fixes swiping lag of the viewPager by caching the pages
        viewPager.setOffscreenPageLimit(5);
        viewPager.setAdapter(new ViewPagerAdapter(this.getActivity().getBaseContext(), chartTypesSource));
        tabLayout.setupWithViewPager(viewPager);
    }

    class ViewPagerAdapter extends PagerAdapter {

        private Context context;
        private final List<ChartTypeModel> chartTypesSource;

        public ViewPagerAdapter(Context context, List<ChartTypeModel> chartTypesSource) {
            this.context = context;
            this.chartTypesSource = chartTypesSource;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View chartView = inflater.inflate(R.layout.example_single_chart_fragment, collection, false);

            ChartTypeModel chartTypeModel = chartTypesSource.get(position);

            updateSurface(chartTypeModel, chartView);
            collection.addView(chartView);

            return chartView;
        }

        private void updateSurface(ChartTypeModel chartTypeModel, View chartView) {
            final SciChartSurface surface = (SciChartSurface) chartView.findViewById(R.id.chart);

            final IAxis xAxis = sciChartBuilder.newNumericAxis().build();
            final IAxis yAxis = sciChartBuilder.newNumericAxis().build();

            final StackedSeriesCollectionBase seriesCollection = chartTypeModel.getSeriesCollection();

            UpdateSuspender.using(surface, new Runnable() {
                @Override
                public void run() {
                    Collections.addAll(surface.getRenderableSeries(), seriesCollection);
                    Collections.addAll(surface.getXAxes(), xAxis);
                    Collections.addAll(surface.getYAxes(), yAxis);
                }
            });
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return chartTypesSource.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            ChartTypeModel chartTypeModel = chartTypesSource.get(position);
            return chartTypeModel.getTypeName();
        }

    }

    private static class ChartTypeModelFactory {

        static ChartTypeModel newHorizontallyStackedColumns(SciChartBuilder sciChartBuilder) {
            HorizontallyStackedColumnsCollection seriesCollection = new HorizontallyStackedColumnsCollection();

            for (int i = 0; i < 5; i++) {
                final IXyDataSeries<Double, Double> dataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Series " + (i + 1)).build();
                dataSeries.append(DashboardDataHelper.xValues, DashboardDataHelper.yValues[i]);

                StackedColumnRenderableSeries rSeries = sciChartBuilder.newStackedColumn().withDataSeries(dataSeries).withLinearGradientColors(seriesColors[i * 2 + 1], seriesColors[i * 2]).withStrokeStyle(seriesColors[i * 2]).build();
                seriesCollection.add(rSeries);
            }

            String name = "Stacked columns side-by-side";
            return new ChartTypeModel(seriesCollection, name);
        }

        static ChartTypeModel newVerticallyStackedColumns(SciChartBuilder sciChartBuilder, boolean isOneHundredPercent) {
            VerticallyStackedColumnsCollection seriesCollection = new VerticallyStackedColumnsCollection();
            seriesCollection.setIsOneHundredPercent(isOneHundredPercent);

            for (int i = 0; i < 5; i++) {
                final IXyDataSeries<Double, Double> dataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Series " + (i + 1)).build();
                dataSeries.append(DashboardDataHelper.xValues, DashboardDataHelper.yValues[i]);

                StackedColumnRenderableSeries rSeries = sciChartBuilder.newStackedColumn().withDataSeries(dataSeries).withLinearGradientColors(seriesColors[i * 2 + 1], seriesColors[i * 2]).withStrokeStyle(seriesColors[i * 2]).build();
                seriesCollection.add(rSeries);
            }

            String name = isOneHundredPercent ? "100% " : "";
            name += "Stacked columns";
            return new ChartTypeModel(seriesCollection, name);
        }

        static ChartTypeModel newVerticallyStackedMountains(SciChartBuilder sciChartBuilder, boolean isOneHundredPercent) {
            VerticallyStackedMountainsCollection seriesCollection = new VerticallyStackedMountainsCollection();
            seriesCollection.setIsOneHundredPercent(isOneHundredPercent);

            for (int i = 0; i < 5; i++) {
                final IXyDataSeries<Double, Double> dataSeries = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Series " + (i + 1)).build();
                dataSeries.append(DashboardDataHelper.xValues, DashboardDataHelper.yValues[i]);

                StackedMountainRenderableSeries rSeries = sciChartBuilder.newStackedMountain().withDataSeries(dataSeries).withLinearGradientColors(seriesColors[i * 2 + 1], seriesColors[i * 2]).withStrokeStyle(seriesColors[i * 2]).build();
                seriesCollection.add(rSeries);
            }

            String name = isOneHundredPercent ? "100% " : "";
            name += "Stacked mountains";
            return new ChartTypeModel(seriesCollection, name);
        }
    }

    private static class DashboardDataHelper {
        private static final Double[] xValues = {0d, 1d, 2d, 3d, 4d, 5d, 6d, 7d, 8d, 9d, 10d, 11d};

        private static final Double[][] yValues = {
                new Double[]{10d, 13d, 7d, 16d, 4d, 6d, 20d, 14d, 16d, 10d, 24d, 11d},
                new Double[]{12d, 17d, 21d, 15d, 19d, 18d, 13d, 21d, 22d, 20d, 5d, 10d},
                new Double[]{7d, 30d, 27d, 24d, 21d, 15d, 17d, 26d, 22d, 28d, 21d, 22d},
                new Double[]{16d, 10d, 9d, 8d, 22d, 14d, 12d, 27d, 25d, 23d, 17d, 17d},
                new Double[]{7d, 24d, 21d, 11d, 19d, 17d, 14d, 27d, 26d, 22d, 28d, 16d}
        };

        private static final int[] seriesColors = {
                R.color.dashboard_chart_blue_series_0, R.color.dashboard_chart_blue_series_1,
                R.color.dashboard_chart_orange_series_0, R.color.dashboard_chart_orange_series_1,
                R.color.dashboard_chart_red_series_0, R.color.dashboard_chart_red_series_1,
                R.color.dashboard_chart_green_series_0, R.color.dashboard_chart_green_series_1,
                R.color.dashboard_chart_violet_series_0, R.color.dashboard_chart_violet_series_1};
    }
}

class ChartTypeModel {
    private final StackedSeriesCollectionBase seriesCollection;
    private final String typeName;

    public ChartTypeModel(StackedSeriesCollectionBase seriesCollection, String header) {
        this.seriesCollection = seriesCollection;
        this.typeName = header;
    }

    public StackedSeriesCollectionBase getSeriesCollection() {
        return seriesCollection;
    }

    public String getTypeName() {
        return typeName;
    }
}