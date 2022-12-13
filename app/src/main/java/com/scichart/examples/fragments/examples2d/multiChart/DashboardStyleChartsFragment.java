//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// DashboardStyleChartsFragment.java is part of SCICHART®, High Performance Scientific Charts
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

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.renderableSeries.HorizontallyStackedColumnsCollection;
import com.scichart.charting.visuals.renderableSeries.StackedColumnRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.StackedMountainRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.StackedSeriesCollectionBase;
import com.scichart.charting.visuals.renderableSeries.VerticallyStackedColumnsCollection;
import com.scichart.charting.visuals.renderableSeries.VerticallyStackedMountainsCollection;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.R;
import com.scichart.examples.databinding.ExampleDashboardStyleChartFragmentBinding;
import com.scichart.examples.databinding.ExampleSingleChartFragmentBinding;
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.extensions.builders.SciChartBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DashboardStyleChartsFragment extends ExampleBaseFragment<ExampleDashboardStyleChartFragmentBinding> {

    private static int[] seriesColors = new int[DashboardDataHelper.seriesColors.length];

    @NonNull
    @Override
    protected ExampleDashboardStyleChartFragmentBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ExampleDashboardStyleChartFragmentBinding.inflate(inflater);
    }

    @Override
    protected void initExample(@NonNull ExampleDashboardStyleChartFragmentBinding binding) {
        for (int i = 0; i < DashboardDataHelper.seriesColors.length; i++) {
            int color = getResources().getColor(DashboardDataHelper.seriesColors[i]);
            seriesColors[i] = color;
        }

        final List<ChartTypeModel> chartTypesSource = new ArrayList<>();
        chartTypesSource.add(ChartTypeModelFactory.newHorizontallyStackedColumns(sciChartBuilder));
        chartTypesSource.add(ChartTypeModelFactory.newVerticallyStackedColumns(sciChartBuilder, false));
        chartTypesSource.add(ChartTypeModelFactory.newVerticallyStackedColumns(sciChartBuilder, true));
        chartTypesSource.add(ChartTypeModelFactory.newVerticallyStackedMountains(sciChartBuilder, false));
        chartTypesSource.add(ChartTypeModelFactory.newVerticallyStackedMountains(sciChartBuilder, true));

        //this line fixes swiping lag of the viewPager by caching the pages
        final ViewPager viewPager = binding.viewpager;
        viewPager.setOffscreenPageLimit(5);
        viewPager.setAdapter(new ViewPagerAdapter(getContext(), chartTypesSource));

        binding.tabLayout.setupWithViewPager(viewPager);
    }

    class ViewPagerAdapter extends PagerAdapter {
        private final Context context;
        private final List<ChartTypeModel> chartTypesSource;

        public ViewPagerAdapter(Context context, List<ChartTypeModel> chartTypesSource) {
            this.context = context;
            this.chartTypesSource = chartTypesSource;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup collection, int position) {
            final LayoutInflater inflater = LayoutInflater.from(context);
            final ExampleSingleChartFragmentBinding binding = ExampleSingleChartFragmentBinding.inflate(inflater, collection, false);

            binding.surface.setTheme(R.style.SciChart_NavyBlue);

            ChartTypeModel chartTypeModel = chartTypesSource.get(position);
            updateSurface(chartTypeModel, binding);

            final LinearLayout root = binding.getRoot();
            collection.addView(root);

            return root;
        }

        private void updateSurface(ChartTypeModel chartTypeModel, ExampleSingleChartFragmentBinding binding) {
            final SciChartSurface surface = binding.surface;

            UpdateSuspender.using(surface, () -> {
                Collections.addAll(surface.getXAxes(), sciChartBuilder.newNumericAxis().build());
                Collections.addAll(surface.getYAxes(), sciChartBuilder.newNumericAxis().build());
                Collections.addAll(surface.getRenderableSeries(), chartTypeModel.getSeriesCollection());
            });
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, @NonNull Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return chartTypesSource.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
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

    private static class ChartTypeModel {
        private final StackedSeriesCollectionBase<?> seriesCollection;
        private final String typeName;

        public ChartTypeModel(StackedSeriesCollectionBase<?> seriesCollection, String header) {
            this.seriesCollection = seriesCollection;
            this.typeName = header;
        }

        public StackedSeriesCollectionBase<?> getSeriesCollection() {
            return seriesCollection;
        }
        public String getTypeName() {
            return typeName;
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