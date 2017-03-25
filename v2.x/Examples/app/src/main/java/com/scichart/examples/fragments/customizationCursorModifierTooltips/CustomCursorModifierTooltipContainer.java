//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CustomCursorModifierTooltipContainer.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.customizationCursorModifierTooltips;

import android.content.Context;
import android.util.AttributeSet;

import com.scichart.charting.modifiers.behaviors.CursorModifierTooltip;
import com.scichart.charting.themes.IThemeProvider;

public class CustomCursorModifierTooltipContainer extends CursorModifierTooltip {
    public CustomCursorModifierTooltipContainer(Context context) {
        super(context);
    }

    public CustomCursorModifierTooltipContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomCursorModifierTooltipContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void applyThemeProvider(IThemeProvider themeProvider) {
    }
}