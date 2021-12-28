//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// DashboardStyleChartsFragment.kt is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.multiChart.kt

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager.widget.PagerAdapter
import com.scichart.charting.visuals.renderableSeries.HorizontallyStackedColumnsCollection
import com.scichart.charting.visuals.renderableSeries.StackedSeriesCollectionBase
import com.scichart.charting.visuals.renderableSeries.VerticallyStackedColumnsCollection
import com.scichart.charting.visuals.renderableSeries.VerticallyStackedMountainsCollection
import com.scichart.examples.R
import com.scichart.examples.databinding.ExampleDashboardStyleChartFragmentBinding
import com.scichart.examples.databinding.ExampleSingleChartFragmentBinding
import com.scichart.examples.fragments.base.ExampleBaseFragment
import com.scichart.examples.utils.scichartExtensions.*

class DashboardStyleChartsFragment : ExampleBaseFragment<ExampleDashboardStyleChartFragmentBinding>() {

    override fun inflateBinding(inflater: LayoutInflater): ExampleDashboardStyleChartFragmentBinding {
        return ExampleDashboardStyleChartFragmentBinding.inflate(inflater)
    }

    override fun initExample(binding: ExampleDashboardStyleChartFragmentBinding) {
        val seriesColors = colors.map { ResourcesCompat.getColor(resources, it, null) }

        val chartTypesSource = listOf(
            ChartTypeModel(HorizontallyStackedColumnsCollection().apply {
                for (i in 0 until 5) {
                    stackedColumnRenderableSeries {
                        xyDataSeries<Double, Double>("Series ${i + 1}") { append(xData, yData[i]) }
                        fillBrushStyle = LinearGradientBrushStyle(seriesColors[i * 2 + 1], seriesColors[i * 2])
                        strokeStyle = SolidPenStyle(seriesColors[i * 2])
                    }
                }
            }, "Stacked columns side-by-side"),
            ChartTypeModel(VerticallyStackedColumnsCollection().apply {
                for (i in 0 until 5) {
                    stackedColumnRenderableSeries {
                        xyDataSeries<Double, Double>("Series ${i + 1}") { append(xData, yData[i]) }
                        fillBrushStyle = LinearGradientBrushStyle(seriesColors[i * 2 + 1], seriesColors[i * 2])
                        strokeStyle = SolidPenStyle(seriesColors[i * 2])
                    }
                }
            }, "Stacked columns"),
            ChartTypeModel(VerticallyStackedColumnsCollection().apply {
                isOneHundredPercent = true
                for (i in 0 until 5) {
                    stackedColumnRenderableSeries {
                        xyDataSeries<Double, Double>("Series ${i + 1}") { append(xData, yData[i]) }
                        fillBrushStyle = LinearGradientBrushStyle(seriesColors[i * 2 + 1], seriesColors[i * 2])
                        strokeStyle = SolidPenStyle(seriesColors[i * 2])
                    }
                }
            }, "100% Stacked columns"),
            ChartTypeModel(VerticallyStackedMountainsCollection().apply {
                for (i in 0 until 5) {
                    stackedMountainRenderableSeries {
                        xyDataSeries<Double, Double>("Series ${i + 1}") { append(xData, yData[i]) }
                        areaStyle = LinearGradientBrushStyle(seriesColors[i * 2 + 1], seriesColors[i * 2])
                        strokeStyle = SolidPenStyle(seriesColors[i * 2])
                    }
                }
            }, "Stacked mountains"),
            ChartTypeModel(VerticallyStackedMountainsCollection().apply {
                isOneHundredPercent = true
                for (i in 0 until 5) {
                    stackedMountainRenderableSeries {
                        xyDataSeries<Double, Double>("Series ${i + 1}") { append(xData, yData[i]) }
                        areaStyle = LinearGradientBrushStyle(seriesColors[i * 2 + 1], seriesColors[i * 2])
                        strokeStyle = SolidPenStyle(seriesColors[i * 2])
                    }
                }
            }, "100% Stacked mountains"),
        )

        binding.tabLayout.setupWithViewPager(binding.viewpager.apply {
            offscreenPageLimit = 5
            adapter = ViewPagerAdapter(context, chartTypesSource)
        })
    }

    internal class ViewPagerAdapter(private val context: Context, private val chartTypesSource: List<ChartTypeModel>) : PagerAdapter() {

        override fun instantiateItem(collection: ViewGroup, position: Int): Any {
            val inflater = LayoutInflater.from(context)
            val binding: ExampleSingleChartFragmentBinding = ExampleSingleChartFragmentBinding.inflate(inflater, collection, false)

            val chartTypeModel = chartTypesSource[position]
            updateSurface(chartTypeModel, binding)

            val root: LinearLayout = binding.root
            collection.addView(root)

            return root
        }

        private fun updateSurface(chartTypeModel: ChartTypeModel, binding: ExampleSingleChartFragmentBinding) {
            binding.surface.suspendUpdates {
                xAxes { numericAxis() }
                yAxes { numericAxis() }
                renderableSeries {
                    rSeries(chartTypeModel.seriesCollection)
                }
            }
        }

        override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
            collection.removeView(view as View)
        }

        override fun getCount(): Int = chartTypesSource.size

        override fun isViewFromObject(view: View, `object`: Any): Boolean = view === `object`

        override fun getPageTitle(position: Int): CharSequence = chartTypesSource[position].typeName
    }

    internal class ChartTypeModel(val seriesCollection: StackedSeriesCollectionBase<*>, val typeName: String)

    companion object {
        private val xData = listOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0)
        private val yData = arrayOf(
            listOf(10.0, 13.0, 7.0, 16.0, 4.0, 6.0, 20.0, 14.0, 16.0, 10.0, 24.0, 11.0),
            listOf(12.0, 17.0, 21.0, 15.0, 19.0, 18.0, 13.0, 21.0, 22.0, 20.0, 5.0, 10.0),
            listOf(7.0, 30.0, 27.0, 24.0, 21.0, 15.0, 17.0, 26.0, 22.0, 28.0, 21.0, 22.0),
            listOf(16.0, 10.0, 9.0, 8.0, 22.0, 14.0, 12.0, 27.0, 25.0, 23.0, 17.0, 17.0),
            listOf(7.0, 24.0, 21.0, 11.0, 19.0, 17.0, 14.0, 27.0, 26.0, 22.0, 28.0, 16.0)
        )
        private val colors = intArrayOf(
            R.color.dashboard_chart_blue_series_0, R.color.dashboard_chart_blue_series_1,
            R.color.dashboard_chart_orange_series_0, R.color.dashboard_chart_orange_series_1,
            R.color.dashboard_chart_red_series_0, R.color.dashboard_chart_red_series_1,
            R.color.dashboard_chart_green_series_0, R.color.dashboard_chart_green_series_1,
            R.color.dashboard_chart_violet_series_0, R.color.dashboard_chart_violet_series_1
        )
    }
}