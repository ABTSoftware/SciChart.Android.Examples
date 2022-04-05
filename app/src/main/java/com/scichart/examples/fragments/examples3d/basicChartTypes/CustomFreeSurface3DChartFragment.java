//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CreateCustomFreeSurface3DChartFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples3d.basicChartTypes;

import static com.scichart.charting3d.visuals.axes.AxisSideClipping.None;
import static com.scichart.charting3d.visuals.renderableSeries.freeSurface.FreeSurfacePaletteMinMaxMode.Absolute;
import static com.scichart.charting3d.visuals.renderableSeries.freeSurface.FreeSurfacePaletteMinMaxMode.Relative;
import static com.scichart.drawing.utility.ColorUtil.Blue;
import static com.scichart.drawing.utility.ColorUtil.Cyan;
import static com.scichart.drawing.utility.ColorUtil.DarkRed;
import static com.scichart.drawing.utility.ColorUtil.GreenYellow;
import static com.scichart.drawing.utility.ColorUtil.Red;
import static com.scichart.drawing.utility.ColorUtil.Yellow;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.scichart.charting3d.common.math.Vector3;
import com.scichart.charting3d.model.dataSeries.freeSurface.CustomSurfaceDataSeries3D;
import com.scichart.charting3d.model.dataSeries.freeSurface.CustomSurfaceDataSeries3D.UVFunc;
import com.scichart.charting3d.model.dataSeries.freeSurface.CustomSurfaceDataSeries3D.ValueFunc;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.axes.NumericAxis3D;
import com.scichart.charting3d.visuals.renderableSeries.data.DrawMeshAs;
import com.scichart.charting3d.visuals.renderableSeries.data.GradientColorPalette;
import com.scichart.charting3d.visuals.renderableSeries.freeSurface.FreeSurfaceRenderableSeries3D;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.R;
import com.scichart.examples.components.SpinnerStringAdapter;
import com.scichart.examples.databinding.ExampleCreateFreeSurface3dFragmentBinding;
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.examples.utils.ItemSelectedListenerBase;

public class CustomFreeSurface3DChartFragment extends ExampleBaseFragment<ExampleCreateFreeSurface3dFragmentBinding> {

    @NonNull
    @Override
    protected ExampleCreateFreeSurface3dFragmentBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ExampleCreateFreeSurface3dFragmentBinding.inflate(inflater);
    }

    @Override
    protected void initExample(ExampleCreateFreeSurface3dFragmentBinding binding) {
        final NumericAxis3D xAxis = sciChart3DBuilder.newNumericAxis3D().withNegativeSideClipping(None).withPositiveSideClipping(None).build();
        final NumericAxis3D yAxis = sciChart3DBuilder.newNumericAxis3D().build();
        final NumericAxis3D zAxis = sciChart3DBuilder.newNumericAxis3D().withNegativeSideClipping(None).withPositiveSideClipping(None).build();

        final UVFunc radialDistanceFunc = (u, v) -> 5d + Math.sin(5 * (u + v));
        final UVFunc azimuthalAngleFunc = (u, v) -> u;
        final UVFunc polarAngleFunc = (u, v) -> v;

        final ValueFunc<Double> xFunc = (r, theta, phi) -> r * Math.sin(theta) * Math.cos(phi);
        final ValueFunc<Double> yFunc = (r, theta, phi) -> r * Math.cos(theta);
        final ValueFunc<Double> zFunc = (r, theta, phi) -> r * Math.sin(theta) * Math.sin(phi);

        final CustomSurfaceDataSeries3D<Double, Double, Double> ds = new CustomSurfaceDataSeries3D<>(Double.class, Double.class, Double.class,
                30, 30,
                radialDistanceFunc, azimuthalAngleFunc, polarAngleFunc,
                xFunc, yFunc, zFunc,
                0d, Math.PI * 2, 0, Math.PI
        );

        final FreeSurfaceRenderableSeries3D rs = sciChart3DBuilder.newFreeSurfaceSeries3D()
                .withDataSeries(ds)
                .withDrawMeshAs(DrawMeshAs.SolidWireframe)
                .withStroke(0x77228B22)
                .withContourInterval(0.1f)
                .withContourStroke(0x77228B22)
                .withStrokeThicknes(1f)
                .withLightingFactor(0.8f)
                .withMeshColorPalette(new GradientColorPalette(
                        new int[]{0xFF1D2C6B, Blue, Cyan, GreenYellow, Yellow, Red, DarkRed},
                        new float[]{0, .1f, .3f, .5f, .7f, .9f, 1})
                )
                .build();

        final SciChartSurface3D surface3d = binding.surface3d;
        UpdateSuspender.using(surface3d, () -> {
            surface3d.setXAxis(xAxis);
            surface3d.setYAxis(yAxis);
            surface3d.setZAxis(zAxis);

            surface3d.getRenderableSeries().add(rs);

            surface3d.getChartModifiers().add(sciChart3DBuilder.newModifierGroupWithDefaultModifiers().build());
        });

        final Spinner paletteModeSelector = binding.paletteModeSelector;
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
                rs.setPaletteMinMaxMode(Relative);
                rs.setPaletteMinimum(new Vector3(0f, 5f, 0f));
                rs.setPaletteMaximum(new Vector3(0f, 7f, 0f));
                rs.setPaletteRadialFactor(1f);
                rs.setPaletteAxialFactor(new Vector3(0, 0, 0));
                rs.setPaletteAzimuthalFactor(0f);
                rs.setPalettePolarFactor(0f);
                break;
            // Axial
            case 1:
                rs.setPaletteMinMaxMode(Absolute);
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
