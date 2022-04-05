//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2019. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ExampleActivityBase.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples;

import android.app.Dialog;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.scichart.examples.components.CustomDrawerLayout;
import com.scichart.examples.demo.DemoKeys;
import com.scichart.examples.demo.helpers.Example;
import com.scichart.examples.demo.helpers.Module;
import com.scichart.examples.demo.utils.Utils;
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.examples.utils.ViewSettingsUtil;
import com.scichart.examples.utils.widgetgeneration.ImageViewWidget;
import com.scichart.examples.utils.widgetgeneration.Widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ExampleActivityBase extends AppCompatActivity {

    private Example example;
    private ExampleBaseFragment<?> exampleFragment;

    private CustomDrawerLayout drawerLayout;

    protected boolean showFpsCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        final Window window = getWindow();

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // don't turn off screen when showing example
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setUpExample(savedInstanceState);
        setUpDrawerAndToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.example_menu_drawer, menu);

        final MenuItem menuItem = menu.findItem(R.id.toggle_example_toolbar);
        menuItem.setOnMenuItemClickListener(item -> {
            drawerLayout.onMenuItemClick();
            return true;
        });

        drawerLayout.setViewsClickableAction(menuItem::setEnabled);

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

        generateSideMenuItems();
    }

    private void setUpDrawerAndToolbar() {
        drawerLayout = findViewById(R.id.drawer_layout_example);

        Toolbar appToolbar = findViewById(R.id.appToolbar);
        if (appToolbar != null) {
            setSupportActionBar(appToolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            appToolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
    }

    private void setUpExample(Bundle savedInstanceState) {
        final String exampleId = getIntent().getStringExtra(DemoKeys.EXAMPLE_ID);
        final Module module = SciChartApp.getInstance().getModule();

        this.example = module.getExampleByTitle(exampleId);
        setTitle(example.title);

        FragmentManager fragmentManager = getSupportFragmentManager();
        exampleFragment = savedInstanceState != null
                ? (ExampleBaseFragment<?>) fragmentManager.findFragmentByTag(DemoKeys.FRAGMENT_TAG)
                : (ExampleBaseFragment<?>) Utils.createObject(module.getExampleFragmentPath(example));

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
            final List<Widget> defaultModifiers = getDefaultModifiers();
            if(!defaultModifiers.isEmpty()) {
                addWidgetsToMenuList(defaultModifiers);
                drawerLayout.addMenuSeparator();
            }
        }

        addWidgetsToMenuList(getAppWideWidgets());
    }

    private void addWidgetsToMenuList(List<Widget> widgets) {
        for (int i = 0, size = widgets.size(); i < size; i++) {
            Widget widget = widgets.get(i);
            drawerLayout.addMenuItem(widget);
        }
    }

    protected List<Widget> getDefaultModifiers() {
        return Collections.emptyList();
    }

    private List<Widget> getAppWideWidgets() {
        ArrayList<Widget> widgets = new ArrayList<>();

        widgets.add(new ImageViewWidget.Builder().setId(R.drawable.example_toolbar_show_code).setListener(v -> {
            final Module module = SciChartApp.getInstance().getModule();
            final String githubLink = module.getGitHubLink(example);

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(githubLink));
            startActivity(browserIntent);
        }).build());

        widgets.add(new ImageViewWidget.Builder().setId(R.drawable.ic_build_white_48px).setListener(v -> openDevModeSettingsDialog()).build());

        return widgets;
    }

    private void openDevModeSettingsDialog() {
        final Dialog dialog = ViewSettingsUtil.createSettingsPopup(this, R.layout.example_dev_mode_settings_popup_layout);

        ViewSettingsUtil.setUpSwitchCompat(dialog, R.id.show_fps_counter_checkbox, showFpsCounter, (buttonView, isChecked) -> {
            showFpsCounter = isChecked;

            onShowFpsCounterChanged(showFpsCounter);
        });

        dialog.show();
    }

    protected abstract void onShowFpsCounterChanged(boolean showFpsCounter);
}
