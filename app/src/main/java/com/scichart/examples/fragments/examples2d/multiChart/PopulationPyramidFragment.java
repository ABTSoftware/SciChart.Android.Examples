//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2022. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// PopulationPyramidFragment.java is part of SCICHART®, High Performance Scientific Charts
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

import android.os.Handler;

import androidx.annotation.NonNull;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.modifiers.RolloverModifier;
import com.scichart.charting.modifiers.ZoomExtentsModifier;
import com.scichart.charting.numerics.labelProviders.NumericLabelProvider;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.annotations.TextAnnotation;
import com.scichart.charting.visuals.axes.AxisAlignment;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.renderableSeries.HorizontallyStackedColumnsCollection;
import com.scichart.charting.visuals.renderableSeries.StackedColumnRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.VerticallyStackedColumnsCollection;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.drawing.common.FontStyle;
import com.scichart.examples.fragments.base.ExampleSingleChartBaseFragment;
import com.scichart.examples.utils.PopData;
import com.scichart.examples.utils.PopulationPyramidUtil;
import com.scichart.examples.utils.populationpyramid.PyramidLabelProvider;
import com.scichart.examples.utils.populationpyramid.SharedPaletteProvider;

import java.util.Collections;
import java.util.List;

public class PopulationPyramidFragment extends ExampleSingleChartBaseFragment {

    List<PopData> maleDataList;
    List<PopData> femaleDataList;

    IXyDataSeries<Double, Double> maleDataSeriesXy;
    IXyDataSeries<Double, Double> femaleDataSeriesXy;

    SharedPaletteProvider sharedPaletteProvider = new SharedPaletteProvider(7.0, 16.0);

    TextAnnotation textAnnotation;
    int mainIndex = 0;
    Handler handler;
    Runnable runnable;

    @Override
    protected void initExample(@NonNull SciChartSurface surface) {
        maleDataList = PopulationPyramidUtil.Companion.readCsv("data/male_population_data_formated.csv", true, getActivity());
        femaleDataList = PopulationPyramidUtil.Companion.readCsv("data/female_population_data_formated.csv", false, getActivity());

        List<Double> maleData = maleDataList.get(mainIndex).getPopulation();
        Collections.reverse(maleData);
        List<Double> femaleData = femaleDataList.get(mainIndex).getPopulation();
        Collections.reverse(femaleData);

        textAnnotation = new TextAnnotation(getActivity());
        textAnnotation.setText(maleDataList.get(mainIndex).getYear());
        textAnnotation.setFontStyle(new FontStyle(150f, -0x440363d7));
        textAnnotation.setX1(0);
        textAnnotation.setY1(-500);

        Double data = 0.0;
        maleDataSeriesXy = new XyDataSeries(Double.class, Double.class);
        for(int i = 0; i<maleData.size();i++){
            maleDataSeriesXy.append(i+data, maleData.get(i));
        }
        femaleDataSeriesXy = new XyDataSeries(Double.class, Double.class);
        for(int i = 0; i<maleData.size();i++){
            femaleDataSeriesXy.append(i+data, femaleData.get(i));
        }

        final StackedColumnRenderableSeries maleSeries = sciChartBuilder.newStackedColumn().withDataSeries(maleDataSeriesXy).withPaletteProvider(sharedPaletteProvider).build();
        final StackedColumnRenderableSeries femaleSeries = sciChartBuilder.newStackedColumn().withDataSeries(femaleDataSeriesXy).withPaletteProvider(sharedPaletteProvider).build();

        final VerticallyStackedColumnsCollection verticalCollection1 = new VerticallyStackedColumnsCollection();
        verticalCollection1.add(maleSeries);
        verticalCollection1.add(femaleSeries);

        final HorizontallyStackedColumnsCollection horizontalCollection = new HorizontallyStackedColumnsCollection();
        horizontalCollection.add(verticalCollection1);
        horizontalCollection.setSpacing(0.0);

        final IAxis xAxis = new NumericAxis(requireActivity());
        xAxis.setAxisAlignment(AxisAlignment.Left);
        xAxis.setLabelProvider(new NumericLabelProvider(new PyramidLabelProvider()));

        final IAxis yAxis = new NumericAxis(requireActivity());
        yAxis.setAxisAlignment(AxisAlignment.Bottom);

        textAnnotation = new TextAnnotation(requireActivity());
        textAnnotation.setText(maleDataList.get(mainIndex).getYear());
        textAnnotation.setFontStyle(new FontStyle(150f, -0x440363d7));
        textAnnotation.setX1(0);
        textAnnotation.setY1(-500);

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xAxis);
            Collections.addAll(surface.getYAxes(), yAxis);

            Collections.addAll(surface.getAnnotations(), textAnnotation);

            Collections.addAll(surface.getRenderableSeries(), horizontalCollection);
            Collections.addAll(surface.getChartModifiers(), new RolloverModifier());
            Collections.addAll(surface.getChartModifiers(), new ZoomExtentsModifier());
        });

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                refreshData();
                handler.postDelayed(runnable, 1500);
            }
        };

        handler.postDelayed(runnable, 1500);
    }

    private void refreshData(){
        if(maleDataList.size() > mainIndex){
            mainIndex++;
        } else {
            mainIndex = 0;
        }

        getActivity().runOnUiThread(()->{
            UpdateSuspender.using(binding.surface, ()->{
                textAnnotation.setText(maleDataList.get(mainIndex).getYear());

                List<Double> maleData = maleDataList.get(mainIndex).getPopulation();
                Collections.reverse(maleData);
                List<Double> femaleData = femaleDataList.get(mainIndex).getPopulation();
                Collections.reverse(femaleData);

                for(int i = 0; i<maleData.size();i++){
                    maleDataSeriesXy.updateYAt(i, maleData.get(i));
                }
                for(int i = 0; i<maleData.size();i++){
                    femaleDataSeriesXy.updateYAt(i, femaleData.get(i));
                }
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnable);
    }
}
