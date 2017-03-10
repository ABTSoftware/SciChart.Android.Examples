//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SciChartApp.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************
package com.scichart.examples;
import android.app.Application;
import android.util.Log;
//BEGIN_DEMO_APPLICATION
import com.scichart.examples.demo.helpers.Module;
import com.scichart.examples.demo.search.ExampleSearchProvider;
//END_DEMO_APPLICATION
public class SciChartApp extends Application {
    private static SciChartApp sInstance;
    public static SciChartApp getInstance() {
        return sInstance;
    }
    //BEGIN_DEMO_APPLICATION
    private Module module;
    private ExampleSearchProvider searchProvider;
    //END_DEMO_APPLICATION
    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }
    //BEGIN_DEMO_APPLICATION
    public Module getModule() {
        if (module == null) {
            module = initModule();
        }
        return module;
    }
    public ExampleSearchProvider getSearchProvider(Module module) {
        if (searchProvider == null) {
            searchProvider = initSearchProvider(module);
        }
        return searchProvider;
    }
    private Module initModule() {
        final Module module = new Module(getApplicationContext());
        module.initialize();
        return module;
    }
    private ExampleSearchProvider initSearchProvider(Module module) {
        if (module != null) {
            final ExampleSearchProvider searchProvider = new ExampleSearchProvider(getApplicationContext());
            searchProvider.initSearchProvider(module.getExamples());
            return searchProvider;
        }
        return null;
    }
    //END_DEMO_APPLICATION
}
