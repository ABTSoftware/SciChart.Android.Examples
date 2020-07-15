//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2020. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ShowcaseActivity.java is part of the SCICHART® Examples. Permission is hereby granted
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

import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.examples.components.FpsDrawable;
import com.scichart.examples.utils.widgetgeneration.Widget;
import com.scichart.extensions.builders.SciChartBuilder;
import com.scichart.extensions3d.builders.SciChart3DBuilder;

import java.util.Collections;
import java.util.List;

public class ShowcaseActivity extends ExampleActivityBase{

    private SciChartSurface3D surface3D;
    private SciChartSurface surface;

    private FpsDrawable fpsDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SciChartBuilder.init(this);
        SciChart3DBuilder.init(this);

        super.onCreate(savedInstanceState);

        fpsDrawable = new FpsDrawable(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SciChartBuilder.dispose();
        SciChart3DBuilder.dispose();

        fpsDrawable.setTargets(null, null);
        fpsDrawable = null;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        surface = findViewById(R.id.chart);
        surface3D = findViewById(R.id.chart3d);
        showFpsCounter = fpsDrawable.hasTargets();
    }

    @Override
    protected List<Widget> getDefaultModifiers() {
        return Collections.emptyList();
    }

    @Override
    protected void onShowFpsCounterChanged(boolean showFpsCounter) {
        if (surface3D != null)
            surface3D.setIsFpsCounterVisible(showFpsCounter);

        if (showFpsCounter) {
            if (surface != null) {
                fpsDrawable.setTargets(surface, (View) surface.getRenderableSeriesArea());
                fpsDrawable.setVisible(true, true);
            }
        } else {
            fpsDrawable.setTargets(null, null);
            fpsDrawable.setVisible(false, false);
        }
    }
}
