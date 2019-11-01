//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2019. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// AccessibleSciChartSurface.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.testing.accessibility;

import android.content.Context;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.scichart.charting.visuals.SciChartSurface;

public class AccessibleSciChartSurface extends SciChartSurface {

    public final AccessibilityHelper helper = new AccessibilityHelper(this);

    public AccessibleSciChartSurface(Context context) {
        super(context);

        init();
    }

    public AccessibleSciChartSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AccessibleSciChartSurface(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        ViewCompat.setAccessibilityDelegate(this, helper);
    }

    @Override
    public boolean dispatchHoverEvent(MotionEvent event) {
        // Always attempt to dispatch hover events to accessibility first.
        if (helper.dispatchHoverEvent(event)) {
            return true;
        }

        return super.dispatchHoverEvent(event);
    }
}
