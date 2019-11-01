//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2019. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// Example3DActivity.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples;

import android.os.Bundle;

import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.extensions3d.builders.SciChart3DBuilder;

public class Example3DActivity extends ExampleActivityBase{
    private SciChartSurface3D surface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SciChart3DBuilder.init(this);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SciChart3DBuilder.dispose();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        surface = findViewById(R.id.chart3d);
        showFpsCounter = surface.getIsFpsCounterVisible();
    }

    @Override
    protected void onShowFpsCounterChanged(boolean showFpsCounter) {
        surface.setIsFpsCounterVisible(showFpsCounter);
    }
}
