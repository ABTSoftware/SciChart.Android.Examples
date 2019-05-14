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

        setUpSciChartLicense();
    }

    private void setUpSciChartLicense() {
        // Set your license code here to license the SciChart Android Examples app
        // You can get a trial license key from https://www.scichart.com/licensing-scichart-android/
        // Purchased license keys can be viewed at https://www.scichart.com/profile
        //
        // e.g.
        //
        // com.scichart.charting.visuals.SciChartSurface.setRuntimeLicenseKey(
        //        "<LicenseContract>" +
        //                "<Customer>your-email@company.com</Customer>" +
        //                "<OrderId>Trial</OrderId>" +
        //                "<LicenseCount>1</LicenseCount>" +
        //                "<IsTrialLicense>true</IsTrialLicense>" +
        //                "<SupportExpires>12/21/2017 00:00:00</SupportExpires>" +
        //                "<ProductCode>SC-ANDROID-2D-ENTERPRISE-SRC</ProductCode>" +
        //                "<KeyCode>6ccc960b22b7b12360a141a7c2a89bce4e40.....09744b6c195022e9fa1ebcf9a0e78167cbaa8f9b8eee9221</KeyCode>" +
        //        "</LicenseContract>"
        // );

        try {
            com.scichart.charting.visuals.SciChartSurface.setRuntimeLicenseKey("<LicenseContract>\n" +
                    "  <Customer>aa</Customer>\n" +
                    "  <OrderId>bb</OrderId>\n" +
                    "  <LicenseCount>1</LicenseCount>\n" +
                    "  <IsTrialLicense>true</IsTrialLicense>\n" +
                    "  <SupportExpires>04/29/2019 00:00:00</SupportExpires>\n" +
                    "  <ProductCode>SC-ANDROID-2D-ENTERPRISE-SRC</ProductCode>\n" +
                    "  <KeyCode>10d505d21df6129e3c83dc9fc45e83532bc5702970e21621044430320ffa1b1659512b28af39a11c4c8064be795b12cf1de3d2e5157e132e37d459dc236c3a0bfe68e0480404370998111056d51f3615a5d23463ccc7180b4cfda4da8837907fcf2a3c7396a6a93e42ce0980029189b284570b4b560c7e4be8cd68f5590e2823d0804d54d922447fc817b08b2f2e223d7d640c27ac1e902c69</KeyCode>\n" +
                    "</LicenseContract>");
        } catch (Exception e) {
            Log.e("SciChart", "Error when setting the license", e);
        }
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
            return new ExampleSearchProvider(getApplicationContext(), module.getExamples());
        }
        return null;
    }
    //END_DEMO_APPLICATION
}
