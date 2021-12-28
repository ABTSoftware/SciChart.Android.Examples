//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2021. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CustomCursorModifierTooltipContainer.java is part of SCICHART®, High Performance Scientific Charts
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

package com.scichart.examples.fragments.examples2d.tooltipsAndHitTest;

import android.content.Context;
import android.util.AttributeSet;

import com.scichart.charting.modifiers.behaviors.CursorModifierTooltip;
import com.scichart.charting.themes.IThemeProvider;

public  class CustomCursorModifierTooltipContainer extends CursorModifierTooltip {
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
