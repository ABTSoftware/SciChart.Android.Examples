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

import static com.scichart.examples.ExceptionActivity.EXCEPTION_MESSAGE_KEY;
import static com.scichart.examples.ExceptionActivity.STACK_TRACE_KEY;
import static com.scichart.examples.demo.DemoKeys.EXAMPLE_ID;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import com.scichart.examples.databinding.ActivityHomeBinding;
import com.scichart.examples.demo.CustomAdapter;
import com.scichart.examples.demo.DemoKeys;
import com.scichart.examples.demo.SortByCategory;
import com.scichart.examples.demo.SortByFeatures;
import com.scichart.examples.demo.SortByMostUsed;
import com.scichart.examples.demo.SortByName;
import com.scichart.examples.demo.components.SearchResultsDialog;
import com.scichart.examples.demo.helpers.Example;
import com.scichart.examples.demo.helpers.Module;
import com.scichart.examples.demo.search.ExampleSearchHelper;
import com.scichart.examples.demo.viewobjects.ExampleView;
import com.scichart.examples.utils.CustomListAlert;
import com.scichart.examples.utils.PermissionManager;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class HomeActivity extends AppCompatActivity implements Thread.UncaughtExceptionHandler,
        AdapterView.OnItemSelectedListener,
        AdapterView.OnItemClickListener,
        SearchResultsDialog.IOnSearchItemClickListener {
    private static final int EXAMPLE_REQUEST_CODE = 42;

    private Module module;
    private ExampleSearchHelper searchProvider;

    private Example example;

    private SearchResultsDialog searchResultsDialog;

    private String category = DemoKeys.CHARTS_2D;

    //    private Spinner sortBySpinner;
    private ListView listView;
    private CustomAdapter adapter;

    private TextView catTitle;
    //    private ImageView catIcon;
    private SearchView searchView;
    private MenuItem searchMenuItem;

    private int selectedSortId = 0;

    private ActivityHomeBinding binding;

    public HomeActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        module = SciChartApp.getInstance().getModule();
        searchProvider = new ExampleSearchHelper(this, module.getExamples());

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        if (savedInstanceState != null) {
            category = savedInstanceState.getString(DemoKeys.CATEGORY_ID);
        } else {
            final Bundle extras = getIntent().getExtras();
            if (extras != null) {
                category = extras.getString(DemoKeys.CATEGORY_ID);
            }
        }

        Toolbar toolbar = findViewById(R.id.appToolbar);
        setSupportActionBar(toolbar);

        final ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayShowHomeEnabled(true);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_home_24dp);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        catTitle = findViewById(R.id.category_title);
//        catIcon = findViewById(R.id.category_icon);

//        sortBySpinner = findViewById(R.id.sortBy);
//        sortBySpinner.setOnItemSelectedListener(HomeActivity.this);


        binding.contentHome.sortTextTv.setText(getString(R.string.sort_text_formatter, getString(R.string.category)));

        binding.contentHome.sortDropDownLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] list = getResources().getStringArray(R.array.sort_by);
                CustomListAlert.Companion.showAlert(
                        HomeActivity.this,
                        Arrays.asList(list),
                        selectedSortId,
                        new Function1<Integer, Unit>() {
                            @Override
                            public Unit invoke(Integer integer) {
                                selectedSortId = integer;
                                onSortSubmit();
                                binding.contentHome.sortTextTv.setText(getString(R.string.sort_text_formatter, list[integer]));
                                return null;
                            }
                        });
            }
        });


        searchResultsDialog = findViewById(R.id.search_results_dialog);
        searchResultsDialog.setOnSearchItemClickListener(this);
        searchResultsDialog.setData(searchProvider.dataAdapter);

        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
        adapter = new CustomAdapter(this);

//        sortBySpinner.setAdapter(new SpinnerStringAdapter(this, R.array.sort_by));

        updateLoadExamples();

        Thread.setDefaultUncaughtExceptionHandler(this);
    }



    private void onSortSubmit() {
        if (module == null) return;
        final List<Example> examples = module.getExamples();
        final List<Example> exampleItems = convertToAdapterItems(examples, category);
        switch (selectedSortId) {
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
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu_search, menu);

        final MenuItem isKotlinMenuItem = menu.findItem(R.id.isKotlin);
        isKotlinMenuItem.setOnMenuItemClickListener(menuItem -> {
            module.toggleKotlin();
            isKotlinMenuItem.setTitle(module.isKotlin() ? "Kotlin" : "Java");
            return true;
        });

        searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();
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

        final ImageView closeButton = searchView.findViewById(R.id.search_close_btn);
        closeButton.setBackgroundResource(R.drawable.ic_arrow_back);
        closeButton.setOnClickListener(v -> {
            searchResultsDialog.hide();
            searchView.setQuery("", false);
        });

        return true;
    }

    public void onSearchToolbarTextChanged(CharSequence charSequence) {
        if (charSequence.length() == 0) {
            searchResultsDialog.hide();
        } else {
            searchResultsDialog.show();

            searchProvider.query(charSequence.toString());
        }
    }


    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        handleUncaughtException(throwable);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(DemoKeys.CATEGORY_ID, category);
    }

    private static List<Example> convertToAdapterItems(List<Example> definitionsByCategory, String category) {
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

    public void updateLoadExamples() {
        final List<Example> examples = module.getExamples();

        final List<Example> exampleItems = convertToAdapterItems(examples, category);
        adapter.setSortStrategy(new SortByCategory(exampleItems));

        listView.setAdapter(adapter);
//        sortBySpinner.setSelection(0);

        catTitle.setText(module.getCategoryName(category));
//        catIcon.setImageDrawable(module.getIconForCategory(category));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (module == null) return;
        final List<Example> examples = module.getExamples();
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

        openFragment(selectedExample.title);
    }

    private void hideKeyboard() {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        final View currentFocus = getCurrentFocus();
        if (imm != null && currentFocus != null) {
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    private void openFragment(final String exampleId) {
        this.example = SciChartApp.getInstance().getModule().getExampleByTitle(exampleId);
        if (example != null) {
            if (searchMenuItem != null) {
                searchMenuItem.collapseActionView();
                hideKeyboard();
            }
            searchResultsDialog.hide();

            final boolean hasPermissions = PermissionManager.askForPermissions(this, EXAMPLE_REQUEST_CODE, example.permissions);
            if (hasPermissions) {
                startExampleActivity(example);
            }
        }
    }

    private void startExampleActivity(Example example) {
        final Class<?> activityType;

        final String category = example.topLevelCategory;
        if (category.toLowerCase(Locale.ROOT).contains("3d")) {
            activityType = Example3DActivity.class;
        } else if (category.toLowerCase(Locale.ROOT).contains("featured")) {
            activityType = ShowcaseActivity.class;
        } else {
            activityType = ExampleActivity.class;
        }

        Intent exampleActivity = new Intent(this, activityType);
        exampleActivity.putExtra(EXAMPLE_ID, example.title);

        startActivity(exampleActivity);
    }

    @Override
    public void onSearchItemClick(String exampleId) {
        openFragment(exampleId);
    }
}
