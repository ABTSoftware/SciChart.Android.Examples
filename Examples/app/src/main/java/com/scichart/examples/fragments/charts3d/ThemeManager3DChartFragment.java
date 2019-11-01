//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2019. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ThemeManager3DChartFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.charts3d;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.scichart.charting.themes.ThemeManager;
import com.scichart.charting3d.model.dataSeries.xyz.XyzDataSeries3D;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.axes.NumericAxis3D;
import com.scichart.charting3d.visuals.camera.Camera3D;
import com.scichart.charting3d.visuals.pointMarkers.SpherePointMarker3D;
import com.scichart.charting3d.visuals.renderableSeries.metadataProviders.PointMetadataProvider3D;
import com.scichart.charting3d.visuals.renderableSeries.scatter.ScatterRenderableSeries3D;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.components.SpinnerStringAdapter;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.examples.utils.ItemSelectedListenerBase;

import java.util.List;

import butterknife.BindView;

public class ThemeManager3DChartFragment extends ExampleBaseFragment {
    private final static int BLACK_STEEL = 0;
    private final static int BRIGHT_SPARK = 1;
    private final static int CHROME = 2;
    private final static int ELECTRIC = 3;
    private final static int EXPRESSION_DARK = 4;
    private final static int EXPRESSION_LIGHT = 5;
    private final static int OSCILLOSCOPE = 6;
    private final static int SCI_CHART_V4_DARK = 7;
    private final static int BERRY_BLUE = 8;

    @BindView(R.id.chart3d)
    SciChartSurface3D surface3d;

    @BindView(R.id.themeSelector)
    Spinner themeSelector;

    @Override
    protected int getLayoutId() {
        return R.layout.example_theme_manager_3d_chart_fragment;
    }

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

        final DataManager dataManager = DataManager.getInstance();

        final Camera3D camera = sciChart3DBuilder.newCamera3D().build();

        final NumericAxis3D xAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(.1, .1).build();
        final NumericAxis3D yAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(.1, .1).build();
        final NumericAxis3D zAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(.1, .1).build();

        final XyzDataSeries3D<Double, Double, Double> xyzDataSeries3D = new XyzDataSeries3D<>(Double.class, Double.class, Double.class);
        final PointMetadataProvider3D metadataProvider = new PointMetadataProvider3D();

        final List<PointMetadataProvider3D.PointMetadata3D> medatata = metadataProvider.metadata;
        for (int i = 0; i < 1250; i++) {
            final double x = dataManager.getGaussianRandomNumber(5, 1.5);
            final double y = dataManager.getGaussianRandomNumber(5, 1.5);
            final double z = dataManager.getGaussianRandomNumber(5, 1.5);

            xyzDataSeries3D.append(x, y, z);

            final int color = dataManager.getRandomColor();
            final float scale = dataManager.getRandomScale();
            medatata.add(new PointMetadataProvider3D.PointMetadata3D(color, scale));
        }

        final SpherePointMarker3D pointMarker = sciChart3DBuilder.newSpherePointMarker3D()
                .withFill(ColorUtil.LimeGreen)
                .withSize(2f)
                .build();

        final ScatterRenderableSeries3D rs = sciChart3DBuilder.newScatterSeries3D()
                .withDataSeries(xyzDataSeries3D)
                .withPointMarker(pointMarker)
                .withMetadataProvider(metadataProvider)
                .build();

        UpdateSuspender.using(surface3d, new Runnable() {
            @Override
            public void run() {
                surface3d.setCamera(camera);

                surface3d.setXAxis(xAxis);
                surface3d.setYAxis(yAxis);
                surface3d.setZAxis(zAxis);

                surface3d.getRenderableSeries().add(rs);

                surface3d.getChartModifiers().add(sciChart3DBuilder.newModifierGroupWithDefaultModifiers().build());
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

        surface3d.setTheme(themeId);
    }
}
