//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ExampleActivity.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************
package com.scichart.examples;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.DrawableRes;

import com.scichart.charting.model.ChartModifierCollection;
import com.scichart.charting.modifiers.ZoomExtentsModifier;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.examples.components.FpsDrawable;
import com.scichart.examples.components.modifiers.CustomRotateChartModifier;
import com.scichart.examples.components.modifiers.FlipAxesCoordsChartModifier;
import com.scichart.examples.utils.SideMenuHelper;
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget;
import com.scichart.examples.utils.widgetgeneration.Widget;
import com.scichart.extensions.builders.SciChartBuilder;

import java.util.ArrayList;
import java.util.List;

public class ExampleActivity extends ExampleActivityBase {
    private FpsDrawable fpsDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SciChartBuilder.init(this);

        super.onCreate(savedInstanceState);

        fpsDrawable = new FpsDrawable(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SciChartBuilder.dispose();

        fpsDrawable.setTargets(null, null);
        fpsDrawable = null;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        showFpsCounter = fpsDrawable.hasTargets();
    }

    @Override
    protected List<Widget> getDefaultModifiers() {
        List<Widget> widgets = new ArrayList<>();

        final SciChartSurface surface = getTargetChart();
        if (surface != null) {
            final ChartModifierCollection chartModifiers = surface.getChartModifiers();

            widgets.add(attachModifierAndCreateWidget(null, R.drawable.example_toolbar_zoom_extents, surface, v -> surface.animateZoomExtents(ZoomExtentsModifier.DEFAULT_ANIMATION_DURATION)));
            widgets.add(attachModifierAndCreateWidget(FlipAxesCoordsChartModifier.class, R.drawable.example_toolbar_flip_x, surface, v -> ((FlipAxesCoordsChartModifier) SideMenuHelper.getModifier(FlipAxesCoordsChartModifier.class, chartModifiers)).flipXAxes()));
            widgets.add(attachModifierAndCreateWidget(FlipAxesCoordsChartModifier.class, R.drawable.example_toolbar_flip_y, surface, v -> ((FlipAxesCoordsChartModifier) SideMenuHelper.getModifier(FlipAxesCoordsChartModifier.class, chartModifiers)).flipYAxes()));
            widgets.add(attachModifierAndCreateWidget(CustomRotateChartModifier.class, R.drawable.example_toolbar_rotate, surface, v -> ((CustomRotateChartModifier) SideMenuHelper.getModifier(CustomRotateChartModifier.class, chartModifiers)).rotateChart()));
        }
        return widgets;
    }

    private SciChartSurface getTargetChart() {
        return findViewById(R.id.surface);
    }

    private Widget attachModifierAndCreateWidget(Class<?> modifierType, @DrawableRes int drawableRes, SciChartSurface surface, View.OnClickListener listener) {
        if (modifierType != null) {
            SideMenuHelper.attachModifierToSurface(modifierType, surface);
        }

        return new ImageViewWidget.Builder().setId(drawableRes).setListener(listener).build();
    }

    @Override
    protected void onShowFpsCounterChanged(boolean showFpsCounter) {
        if (showFpsCounter) {
            final SciChartSurface surface = getTargetChart();
            if (surface != null) {
                fpsDrawable.setTargets(surface, (View) surface.getRenderableSeriesArea());
                fpsDrawable.setVisible(true, true);
            } else {
                Toast.makeText(ExampleActivity.this, "There isn't any SciChartSurface with +id/chart on the view.", Toast.LENGTH_LONG).show();
            }
        } else {
            fpsDrawable.setTargets(null, null);
            fpsDrawable.setVisible(false, false);
        }
    }
}