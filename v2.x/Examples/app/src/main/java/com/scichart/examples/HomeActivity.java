//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// HomeActivity.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.scichart.examples.components.SpinnerStringAdapter;
import com.scichart.examples.demo.CustomAdapter;
import com.scichart.examples.demo.SortByCategory;
import com.scichart.examples.demo.SortByFeatures;
import com.scichart.examples.demo.SortByMostUsed;
import com.scichart.examples.demo.SortByName;
import com.scichart.examples.demo.components.SearchResultsDialog;
import com.scichart.examples.demo.helpers.Example;
import com.scichart.examples.demo.helpers.Module;
import com.scichart.examples.demo.search.ExampleSearchProvider;
import com.scichart.examples.demo.viewobjects.ExampleSearchView;
import com.scichart.examples.demo.viewobjects.ExampleView;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.scichart.examples.ExceptionActivity.EXCEPTION_MESSAGE_KEY;
import static com.scichart.examples.ExceptionActivity.STACK_TRACE_KEY;
import static com.scichart.examples.demo.DemoKeys.EXAMPLE_ID;
import static com.scichart.examples.demo.DemoKeys.SELECTED_POSITION;
import static com.scichart.examples.demo.viewobjects.ExampleSearchView.createExampleSearchView;

public class HomeActivity extends AppCompatActivity implements Thread.UncaughtExceptionHandler,
        AdapterView.OnItemSelectedListener,
        AdapterView.OnItemClickListener,
        SearchResultsDialog.IOnCloseDialogListener,
        SearchResultsDialog.IOnSearchItemClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    private static final int OPEN_EXAMPLE_REQUEST = 199;

    private static Module module;
    private LinearLayout mainScreen;
    private ExampleSearchProvider searchProvider;
    private SearchResultsDialog searchResultsDialog;

    private Spinner sortBySpinner;
    private ListView listView;
    private CustomAdapter adapter;

    private int selectedPosition = 0; // a2D_Charts category
    private List<String> categories;

    private String exampleId;

    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        if (savedInstanceState != null) {
            selectedPosition = savedInstanceState.getInt(SELECTED_POSITION);
            exampleId = savedInstanceState.getString(EXAMPLE_ID);
        }

        progress = new ProgressDialog(this, R.style.SciChart_ExportProgressDialogStyle);
        progress.setTitle("");
        progress.setMessage("Loading data...");
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        toolbar = (Toolbar) findViewById(R.id.appToolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navMenu = navigationView.getMenu();
        catTitle = (TextView) findViewById(R.id.category_title);
        catIcon = (ImageView) findViewById(R.id.category_icon);

        initLayout(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    Menu navMenu;
    TextView catTitle;
    ImageView catIcon;
    Toolbar toolbar;
    NavigationView navigationView;
    SearchView searchView;
    MenuItem searchMenuItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu_search, menu);

        searchMenuItem = menu.findItem(R.id.action_search);
        searchMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                tryCloseDrawer();
                return false;
            }
        });

        searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                onSearchToolbarTextChanged(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                onSearchToolbarTextChanged(newText);
                return true;
            }
        });

        ImageView closeButton = (ImageView) searchView.findViewById(R.id.search_close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchResultsDialog.hide();
                searchView.setQuery("", false);
            }
        });

        return true;
    }

    private boolean tryCloseDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        boolean isOpen = drawer.isDrawerOpen(GravityCompat.START);
        if (isOpen) {
            drawer.closeDrawer(GravityCompat.START);
        }

        return isOpen;
    }

    @Override
    public void onBackPressed() {
        if (!tryCloseDrawer()) {
            super.onBackPressed();
        }
    }

    private void initLayout(final Bundle savedInstanceState) {
        mainScreen = (LinearLayout) findViewById(R.id.mainScreen);

        sortBySpinner = (Spinner) findViewById(R.id.sortBy);
        sortBySpinner.setOnItemSelectedListener(HomeActivity.this);

        searchResultsDialog = (SearchResultsDialog) findViewById(R.id.search_results_dialog);
        searchResultsDialog.setOnCloseListener(this);
        searchResultsDialog.setOnSearchItemClickListener(this);

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
        adapter = new CustomAdapter(this);

        // set stub search provider
        searchProvider = new ExampleSearchProvider();

        final SpinnerStringAdapter spinnerStringAdapter = new SpinnerStringAdapter(this, R.array.sort_by);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                module = SciChartApp.getInstance().getModule();
                searchProvider = SciChartApp.getInstance().getSearchProvider(module);
                categories = module.getCategories();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                sortBySpinner.setAdapter(spinnerStringAdapter);

                for (int i = 0; i < categories.size(); ++i) {
                    String category = categories.get(i);

                    MenuItem menuItem = navMenu.add(Menu.NONE, View.generateViewId(), Menu.NONE, module.getCategoryName(category));

                    menuItem.setTitleCondensed(category);
                    menuItem.setIcon(module.getIconForCategory(category));
                    menuItem.setCheckable(true);

                    menuItem.setChecked(i == selectedPosition);
                }

                synchronized (HomeActivity.getModule().getExamples()) {
                    openFragment(exampleId, savedInstanceState);
                }

                updateLoadExamples(selectedPosition);

                mainScreen.setVisibility(View.VISIBLE);
                hideProgressBar();
            }
        }.execute();
    }

    private void hideProgressBar() {
        if (progress != null && progress.isShowing()) {
            progress.hide();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideProgressBar();
    }

    public void onSearchToolbarTextChanged(CharSequence charSequence) {
        if (charSequence.length() == 0) {
            searchResultsDialog.hide();
        } else {
            searchResultsDialog.show();
            final Set<UUID> ids = searchProvider.query(charSequence.toString());
            final List<ExampleSearchView> result = matchExamplesAndIds(module.getExamples(), ids);
            searchResultsDialog.setInputString(charSequence.toString());
            searchResultsDialog.setData(result);
        }
    }

    private List<ExampleSearchView> matchExamplesAndIds(List<Example> examples, Set<UUID> ids) {
        final List<ExampleSearchView> result = new ArrayList<>();
        for (Example example : examples) {
            if (ids.contains(example.id)) {
                result.add(createExampleSearchView(example));
            }
        }
        return result;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        handleUncaughtException(throwable);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_POSITION, selectedPosition);
        outState.putString(EXAMPLE_ID, exampleId);
    }

    private List<Example> convertToAdapterItems(List<Example> definitionsByCategory, String category) {
        final List<Example> result = new ArrayList<>();
        for (Example example : definitionsByCategory) {
            if (example.topLevelCategory.equals(category)) {
                result.add(example);
            }
        }
        return result;
    }

    private void handleUncaughtException(Throwable e) {
        Intent intent = new Intent(this, ExceptionActivity.class);
        intent.putExtra(EXCEPTION_MESSAGE_KEY, e.toString());
        intent.putExtra(STACK_TRACE_KEY, getStackTrace(e));
        startActivity(intent);
        e.printStackTrace();
        System.exit(1);
    }

    private String getStackTrace(Throwable e) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);

        e.printStackTrace(pw);

        return sw.toString();
    }

    public void updateLoadExamples(int selectedCategory) {
        final String category = getCategoryByPosition(selectedCategory);
        final List<Example> examples = module.getExamples();

        final List<Example> exampleItems = convertToAdapterItems(examples, category);
        adapter.setSortStrategy(new SortByCategory(exampleItems));

        listView.setAdapter(adapter);
        sortBySpinner.setSelection(0);

        catTitle.setText(module.getCategoryName(category));
        catIcon.setImageDrawable(module.getIconForCategory(category));
    }

    private String getCategoryByPosition(int selectedPosition) {
        return categories.size() > 0 ? categories.get(selectedPosition) : "";
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (module == null) return;
        final List<Example> examples = module.getExamples();
        final String category = getCategoryByPosition(selectedPosition);
        final List<Example> exampleItems = convertToAdapterItems(examples, category);
        switch (position) {
            case 0:
                adapter.setSortStrategy(new SortByCategory(exampleItems));
                break;
            case 1:
                adapter.setSortStrategy(new SortByFeatures(exampleItems));
                break;
            case 2:
                adapter.setSortStrategy(new SortByName(exampleItems));
                break;
            case 3:
                adapter.setSortStrategy(new SortByMostUsed(exampleItems));
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final ExampleView selectedExample = (ExampleView) adapter.getItem(position);
        exampleId = selectedExample.title;

        openFragment(exampleId, null);
    }

    private void hideKeyboard() {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        final View currentFocus = getCurrentFocus();
        if(imm != null && currentFocus != null)
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
    }

    private void openFragment(final String exampleId, final Bundle savedInstanceState) {
        final Example example = HomeActivity.getModule().getExampleByTitle(exampleId);
        if (example != null) {
            progress.show();

            if (searchMenuItem != null) {
                searchMenuItem.collapseActionView();
                hideKeyboard();
            }
            searchResultsDialog.hide();

            Intent exampleActivity = new Intent(this, ExampleActivity.class);
            exampleActivity.putExtra(EXAMPLE_ID, exampleId);
            startActivityForResult(exampleActivity, OPEN_EXAMPLE_REQUEST);

            this.exampleId = "";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OPEN_EXAMPLE_REQUEST) {
            // Navigated back from ExampleActivity
            hideProgressBar();
        }
    }

    public static Module getModule() {
        return SciChartApp.getInstance().getModule();
    }

    @Override
    public void onClose() {
        searchView.clearFocus();

    }

    @Override
    public void onSearchItemClick(String exampleId) {
        this.exampleId = exampleId;
        openFragment(exampleId, null);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        String category = String.valueOf(item.getTitleCondensed());

        navigationView.setCheckedItem(item.getItemId());

        selectedPosition = categories.indexOf(category);
        updateLoadExamples(selectedPosition);

        tryCloseDrawer();

        return true;
    }

    public String getExampleId() {
        return exampleId;
    }

    public void setExampleId(String exampleId) {
        this.exampleId = exampleId;
    }

}
