//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CreateRealtimeGeoid3DChartFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples3d.realtime3DCharts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting3d.common.math.Vector3;
import com.scichart.charting3d.model.dataSeries.freeSurface.EllipsoidDataSeries3D;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.axes.NumericAxis3D;
import com.scichart.charting3d.visuals.renderableSeries.data.DrawMeshAs;
import com.scichart.charting3d.visuals.renderableSeries.data.GradientColorPalette;
import com.scichart.charting3d.visuals.renderableSeries.freeSurface.FreeSurfaceRenderableSeries3D;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.model.DoubleValues;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.fragments.base.ExampleSingleChart3DBaseFragment;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.scichart.charting.visuals.axes.AutoRange.Never;
import static com.scichart.drawing.utility.ColorUtil.Blue;
import static com.scichart.drawing.utility.ColorUtil.Cyan;
import static com.scichart.drawing.utility.ColorUtil.DarkRed;
import static com.scichart.drawing.utility.ColorUtil.GreenYellow;
import static com.scichart.drawing.utility.ColorUtil.Red;
import static com.scichart.drawing.utility.ColorUtil.Yellow;

import androidx.annotation.NonNull;

public class RealtimeGeoid3DChartFragment extends ExampleSingleChart3DBaseFragment {
    private static final int SIZE = 100;
    private static final double HEIGHT_OFFSET_SCALE = 0.5;

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> schedule;

    private int frames = 0;
    private final DoubleValues buffer = new DoubleValues();

    @Override
    protected void initExample(@NonNull SciChartSurface3D surface3d) {
        final NumericAxis3D xAxis = sciChart3DBuilder.newNumericAxis3D().withVisibleRange(-8, 8).withAutoRangeMode(Never).build();
        final NumericAxis3D yAxis = sciChart3DBuilder.newNumericAxis3D().withVisibleRange(-8, 8).withAutoRangeMode(Never).build();
        final NumericAxis3D zAxis = sciChart3DBuilder.newNumericAxis3D().withVisibleRange(-8, 8).withAutoRangeMode(Never).build();

        final DoubleValues globeHeightMap = getGlobeHeightMap(requireContext());

        final EllipsoidDataSeries3D<Double> ds = new EllipsoidDataSeries3D<>(Double.class, SIZE, SIZE);
        ds.setA(6d);
        ds.setB(6d);
        ds.setC(6d);

        ds.copyFrom(globeHeightMap);

        final FreeSurfaceRenderableSeries3D rs = sciChart3DBuilder.newFreeSurfaceSeries3D()
                .withDataSeries(ds)
                .withDrawMeshAs(DrawMeshAs.SolidMesh)
                .withStroke(0x77228b22)
                .withContourStroke(0x77228b22)
                .withStrokeThicknes(1f)
                .withMeshColorPalette(new GradientColorPalette(
                        new int[]{0xFF1D2C6B, Blue, Cyan, GreenYellow, Yellow, Red, DarkRed},
                        new float[]{0, 0.005f, 0.0075f, 0.01f, 0.5f, 0.7f, 1f})
                )
                .withPaletteMinimum(new Vector3(0, 6, 0))
                .withPaletteMaximum(new Vector3(0, 7, 0))
                .build();

        UpdateSuspender.using(surface3d, () -> {
            surface3d.setXAxis(xAxis);
            surface3d.setYAxis(yAxis);
            surface3d.setZAxis(zAxis);
            surface3d.getRenderableSeries().add(rs);
            surface3d.getChartModifiers().add(sciChart3DBuilder.newModifierGroupWithDefaultModifiers().build());

            surface3d.getWorldDimensions().assign(200, 200, 200);
        });

        schedule = scheduledExecutorService.scheduleWithFixedDelay(() -> UpdateSuspender.using(surface3d, () -> {
            final double freq = (Math.sin(frames++ * 0.1) + 1d) / 2d;
            final double exp = freq * 10;

            final int offset = frames % SIZE;
            final int size = globeHeightMap.size();

            buffer.setSize(size);

            final double[] heightMapItems = globeHeightMap.getItemsArray();
            final double[] bufferItems = buffer.getItemsArray();
            for (int i = 0; i < size; i++) {
                int currentValueIndex = i + offset;
                if(currentValueIndex >= size) {
                    currentValueIndex -= SIZE;
                }

                final double currentValue = heightMapItems[currentValueIndex];
                bufferItems[i] = currentValue + Math.pow(currentValue, exp) * HEIGHT_OFFSET_SCALE;
            }

            ds.copyFrom(buffer);
        }), 0, 33, TimeUnit.MILLISECONDS);
    }

    private static DoubleValues getGlobeHeightMap(Context context) {
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.example_globe_heightmap);
        final int stepU = bitmap.getWidth() / SIZE;
        final int stepV = bitmap.getHeight() / SIZE;

        final DoubleValues globeHeightMap = new DoubleValues();
        globeHeightMap.setSize(SIZE * SIZE);

        final double[] heightMapItems = globeHeightMap.getItemsArray();
        for (int v = 0; v < SIZE; v++) {
            for (int u = 0; u < SIZE; u++) {
                final int index = v * SIZE + u;
                final int x = u * stepU;
                final int y = v * stepV;

                heightMapItems[index] = ColorUtil.red(bitmap.getPixel(x, y)) / 255d;
            }
        }

        bitmap.recycle();

        return globeHeightMap;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (schedule != null) {
            schedule.cancel(true);
        }
    }
}
