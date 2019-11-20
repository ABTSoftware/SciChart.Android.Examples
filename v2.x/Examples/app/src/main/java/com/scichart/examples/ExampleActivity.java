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

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.scichart.charting.model.ChartModifierCollection;
import com.scichart.charting.modifiers.ZoomExtentsModifier;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.core.common.Action1;
import com.scichart.examples.components.CustomDrawerLayout;
import com.scichart.examples.components.FpsDrawable;
import com.scichart.examples.components.modifiers.CustomRotateChartModifier;
import com.scichart.examples.components.modifiers.FlipAxesCoordsChartModifier;
import com.scichart.examples.demo.DemoKeys;
import com.scichart.examples.demo.helpers.Example;
import com.scichart.examples.demo.highlight.PrettifySourceCodeTask;
import com.scichart.examples.demo.utils.MailUtils;
import com.scichart.examples.demo.utils.Utils;
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.examples.utils.SideMenuHelper;
import com.scichart.examples.utils.ViewSettingsUtil;
import com.scichart.extensions.builders.SciChartBuilder;
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget;
import com.scichart.examples.utils.widgetgeneration.Widget;

import java.util.ArrayList;
import java.util.List;

public class ExampleActivity extends AppCompatActivity {
    private Example example;
    private ExampleBaseFragment exampleFragment;

    private SciChartSurface surface;
    private CustomDrawerLayout drawerLayout;

    private boolean showFpsCounter;
    private FpsDrawable fpsDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fpsDrawable = new FpsDrawable(this);

        SciChartBuilder.init(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setUpExample(savedInstanceState);
        setUpDrawerAndToolbar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SciChartBuilder.dispose();

        fpsDrawable.setTargets(null, null);
        fpsDrawable = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.example_menu_drawer, menu);

        final MenuItem menuItem = menu.findItem(R.id.toggle_example_toolbar);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                drawerLayout.onMenuItemClick();
                return true;
            }
        });

        drawerLayout.setViewsClickableAction(new Action1<Boolean>() {
            @Override
            public void execute(Boolean isEnabled) {
                menuItem.setEnabled(isEnabled);
            }
        });

        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        drawerLayout.clearMenuItems();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        surface = (SciChartSurface) exampleFragment.getView().findViewById(R.id.chart);
        generateSideMenuItems();
    }

    private void setUpDrawerAndToolbar() {
        drawerLayout = (CustomDrawerLayout) findViewById(R.id.drawer_layout_example);

        Toolbar appToolbar = (Toolbar) findViewById(R.id.appToolbar);
        if (appToolbar != null) {
            setSupportActionBar(appToolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            appToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    private void setUpExample(Bundle savedInstanceState) {
        final String exampleId = getIntent().getStringExtra(DemoKeys.EXAMPLE_ID);
        this.example = HomeActivity.getModule().getExampleByTitle(exampleId);

        setTitle(example.title);
        new PrettifySourceCodeTask(example).execute();

        FragmentManager fragmentManager = getFragmentManager();
        if (savedInstanceState != null) {
            exampleFragment = (ExampleBaseFragment) fragmentManager.findFragmentByTag(DemoKeys.FRAGMENT_TAG);
        } else {
            exampleFragment = (ExampleBaseFragment) Utils.createObject(example.fragment);
        }

        if (!exampleFragment.isInLayout()) {
            fragmentManager.beginTransaction().replace(R.id.fragment_container, exampleFragment, DemoKeys.FRAGMENT_TAG).commit();
            getSupportFragmentManager().executePendingTransactions();
        }
    }

    private void generateSideMenuItems() {
        List<Widget> exampleToolbarItems = exampleFragment.getToolbarItems();
        if (exampleToolbarItems.size() > 0) {
            addWidgetsToMenuList(exampleToolbarItems);
            drawerLayout.addMenuSeparator();
        }

        if (exampleFragment.showDefaultModifiersInToolbar()) {
            addWidgetsToMenuList(getDefaultModifiers());
            drawerLayout.addMenuSeparator();
        }

        addWidgetsToMenuList(getAppWideWidgets());
    }

    private void addWidgetsToMenuList(List<Widget> widgets) {
        for (int i = 0, size = widgets.size(); i < size; i++) {
            Widget widget = widgets.get(i);
            drawerLayout.addMenuItem(widget);
        }
    }

    private List<Widget> getDefaultModifiers() {
        List<Widget> widgets = new ArrayList<>();

        if (surface != null) {
            final ChartModifierCollection chartModifiers = surface.getChartModifiers();

            widgets.add(attachModifierAndCreateWidget(null, R.drawable.example_toolbar_zoom_extents, this.surface, new View.OnClickListener() {
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
        }
        return widgets;
    }

    private Widget attachModifierAndCreateWidget(Class modifierType, @DrawableRes int drawableRes, SciChartSurface surface, View.OnClickListener listener) {
        if (modifierType != null) {
            SideMenuHelper.attachModifierToSurface(modifierType, surface);
        }

        return new ImageViewWidget.Builder().setId(drawableRes).setListener(listener).build();
    }

    private List<Widget> getAppWideWidgets() {
        ArrayList<Widget> widgets = new ArrayList<>();

        widgets.add(new ImageViewWidget.Builder().setId(R.drawable.example_toolbar_show_code).setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(ExampleActivity.this, ShowCodeActivity.class);
                intent.putExtra(DemoKeys.EXAMPLE_ID, example.title);
                startActivity(intent);
            }
        }).build());

        widgets.add(new ImageViewWidget.Builder().setId(R.drawable.ic_share_white).setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MailUtils.trySendExampleByMail(ExampleActivity.this, example, (new int[]{}));
            }
        }).build());

        widgets.add(new ImageViewWidget.Builder().setId(R.drawable.ic_build_white_48px).setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDevModeSettingsDialog();
            }
        }).build());

        return widgets;
    }

    private void openDevModeSettingsDialog() {
        final Dialog dialog = ViewSettingsUtil.createSettingsPopup(this, R.layout.example_dev_mode_settings_popup_layout);

        ViewSettingsUtil.setUpSwitchCompat(dialog, R.id.show_fps_counter_checkbox, showFpsCounter, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showFpsCounter = isChecked;
                if (showFpsCounter) {
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
        });

        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MailUtils.EMAIL_PERMISSIONS_REQUEST) {
            MailUtils.trySendEmail(this, example, grantResults);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}