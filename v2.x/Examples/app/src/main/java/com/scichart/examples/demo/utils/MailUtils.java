//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// MailUtils.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.demo.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.scichart.examples.SciChartApp;
import com.scichart.examples.demo.export.ZipAndShareAllProjectsTask;
import com.scichart.examples.demo.export.ZipAndShareTask;
import com.scichart.examples.demo.helpers.Example;

public class MailUtils {

    public static final int EMAIL_PERMISSIONS_REQUEST = 12;

    private static final String[] requiredPermissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void trySendExampleByMail(Activity activity, Example example, @NonNull int[] grantResults) {
        if (askForPermissions(activity, requiredPermissions)) {
            trySendEmail(activity, example, (new int[]{}));
        }
    }

    public static void trySendEmail(Activity activity, Example example, @NonNull int[] grantResults) {
        boolean isAllowed = true;

        for (int result : grantResults) {
            isAllowed &= result == PackageManager.PERMISSION_GRANTED;
        }

        if (isAllowed) {
            new ZipAndShareTask(activity, example).execute();
        } else {
            Toast.makeText(activity, "Cannot send an email with an attachment without permissions granted.", Toast.LENGTH_LONG).show();
        }
    }

    private static boolean askForPermissions(Activity activity, @NonNull String[] permissions) {
        boolean hasPermissions = true;

        for (String permission : permissions) {
            hasPermissions &= ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
        }

        if (!hasPermissions) {
            ActivityCompat.requestPermissions(activity, permissions, EMAIL_PERMISSIONS_REQUEST);
        }

        return hasPermissions;
    }
}
