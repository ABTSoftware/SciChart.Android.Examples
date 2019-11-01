//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2019. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CreateCustomFreeSurface3DChartFragment.java is part of the SCICHART® Examples. Permission is hereby granted
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
import android.widget.TextView;

import com.scichart.charting3d.common.math.Vector3;
import com.scichart.charting3d.model.dataSeries.freeSurface.CustomSurfaceDataSeries3D;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.axes.AxisSideClipping;
import com.scichart.charting3d.visuals.axes.NumericAxis3D;
import com.scichart.charting3d.visuals.camera.Camera3D;
import com.scichart.charting3d.visuals.renderableSeries.data.DrawMeshAs;
import com.scichart.charting3d.visuals.renderableSeries.data.GradientColorPalette;
import com.scichart.charting3d.visuals.renderableSeries.freeSurface.FreeSurfacePaletteMinMaxMode;
import com.scichart.charting3d.visuals.renderableSeries.freeSurface.FreeSurfaceRenderableSeries3D;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.R;
import com.scichart.examples.components.SpinnerStringAdapter;
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.examples.utils.ItemSelectedListenerBase;

import butterknife.BindView;

import static com.scichart.drawing.utility.ColorUtil.Blue;
import static com.scichart.drawing.utility.ColorUtil.Cyan;
import static com.scichart.drawing.utility.ColorUtil.DarkRed;
import static com.scichart.drawing.utility.ColorUtil.GreenYellow;
import static com.scichart.drawing.utility.ColorUtil.Red;
import static com.scichart.drawing.utility.ColorUtil.Yellow;

public class CreateCustomFreeSurface3DChartFragment extends ExampleBaseFragment {
    @BindView(R.id.chart3d)
    SciChartSurface3D surface3d;

    @BindView(R.id.paletteModeSelector)
    Spinner paletteModeSelector;

    @Override
    protected int getLayoutId() {
        return R.layout.example_create_free_surface_3d_fragment;
    }

    @Override
    protected void initExample() {
        final Camera3D camera = sciChart3DBuilder.newCamera3D().build();

        final NumericAxis3D xAxis = sciChart3DBuilder.newNumericAxis3D().withNegativeSideClipping(AxisSideClipping.None).withPositiveSideClipping(AxisSideClipping.None).build();
        final NumericAxis3D yAxis = sciChart3DBuilder.newNumericAxis3D().build();
        final NumericAxis3D zAxis = sciChart3DBuilder.newNumericAxis3D().withNegativeSideClipping(AxisSideClipping.None).withPositiveSideClipping(AxisSideClipping.None).build();

        final CustomSurfaceDataSeries3D.UVFunc radialDistanceFunc = new CustomSurfaceDataSeries3D.UVFunc() {
            @Override
            public double getValueFor(double u, double v) {
                return 5d + Math.sin(5 * (u + v));
            }
        };

        final CustomSurfaceDataSeries3D.UVFunc azimuthalAngleFunc = new CustomSurfaceDataSeries3D.UVFunc() {

            @Override
            public double getValueFor(double u, double v) {
                return u;
            }
        };

        final CustomSurfaceDataSeries3D.UVFunc polarAngleFunc = new CustomSurfaceDataSeries3D.UVFunc() {

            @Override
            public double getValueFor(double u, double v) {
                return v;
            }
        };

        final CustomSurfaceDataSeries3D.ValueFunc<Double> xFunc = new CustomSurfaceDataSeries3D.ValueFunc<Double>() {

            @Override
            public Double getValueFor(double r, double theta, double phi) {
                return r * Math.sin(theta) * Math.cos(phi);
            }
        };

        final CustomSurfaceDataSeries3D.ValueFunc<Double> yFunc = new CustomSurfaceDataSeries3D.ValueFunc<Double>() {

            @Override
            public Double getValueFor(double r, double theta, double phi) {
                return r * Math.cos(theta);
            }
        };

        final CustomSurfaceDataSeries3D.ValueFunc<Double> zFunc = new CustomSurfaceDataSeries3D.ValueFunc<Double>() {

            @Override
            public Double getValueFor(double r, double theta, double phi) {
                return r * Math.sin(theta) * Math.sin(phi);
            }
        };

        final CustomSurfaceDataSeries3D<Double, Double, Double> ds = new CustomSurfaceDataSeries3D<>(Double.class, Double.class, Double.class, 30, 30,
                radialDistanceFunc, azimuthalAngleFunc, polarAngleFunc,
                xFunc, yFunc, zFunc,  0d, Math.PI * 2, 0, Math.PI);

        final int[] colors = {0xFF1D2C6B, Blue, Cyan, GreenYellow, Yellow, Red, DarkRed};
        final float[] stops = {0, .1f, .3f, .5f, .7f, .9f, 1};

        final FreeSurfaceRenderableSeries3D rs = sciChart3DBuilder.newFreeSurfaceSeries3D()
                .withDataSeries(ds)
                .withDrawMeshAs(DrawMeshAs.SolidWireframe)
                .withStroke(0x77228B22)
                .withContourInterval(0.1f)
                .withContourStroke(0x77228B22)
                .withStrokeThicknes(1f)
                .withLightingFactor(0.8f)
                .withMeshColorPalette(new GradientColorPalette(colors, stops))
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

        paletteModeSelector.setAdapter(new SpinnerStringAdapter(getActivity(), R.array.palette_mode_list));
        paletteModeSelector.setSelection(0);
        paletteModeSelector.setOnItemSelectedListener(new ItemSelectedListenerBase() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switchPaletteMode(rs, position);
            }
        });
    }

    private void switchPaletteMode(FreeSurfaceRenderableSeries3D rs, int paletteMode) {
        switch (paletteMode) {
            // Radial
            case 0:
                rs.setPaletteMinMaxMode(FreeSurfacePaletteMinMaxMode.Relative);
                rs.setPaletteMinimum(new Vector3(0f, 5f, 0f));
                rs.setPaletteMaximum(new Vector3(0f, 7f, 0f));
                rs.setPaletteRadialFactor(1f);
                rs.setPaletteAxialFactor(new Vector3(0, 0, 0));
                rs.setPaletteAzimuthalFactor(0f);
                rs.setPalettePolarFactor(0f);
                break;
            // Axial
            case 1:
                rs.setPaletteMinMaxMode(FreeSurfacePaletteMinMaxMode.Absolute);
                rs.setPaletteMinimum(new Vector3(0f, -2f, 0f));
                rs.setPaletteMaximum(new Vector3(0f, 2f, 0f));
                rs.setPaletteRadialFactor(0f);
                rs.setPaletteAxialFactor(new Vector3(0f, 1f, 0f));
                rs.setPaletteAzimuthalFactor(0f);
                rs.setPalettePolarFactor(0f);
                break;
            // Azimuthal
            case 2:
                rs.setPaletteRadialFactor(0f);
                rs.setPaletteAxialFactor(new Vector3(0f, 0f, 0f));
                rs.setPaletteAzimuthalFactor(1f);
                rs.setPalettePolarFactor(0f);
                break;
            // Polar
            case 3:
                rs.setPaletteRadialFactor(0f);
                rs.setPaletteAxialFactor(new Vector3(0f, 0f, 0f));
                rs.setPaletteAzimuthalFactor(0f);
                rs.setPalettePolarFactor(1f);
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }
}
