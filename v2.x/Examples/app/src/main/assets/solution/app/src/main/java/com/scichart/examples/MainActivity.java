//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2016. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// MainActivity.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************
package com.scichart.examples;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.scichart.charting.model.ChartModifierCollection;
import com.scichart.charting.modifiers.ZoomExtentsModifier;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.core.common.Action1;
import com.scichart.examples.components.CustomDrawerLayout;
import com.scichart.examples.components.modifiers.CustomRotateChartModifier;
import com.scichart.examples.components.modifiers.FlipAxesCoordsChartModifier;
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.examples.utils.SideMenuHelper;
import com.scichart.extensions.builders.SciChartBuilder;
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget;
import com.scichart.examples.utils.widgetgeneration.Widget;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private ExampleBaseFragment exampleFragment;

    private SciChartSurface surface;
    private CustomDrawerLayout drawerLayout;
    private ImageButton settingsButton;

    @Override
    protected void onCreate(Bundle bundle) {
        SciChartBuilder.init(this);

        super.onCreate(bundle);
        setContentView(R.layout.activity_main);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        TextView titleView = (TextView) findViewById(R.id.title);
        titleView.setText(getTitle().toString());

        setUpDrawerAndToolbar();

        settingsButton = (ImageButton) findViewById(R.id.toolbar_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View item) {
                drawerLayout.onMenuItemClick();
            }
        });

        drawerLayout.setViewsClickableAction(new Action1<Boolean>() {
            @Override
            public void execute(Boolean isEnabled) {
                settingsButton.setEnabled(isEnabled);
            }
        });

        exampleFragment = (ExampleBaseFragment) getFragmentManager().findFragmentById(R.id.fragment_id);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SciChartBuilder.dispose();
    }

    @Override
    protected void onPause() {
        super.onPause();

        drawerLayout.clearMenuItems();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        surface = (SciChartSurface) findViewById(R.id.chart);
        generateSideMenuItems();
    }

    private void setUpDrawerAndToolbar() {
        drawerLayout = (CustomDrawerLayout) findViewById(R.id.drawer_layout_example);
    }

    private void generateSideMenuItems() {
        List<Widget> exampleToolbarItems = exampleFragment.getToolbarItems();
        if (exampleToolbarItems.size() > 0) {
            addWidgetsToMenuList(exampleToolbarItems);
        }

        boolean showDefaultModifiers = exampleFragment.showDefaultModifiersInToolbar();
        if (showDefaultModifiers) {
            if (exampleToolbarItems.size() > 0) {
                drawerLayout.addMenuSeparator();
            }
            addWidgetsToMenuList(getDefaultModifiers());
        }

        if (exampleToolbarItems.size() == 0 && !showDefaultModifiers) {
            settingsButton.setVisibility(View.INVISIBLE);
        }
    }

    private void addWidgetsToMenuList(List<Widget> widgets) {
        for (int i = 0, size = widgets.size(); i < size; i++) {
            Widget widget = widgets.get(i);
            drawerLayout.addMenuItem(widget);
        }
    }

    private List<Widget> getDefaultModifiers() {
        List<Widget> widgets = new ArrayList<>();

        final ChartModifierCollection chartModifiers = surface.getChartModifiers();

        widgets.add(attachModifierAndCreateWidget(ZoomExtentsModifier.class, R.drawable.example_toolbar_zoom_extents, this.surface, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                surface.animateZoomExtents(ZoomExtentsModifier.DEFAULT_ANIMATION_DURATION);
            }
        }));
        widgets.add(attachModifierAndCreateWidget(FlipAxesCoordsChartModifier.class, R.drawable.example_toolbar_flip_x, this.surface, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FlipAxesCoordsChartModifier) SideMenuHelper.getModifier(FlipAxesCoordsChartModifier.class, chartModifiers)).flipXAxes();
            }
        }));
        widgets.add(attachModifierAndCreateWidget(FlipAxesCoordsChartModifier.class, R.drawable.example_toolbar_flip_y, this.surface, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FlipAxesCoordsChartModifier) SideMenuHelper.getModifier(FlipAxesCoordsChartModifier.class, chartModifiers)).flipYAxes();
            }
        }));
        widgets.add(attachModifierAndCreateWidget(CustomRotateChartModifier.class, R.drawable.example_toolbar_rotate, this.surface, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CustomRotateChartModifier) SideMenuHelper.getModifier(CustomRotateChartModifier.class, chartModifiers)).rotateChart();
            }
        }));

        return widgets;
    }

    private Widget attachModifierAndCreateWidget(Class modifierType, @DrawableRes int drawableRes, SciChartSurface surface, View.OnClickListener listener) {
        if (modifierType != null) {
            SideMenuHelper.attachModifierToSurface(modifierType, surface);
        }

        return new ImageViewWidget.Builder().setId(drawableRes).setListener(listener).build();
    }
}