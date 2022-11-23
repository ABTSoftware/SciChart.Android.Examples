//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SeriesCustomTooltips3DChartFragment.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples3d.tooltipsAndHitTest3DCharts;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.scichart.charting3d.model.dataSeries.xyz.XyzDataSeries3D;
import com.scichart.charting3d.modifiers.CrosshairMode;
import com.scichart.charting3d.modifiers.TooltipModifier3D;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.axes.NumericAxis3D;
import com.scichart.charting3d.visuals.pointMarkers.SpherePointMarker3D;
import com.scichart.charting3d.visuals.renderableSeries.XyzRenderableSeries3DBase;
import com.scichart.charting3d.visuals.renderableSeries.hitTest.DefaultXyzSeriesInfo3DProvider;
import com.scichart.charting3d.visuals.renderableSeries.hitTest.XyzSeriesInfo3D;
import com.scichart.charting3d.visuals.renderableSeries.scatter.ScatterRenderableSeries3D;
import com.scichart.charting3d.visuals.renderableSeries.tooltips.ISeriesTooltip3D;
import com.scichart.charting3d.visuals.renderableSeries.tooltips.XyzSeriesTooltip3D;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.data.DataManager;
import com.scichart.examples.databinding.ExampleSingleChart3dWithModifierTipFragmentBinding;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import static com.scichart.core.utility.StringUtil.NEW_LINE;

public class SeriesCustomTooltips3DChartFragment extends ExampleBaseFragment<ExampleSingleChart3dWithModifierTipFragmentBinding> {

    @NonNull
    @Override
    protected ExampleSingleChart3dWithModifierTipFragmentBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ExampleSingleChart3dWithModifierTipFragmentBinding.inflate(inflater);
    }

    @Override
    protected void initExample(@NonNull ExampleSingleChart3dWithModifierTipFragmentBinding binding) {
        final NumericAxis3D xAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(.2, .2).withVisibleRange(-1.1, 1.1).build();
        final NumericAxis3D yAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(.2, .2).withVisibleRange(-1.1, 1.1).build();
        final NumericAxis3D zAxis = sciChart3DBuilder.newNumericAxis3D().withGrowBy(.2, .2).withVisibleRange(-1.1, 1.1).build();

        final DataManager dataManager = DataManager.getInstance();
        final XyzDataSeries3D<Double, Double, Double> dataSeries = new XyzDataSeries3D<>(Double.class, Double.class, Double.class);
        for (int i = 0; i < 500; i++) {
            final double m1 = dataManager.getRandomBoolean() ? -1 : 1;
            final double m2 = dataManager.getRandomBoolean() ? -1 : 1;

            final double x1 = dataManager.getRandomDouble() * m1;
            final double x2 = dataManager.getRandomDouble() * m2;
            final double temp = 1 - x1 * x1 - x2 * x2;

            final double x = 2 * x1 * Math.sqrt(temp);
            final double y = 2 * x2 * Math.sqrt(temp);
            final double z = 1 - 2 * (x1 * x1 + x2 * x2);

            dataSeries.append(x, y, z);
        }

        final SpherePointMarker3D pointMarker3D = sciChart3DBuilder.newSpherePointMarker3D()
                .withFill(0xFF47bde6)
                .withSize(7f)
                .build();

        final ScatterRenderableSeries3D rs = sciChart3DBuilder.newScatterSeries3D()
                .withDataSeries(dataSeries)
                .withPointMarker(pointMarker3D)
                .withSeriesInfoProvider(new CustomSeriesInfo3DProvider())
                .build();

        final SciChartSurface3D surface3d = binding.surface3d;
        UpdateSuspender.using(surface3d, () -> {
            surface3d.setXAxis(xAxis);
            surface3d.setYAxis(yAxis);
            surface3d.setZAxis(zAxis);
            surface3d.getRenderableSeries().add(rs);

            surface3d.getChartModifiers().add(sciChart3DBuilder.newModifierGroup()
                    .withPinchZoomModifier3D().build()
                    .withOrbitModifier3D().withReceiveHandledEvents(true).withExecuteOnPointerCount(2).build()
                    .withZoomExtentsModifier3D().build()
                    .withTooltipModifier()
                        .withReceiveHandledEvents(true)
                        .withCrosshairMode(CrosshairMode.Lines)
                        .withCrosshairPlanesFill(0x3347bde6)
                        .withExecuteOnPointerCount(1)
                        .build()
                    .build());
        });
    }

    private static class CustomSeriesInfo3DProvider extends DefaultXyzSeriesInfo3DProvider {
        @Override
        protected ISeriesTooltip3D getSeriesTooltipInternal(Context context, XyzSeriesInfo3D<? extends XyzRenderableSeries3DBase> seriesInfo, Class<?> modifierType) {
            if (modifierType == TooltipModifier3D.class) {
                return new CustomXyzSeriesTooltip3D(context, seriesInfo);
            } else {
                return super.getSeriesTooltipInternal(context, seriesInfo, modifierType);
            }
        }

        private static class CustomXyzSeriesTooltip3D extends XyzSeriesTooltip3D {
            public CustomXyzSeriesTooltip3D(Context context, XyzSeriesInfo3D<?> seriesInfo) {
                super(context, seriesInfo);
            }

            @Override
            protected void internalUpdate(XyzSeriesInfo3D<?> seriesInfo) {
                final SpannableStringBuilder sb = new SpannableStringBuilder();

                sb.append("This is Custom Tooltip").append(NEW_LINE);
                sb.append("VertexId: ").append(Integer.toString(seriesInfo.vertexId)).append(NEW_LINE);

                sb.append("X: ").append(seriesInfo.getFormattedXValue()).append(NEW_LINE);
                sb.append("Y: ").append(seriesInfo.getFormattedYValue()).append(NEW_LINE);
                sb.append("Z: ").append(seriesInfo.getFormattedZValue());

                setText(sb);
                setSeriesColor(seriesInfo.seriesColor);
            }
        }
    }
}
