//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2019. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CreateRealtimeGeoid3DChartFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.charts3d;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting3d.common.math.Vector3;
import com.scichart.charting3d.model.dataSeries.freeSurface.EllipsoidDataSeries3D;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.axes.NumericAxis3D;
import com.scichart.charting3d.visuals.camera.Camera3D;
import com.scichart.charting3d.visuals.renderableSeries.data.DrawMeshAs;
import com.scichart.charting3d.visuals.renderableSeries.data.GradientColorPalette;
import com.scichart.charting3d.visuals.renderableSeries.freeSurface.FreeSurfaceRenderableSeries3D;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.core.model.DoubleValues;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

import static com.scichart.drawing.utility.ColorUtil.Blue;
import static com.scichart.drawing.utility.ColorUtil.Cyan;
import static com.scichart.drawing.utility.ColorUtil.DarkRed;
import static com.scichart.drawing.utility.ColorUtil.GreenYellow;
import static com.scichart.drawing.utility.ColorUtil.Red;
import static com.scichart.drawing.utility.ColorUtil.Yellow;

public class CreateRealtimeGeoid3DChartFragment extends ExampleBaseFragment {
    private static int SIZE = 100;
    private static double HEIGHT_OFFSET_SCALE = 0.5;

    @BindView(R.id.chart3d)
    SciChartSurface3D surface3d;

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> schedule;

    private int frames = 0;
    private final DoubleValues buffer = new DoubleValues();

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart3d_fragment;
    }

    @Override
    protected void initExample() {
        final Camera3D camera = sciChart3DBuilder.newCamera3D().build();

        final NumericAxis3D xAxis = sciChart3DBuilder.newNumericAxis3D().withVisibleRange(-8, 8).withAutoRangeMode(AutoRange.Never).build();
        final NumericAxis3D yAxis = sciChart3DBuilder.newNumericAxis3D().withVisibleRange(-8, 8).withAutoRangeMode(AutoRange.Never).build();
        final NumericAxis3D zAxis = sciChart3DBuilder.newNumericAxis3D().withVisibleRange(-8, 8).withAutoRangeMode(AutoRange.Never).build();

        final DoubleValues globeHeightMap = getGlobeHeightMap(getActivity());

        final EllipsoidDataSeries3D<Double> ds = new EllipsoidDataSeries3D<>(Double.class, SIZE, SIZE);
        ds.setA(6d);
        ds.setB(6d);
        ds.setC(6d);

        ds.copyFrom(globeHeightMap);

        final int[] colors = new int[] {0xFF1D2C6B, Blue, Cyan, GreenYellow, Yellow, Red, DarkRed};
        final float[] stops = new float[] {0, 0.005f, 0.0075f, 0.01f, 0.5f, 0.7f, 1f};

        final FreeSurfaceRenderableSeries3D rs = sciChart3DBuilder.newFreeSurfaceSeries3D()
                .withDataSeries(ds)
                .withDrawMeshAs(DrawMeshAs.SolidMesh)
                .withStroke(0x77228b22)
                .withContourStroke(0x77228b22)
                .withStrokeThicknes(1f)
                .withMeshColorPalette(new GradientColorPalette(colors, stops))
                .withPaletteMinimum(new Vector3(0, 6, 0))
                .withPaletteMaximum(new Vector3(0, 7, 0))
                .build();

        UpdateSuspender.using(surface3d, new Runnable() {
            @Override
            public void run() {
                surface3d.getWorldDimensions().assign(200, 200, 200);

                surface3d.setCamera(camera);

                surface3d.setXAxis(xAxis);
                surface3d.setYAxis(yAxis);
                surface3d.setZAxis(zAxis);

                surface3d.getRenderableSeries().add(rs);

                surface3d.getChartModifiers().add(sciChart3DBuilder.newModifierGroupWithDefaultModifiers().build());
            }
        });

        schedule = scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                UpdateSuspender.using(surface3d, new Runnable() {
                    @Override
                    public void run() {
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
                    }
                });
            }
        }, 0, 33, TimeUnit.MILLISECONDS);
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

        if (schedule != null)
            schedule.cancel(true);
    }
}
